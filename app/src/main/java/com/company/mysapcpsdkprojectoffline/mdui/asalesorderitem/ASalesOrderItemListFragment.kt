package com.company.mysapcpsdkprojectoffline.mdui.asalesorderitem

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.WorkerThread
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.CheckBox
import android.widget.ImageView
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.viewmodel.EntityViewModelFactory
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesorderitemtype.ASalesOrderItemTypeViewModel
import com.company.mysapcpsdkprojectoffline.repository.OperationResult
import com.company.mysapcpsdkprojectoffline.mdui.UIConstants
import com.company.mysapcpsdkprojectoffline.mdui.EntityKeyUtil
import com.company.mysapcpsdkprojectoffline.mdui.EntitySetListActivity.EntitySetName
import com.company.mysapcpsdkprojectoffline.mdui.EntitySetListActivity
import com.company.mysapcpsdkprojectoffline.mdui.InterfacedFragment
import com.sap.cloud.android.odata.api_sales_order_srv_entities.API_SALES_ORDER_SRV_EntitiesMetadata.EntitySets
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderItemType
import com.sap.cloud.mobile.fiori.`object`.ObjectCell
import com.sap.cloud.mobile.fiori.`object`.ObjectHeader
import com.sap.cloud.mobile.odata.EntityValue
import kotlinx.android.synthetic.main.element_entityitem_list.view.*
import org.slf4j.LoggerFactory

/**
 * An activity representing a list of ASalesOrderItemType. This activity has different presentations for handset and tablet-size
 * devices. On handsets, the activity presents a list of items, which when touched, lead to a view representing
 * ASalesOrderItemType details. On tablets, the activity presents the list of ASalesOrderItemType and ASalesOrderItemType details side-by-side using two
 * vertical panes.
 */

class ASalesOrderItemListFragment : InterfacedFragment<ASalesOrderItemType>() {

    /**
     * List adapter to be used with RecyclerView containing all instances of aSalesOrderItem
     */
    private var adapter: ASalesOrderItemTypeListAdapter? = null

    private lateinit var refreshLayout: SwipeRefreshLayout
    private var actionMode: ActionMode? = null
    private var isInActionMode: Boolean = false
    private val selectedItems = ArrayList<Int>()

    /**
     * View model of the entity type
     */
    private lateinit var viewModel: ASalesOrderItemTypeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTitle = getString(EntitySetListActivity.EntitySetName.ASalesOrderItem.titleId)
        menu = R.menu.itemlist_menu
        setHasOptionsMenu(true)
        savedInstanceState?.let {
            isInActionMode = it.getBoolean("ActionMode")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        currentActivity.findViewById<ObjectHeader>(R.id.objectHeader)?.let {
            it.visibility = View.GONE
        }
        return inflater.inflate(R.layout.fragment_entityitem_list, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(this.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_refresh -> {
                refreshLayout.isRefreshing = true
                refreshListData()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("ActionMode", isInActionMode)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        currentActivity.setTitle(activityTitle)

        (currentActivity.findViewById<RecyclerView>(R.id.item_list))?.let {
            this.adapter = ASalesOrderItemTypeListAdapter(currentActivity, it)
            it.adapter = this.adapter
        } ?: throw AssertionError()

        setupRefreshLayout()
        refreshLayout.isRefreshing = true

        navigationPropertyName = currentActivity.intent.getStringExtra("navigation")
        parentEntityData = currentActivity.intent.getParcelableExtra("parent")

        (currentActivity.findViewById<FloatingActionButton>(R.id.fab))?.let {
            if (navigationPropertyName != null && parentEntityData != null) {
                it.hide()
            } else {
                it.setOnClickListener {
                    listener?.onFragmentStateChange(UIConstants.EVENT_CREATE_NEW_ITEM, null)
                }
            }
        }

        prepareViewModel()
    }

    override fun onResume() {
        super.onResume()
        refreshListData()
    }

    /** Initializes the view model and add observers on it */
    private fun prepareViewModel() {
        viewModel = if( navigationPropertyName != null && parentEntityData != null ) {
            ViewModelProvider(currentActivity, EntityViewModelFactory(currentActivity.application, navigationPropertyName!!, parentEntityData!!))
                .get(ASalesOrderItemTypeViewModel::class.java)
        } else {
            ViewModelProvider(currentActivity).get(ASalesOrderItemTypeViewModel::class.java).also {
                it.initialRead{errorMessage ->
                    showError(errorMessage)
                }
            }
        }
        viewModel.observableItems.observe(viewLifecycleOwner, Observer<List<ASalesOrderItemType>> { items ->
            items?.let { entityList ->
                adapter?.let { listAdapter ->
                    listAdapter.setItems(entityList)

                    var item = viewModel.selectedEntity.value?.let { containsItem(entityList, it) }
                    if (item == null) {
                        item = if (entityList.isEmpty()) null else entityList[0]
                    }

                    item?.let {
                        viewModel.inFocusId = listAdapter.getItemIdForASalesOrderItemType(it)
                        if (currentActivity.resources.getBoolean(R.bool.two_pane)) {
                            viewModel.setSelectedEntity(it)
                            if(!isInActionMode && !(currentActivity as ASalesOrderItemActivity).isNavigationDisabled) {
                                listener?.onFragmentStateChange(UIConstants.EVENT_ITEM_CLICKED, it)
                            }
                        }
                        listAdapter.notifyDataSetChanged()
                    }

                    if( item == null ) hideDetailFragment()
                }

                refreshLayout.isRefreshing = false
            }
        })

        viewModel.readResult.observe(this, Observer {
            if (refreshLayout.isRefreshing) {
                refreshLayout.isRefreshing = false
            }
        })

        viewModel.deleteResult.observe( this, Observer {
            this.onDeleteComplete(it!!)
        })
    }

    /**
     * Checks if [item] exists in the list [items] based on the item id, which in offline is the read readLink,
     * while for online the primary key.
     */
    private fun containsItem(items: List<ASalesOrderItemType>, item: ASalesOrderItemType) : ASalesOrderItemType? {
        return items.find { entry ->
            adapter?.getItemIdForASalesOrderItemType(entry) == adapter?.getItemIdForASalesOrderItemType(item)
        }
    }

    /** when no items return from server, hide the detail fragment on tablet */
    private fun hideDetailFragment() {
        currentActivity.supportFragmentManager.findFragmentByTag(UIConstants.DETAIL_FRAGMENT_TAG)?.let {
            currentActivity.supportFragmentManager.beginTransaction()
                .remove(it).commit()
        }
        secondaryToolbar?.let {
            it.menu.clear()
            it.setTitle("")
        }
        currentActivity.findViewById<ObjectHeader>(R.id.objectHeader)?.let {
            it.visibility = View.GONE
        }
    }

    /** Completion callback for delete operation  */
    private fun onDeleteComplete(result: OperationResult<ASalesOrderItemType>) {
        progressBar?.let {
            it.visibility = View.INVISIBLE
        }
        viewModel.removeAllSelected()
        actionMode?.let {
            it.finish()
            isInActionMode = false
        }

        result.error?.let {
            handleDeleteError()
            return
        }
        refreshListData()
    }

    /** Handles the deletion error */
    private fun handleDeleteError() {
        showError(resources.getString(R.string.delete_failed_detail))
        refreshLayout.isRefreshing = false
    }

    /** sets up the refresh layout */
    private fun setupRefreshLayout() {
        refreshLayout = currentActivity.findViewById(R.id.swiperefresh)
        refreshLayout.setColorSchemeColors(UIConstants.FIORI_STANDARD_THEME_GLOBAL_DARK_BASE)
        refreshLayout.setProgressBackgroundColorSchemeColor(UIConstants.FIORI_STANDARD_THEME_BACKGROUND)
        refreshLayout.setOnRefreshListener(this::refreshListData)
    }

    /** Refreshes the list data */
    internal fun refreshListData() {
        navigationPropertyName?.let { _navigationPropertyName ->
            parentEntityData?.let { _parentEntityData ->
                viewModel.refresh(_parentEntityData as EntityValue, _navigationPropertyName)
            }
        } ?: run {
            viewModel.refresh()
        }
        adapter?.notifyDataSetChanged()
    }

    /** Sets the id for the selected item into view model */
    private fun setItemIdSelected(itemId: Int): ASalesOrderItemType? {
        viewModel.observableItems.value?.let { aSalesOrderItem ->
            if (aSalesOrderItem.isNotEmpty()) {
                adapter?.let {
                    viewModel.inFocusId = it.getItemIdForASalesOrderItemType(aSalesOrderItem[itemId])
                    return aSalesOrderItem[itemId]
                }
            }
        }
        return null
    }

    /** Sets the detail image for the given [viewHolder] */
    private fun setDetailImage(viewHolder: ASalesOrderItemTypeListAdapter.ViewHolder, aSalesOrderItemTypeEntity: ASalesOrderItemType?) {
        if (isInActionMode) {
            val drawable: Int = if (viewHolder.isSelected) {
                R.drawable.ic_check_circle_black_24dp
            } else {
                R.drawable.ic_uncheck_circle_black_24dp
            }
            viewHolder.objectCell.prepareDetailImageView().scaleType = ImageView.ScaleType.FIT_CENTER
            viewHolder.objectCell.detailImage = currentActivity.getDrawable(drawable)
        } else {
            if (!viewHolder.masterPropertyValue.isNullOrEmpty()) {
                viewHolder.objectCell.detailImageCharacter = viewHolder.masterPropertyValue?.substring(0, 1)
            } else {
                viewHolder.objectCell.detailImageCharacter = "?"
            }
        }
    }

    /**
     * Represents the listener to start the action mode. 
     */
    inner class OnActionModeStartClickListener(internal var holder: ASalesOrderItemTypeListAdapter.ViewHolder) : View.OnClickListener, View.OnLongClickListener {

        override fun onClick(view: View) {
            onAnyKindOfClick()
        }

        override fun onLongClick(view: View): Boolean {
            return onAnyKindOfClick()
        }

        /** callback function for both normal and long click of an entity */
        private fun onAnyKindOfClick(): Boolean {
            val isNavigationDisabled = (activity as ASalesOrderItemActivity).isNavigationDisabled
            if (isNavigationDisabled) {
                Toast.makeText(activity, "Please save your changes first...", Toast.LENGTH_LONG).show()
            } else {
                if (!isInActionMode) {
                    actionMode = (currentActivity as AppCompatActivity).startSupportActionMode(ASalesOrderItemListActionMode())
                    adapter?.notifyDataSetChanged()
                }
                holder.isSelected = !holder.isSelected
            }
            return true
        }
    }

    /**
     * Represents list action mode.
     */
    inner class ASalesOrderItemListActionMode : ActionMode.Callback {
        override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
            isInActionMode = true
            (currentActivity.findViewById<FloatingActionButton>(R.id.fab))?.let {
                it.hide()
            }
            //(currentActivity as ASalesOrderItemActivity).onSetActionModeFlag(isInActionMode)
            val inflater = actionMode.menuInflater
            inflater.inflate(R.menu.itemlist_view_options, menu)

            hideDetailFragment()
            return true
        }

        override fun onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(actionMode: ActionMode, menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.update_item -> {
                    val aSalesOrderItemTypeEntity = viewModel.getSelected(0)
                    if (viewModel.numberOfSelected() == 1 && aSalesOrderItemTypeEntity != null) {
                        isInActionMode = false
                        actionMode.finish()
                        viewModel.setSelectedEntity(aSalesOrderItemTypeEntity)
                        if(currentActivity.resources.getBoolean(R.bool.two_pane)) {
                            //make sure 'view' is under 'crt/update',
                            //so after done or back, the right panel has things to view
                            listener?.onFragmentStateChange(UIConstants.EVENT_ITEM_CLICKED, aSalesOrderItemTypeEntity)
                        }
                        listener?.onFragmentStateChange(UIConstants.EVENT_EDIT_ITEM, aSalesOrderItemTypeEntity)
                    }
                    true
                }
                R.id.delete_item -> {
                    listener?.onFragmentStateChange(UIConstants.EVENT_ASK_DELETE_CONFIRMATION,null)
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(actionMode: ActionMode) {
            isInActionMode = false
            if (!(navigationPropertyName != null && parentEntityData != null)) {
                (currentActivity.findViewById<FloatingActionButton>(R.id.fab))?.let {
                    it.show()
                }
            }
            selectedItems.clear()
            viewModel.removeAllSelected()

            //if in big screen, make sure one item is selected.
            refreshListData()
        }
    }

    /**
    * List adapter to be used with RecyclerView. It contains the set of aSalesOrderItem.
    */
    inner class ASalesOrderItemTypeListAdapter(private val context: Context, private val recyclerView: RecyclerView) : RecyclerView.Adapter<ASalesOrderItemTypeListAdapter.ViewHolder>() {

        /** Entire list of ASalesOrderItemType collection */
        private var aSalesOrderItem: MutableList<ASalesOrderItemType> = ArrayList()

        /** Flag to indicate whether we have checked retained selected aSalesOrderItem */
        private var checkForSelectedOnCreate = false

        init {
            setHasStableIds(true)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ASalesOrderItemTypeListAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.element_entityitem_list, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return aSalesOrderItem.size
        }

        override fun getItemId(position: Int): Long {
            return getItemIdForASalesOrderItemType(aSalesOrderItem[position])
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            checkForRetainedSelection()

            val aSalesOrderItemTypeEntity = aSalesOrderItem[holder.adapterPosition]
            (aSalesOrderItemTypeEntity.getDataValue(ASalesOrderItemType.higherLevelItem))?.let {
                holder.masterPropertyValue = it.toString()
            }
            populateObjectCell(holder, aSalesOrderItemTypeEntity)

            val isActive = getItemIdForASalesOrderItemType(aSalesOrderItemTypeEntity) == viewModel.inFocusId
            if (isActive) {
                setItemIdSelected(holder.adapterPosition)
            }
            val isASalesOrderItemTypeSelected = viewModel.selectedContains(aSalesOrderItemTypeEntity)
            setViewBackground(holder.objectCell, isASalesOrderItemTypeSelected, isActive)

            holder.view.setOnLongClickListener(OnActionModeStartClickListener(holder))
            setOnClickListener(holder, aSalesOrderItemTypeEntity)

            setOnCheckedChangeListener(holder, aSalesOrderItemTypeEntity)
            holder.isSelected = isASalesOrderItemTypeSelected
            setDetailImage(holder, aSalesOrderItemTypeEntity)
        }

        /**
        * Check to see if there are an retained selected aSalesOrderItemTypeEntity on start.
        * This situation occurs when a rotation with selected aSalesOrderItem is triggered by user.
        */
        private fun checkForRetainedSelection() {
            if (!checkForSelectedOnCreate) {
                checkForSelectedOnCreate = true
                if (viewModel.numberOfSelected() > 0) {
                    manageActionModeOnCheckedTransition()
                }
            }
        }

        /**
        * Computes a stable ID for each ASalesOrderItemType object for use to locate the ViewHolder
        *
        * @param [aSalesOrderItemTypeEntity] to get the items for
        * @return an ID based on the primary key of ASalesOrderItemType
        */
        internal fun getItemIdForASalesOrderItemType(aSalesOrderItemTypeEntity: ASalesOrderItemType): Long {
            return aSalesOrderItemTypeEntity.readLink.hashCode().toLong()
        }

        /**
        * Start Action Mode if it has not been started
        *
        * This is only called when long press action results in a selection. Hence action mode may not have been
        * started. Along with starting action mode, title will be set. If this is an additional selection, adjust title
        * appropriately.
        */
        private fun manageActionModeOnCheckedTransition() {
            if (actionMode == null) {
                actionMode = (activity as AppCompatActivity).startSupportActionMode(ASalesOrderItemListActionMode())
            }
            if (viewModel.numberOfSelected() > 1) {
                actionMode?.menu?.findItem(R.id.update_item)?.isVisible = false
            }
            actionMode?.title = viewModel.numberOfSelected().toString()
        }

        /**
        * This is called when one of the selected aSalesOrderItem has been de-selected
        *
        * On this event, we will determine if update action needs to be made visible or action mode should be
        * terminated (no more selected)
        */
        private fun manageActionModeOnUncheckedTransition() {
            when (viewModel.numberOfSelected()) {
                1 -> actionMode?.menu?.findItem(R.id.update_item)?.isVisible = true
                0 -> {
                    actionMode?.finish()
                    actionMode = null
                    return
                }
            }
            actionMode?.title = viewModel.numberOfSelected().toString()
        }

        private fun populateObjectCell(viewHolder: ViewHolder, aSalesOrderItemTypeEntity: ASalesOrderItemType) {

            val dataValue = aSalesOrderItemTypeEntity.getDataValue(ASalesOrderItemType.higherLevelItem)
            var masterPropertyValue: String? = null
            if (dataValue != null) {
                masterPropertyValue = dataValue.toString()
            }
            viewHolder.objectCell.apply {
                headline = masterPropertyValue
                detailImage = null
                subheadline = "Subheadline goes here"
                footnote = "Footnote goes here"
                when {
                aSalesOrderItemTypeEntity.inErrorState -> setIcon(R.drawable.ic_error_state, 0, R.string.error_state)
                aSalesOrderItemTypeEntity.isUpdated -> setIcon(R.drawable.ic_updated_state, 0, R.string.updated_state)
                aSalesOrderItemTypeEntity.isLocal -> setIcon(R.drawable.ic_local_state, 0, R.string.local_state)
                else -> setIcon(R.drawable.ic_download_state, 0, R.string.download_state)
                }
                setIcon(R.drawable.default_dot, 1, R.string.attachment_item_content_desc)
                setIcon("!", 2)
            }
        }

        private fun processClickAction(viewHolder: ViewHolder, aSalesOrderItemTypeEntity: ASalesOrderItemType) {
            resetPreviouslyClicked()
            setViewBackground(viewHolder.objectCell, false, true)
            viewModel.inFocusId = getItemIdForASalesOrderItemType(aSalesOrderItemTypeEntity)
        }

        /**
        * Attempt to locate previously clicked view and reset its background
        * Reset view model's inFocusId
        */
        private fun resetPreviouslyClicked() {
            (recyclerView.findViewHolderForItemId(viewModel.inFocusId) as ViewHolder?)?.let {
                setViewBackground(it.objectCell, it.isSelected, false)
            } ?: run {
                viewModel.refresh()
            }
        }

        /**
        * If there are selected aSalesOrderItem via long press, clear them as click and long press are mutually exclusive
        * In addition, since we are clearing all selected aSalesOrderItem via long press, finish the action mode.
        */
        private fun resetSelected() {
            if (viewModel.numberOfSelected() > 0) {
                viewModel.removeAllSelected()
                if (actionMode != null) {
                    actionMode?.finish()
                    actionMode = null
                }
            }
        }

        /**
        * Set up checkbox value and visibility based on aSalesOrderItemTypeEntity selection status
        *
        * @param [checkBox] to set
        * @param [isASalesOrderItemTypeSelected] true if aSalesOrderItemTypeEntity is selected via long press action
        */
        private fun setCheckBox(checkBox: CheckBox, isASalesOrderItemTypeSelected: Boolean) {
            checkBox.isChecked = isASalesOrderItemTypeSelected
        }

        /**
        * Use DiffUtil to calculate the difference and dispatch them to the adapter
        * Note: Please use background thread for calculation if the list is large to avoid blocking main thread
        */
        @WorkerThread
        fun setItems(currentASalesOrderItem: List<ASalesOrderItemType>) {
            if (aSalesOrderItem.isEmpty()) {
                aSalesOrderItem = java.util.ArrayList(currentASalesOrderItem)
                notifyItemRangeInserted(0, currentASalesOrderItem.size)
            } else {
                val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun getOldListSize(): Int {
                        return aSalesOrderItem.size
                    }

                    override fun getNewListSize(): Int {
                        return currentASalesOrderItem.size
                    }

                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        return aSalesOrderItem[oldItemPosition].readLink == currentASalesOrderItem[newItemPosition].readLink
                    }

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        val aSalesOrderItemTypeEntity = aSalesOrderItem[oldItemPosition]
                        return !aSalesOrderItemTypeEntity.isUpdated && currentASalesOrderItem[newItemPosition] == aSalesOrderItemTypeEntity
                    }
                })
                aSalesOrderItem.clear()
                aSalesOrderItem.addAll(currentASalesOrderItem)
                result.dispatchUpdatesTo(this)
            }
        }

        /**
        * Set ViewHolder's CheckBox onCheckedChangeListener
        *
        * @param [holder] to set
        * @param [aSalesOrderItemTypeEntity] associated with this ViewHolder
        */
        private fun setOnCheckedChangeListener(holder: ViewHolder, aSalesOrderItemTypeEntity: ASalesOrderItemType) {
            holder.checkBox.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    //(currentActivity as ASalesOrderItemActivity).onUnderDeletion(aSalesOrderItemTypeEntity, true)
                    viewModel.addSelected(aSalesOrderItemTypeEntity)
                    manageActionModeOnCheckedTransition()
                    resetPreviouslyClicked()
                } else {
                    //(currentActivity as ASalesOrderItemActivity).onUnderDeletion(aSalesOrderItemTypeEntity, false)
                    viewModel.removeSelected(aSalesOrderItemTypeEntity)
                    manageActionModeOnUncheckedTransition()
                }
                setViewBackground(holder.objectCell, viewModel.selectedContains(aSalesOrderItemTypeEntity), false)
                setDetailImage(holder, aSalesOrderItemTypeEntity)
            }
        }

        /**
        * Set ViewHolder's view onClickListener
        *
        * @param [holder] to set
        * @param [aSalesOrderItemTypeEntity] associated with this ViewHolder
        */
        private fun setOnClickListener(holder: ViewHolder, aSalesOrderItemTypeEntity: ASalesOrderItemType) {
            holder.view.setOnClickListener { view ->
                val isNavigationDisabled = (currentActivity as ASalesOrderItemActivity).isNavigationDisabled
                if( !isNavigationDisabled ) {
                    resetSelected()
                    resetPreviouslyClicked()
                    processClickAction(holder, aSalesOrderItemTypeEntity)
                    viewModel.setSelectedEntity(aSalesOrderItemTypeEntity)
                    listener?.onFragmentStateChange(UIConstants.EVENT_ITEM_CLICKED, aSalesOrderItemTypeEntity)
                } else {
                    Toast.makeText(currentActivity, "Please save your changes first...", Toast.LENGTH_LONG).show()
                }
            }
        }

        /**
        * Set background of view to indicate aSalesOrderItemTypeEntity selection status
        * Selected and Active are mutually exclusive. Only one can be true
        *
        * @param [view]
        * @param [isASalesOrderItemTypeSelected] - true if aSalesOrderItemTypeEntity is selected via long press action
        * @param [isActive]           - true if aSalesOrderItemTypeEntity is selected via click action
        */
        private fun setViewBackground(view: View, isASalesOrderItemTypeSelected: Boolean, isActive: Boolean) {
            val isMasterDetailView = currentActivity.resources.getBoolean(R.bool.two_pane)
            if (isASalesOrderItemTypeSelected) {
                view.background = ContextCompat.getDrawable(context, R.drawable.list_item_selected)
            } else if (isActive && isMasterDetailView && !isInActionMode) {
                view.background = ContextCompat.getDrawable(context, R.drawable.list_item_active)
            } else {
                view.background = ContextCompat.getDrawable(context, R.drawable.list_item_default)
            }
        }

        /**
        * ViewHolder for RecyclerView.
        * Each view has a Fiori ObjectCell and a checkbox (used by long press)
        */
        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

            var isSelected = false
                set(selected) {
                    field = selected
                    checkBox.isChecked = selected
                }

            var masterPropertyValue: String? = null

            /** Fiori ObjectCell to display aSalesOrderItemTypeEntity in list */
            val objectCell: ObjectCell = view.content

            /** Checkbox for long press selection */
            val checkBox: CheckBox = view.cbx

            override fun toString(): String {
                return super.toString() + " '" + objectCell.description + "'"
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ASalesOrderItemActivity::class.java)
    }
}
