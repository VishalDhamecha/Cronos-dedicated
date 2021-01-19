package com.company.mysapcpsdkprojectoffline.mdui.asalesordertext

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
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesordertexttype.ASalesOrderTextTypeViewModel
import com.company.mysapcpsdkprojectoffline.repository.OperationResult
import com.company.mysapcpsdkprojectoffline.mdui.UIConstants
import com.company.mysapcpsdkprojectoffline.mdui.EntityKeyUtil
import com.company.mysapcpsdkprojectoffline.mdui.EntitySetListActivity.EntitySetName
import com.company.mysapcpsdkprojectoffline.mdui.EntitySetListActivity
import com.company.mysapcpsdkprojectoffline.mdui.InterfacedFragment
import com.sap.cloud.android.odata.api_sales_order_srv_entities.API_SALES_ORDER_SRV_EntitiesMetadata.EntitySets
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderTextType
import com.sap.cloud.mobile.fiori.`object`.ObjectCell
import com.sap.cloud.mobile.fiori.`object`.ObjectHeader
import com.sap.cloud.mobile.odata.EntityValue
import kotlinx.android.synthetic.main.element_entityitem_list.view.*
import org.slf4j.LoggerFactory

/**
 * An activity representing a list of ASalesOrderTextType. This activity has different presentations for handset and tablet-size
 * devices. On handsets, the activity presents a list of items, which when touched, lead to a view representing
 * ASalesOrderTextType details. On tablets, the activity presents the list of ASalesOrderTextType and ASalesOrderTextType details side-by-side using two
 * vertical panes.
 */

class ASalesOrderTextListFragment : InterfacedFragment<ASalesOrderTextType>() {

    /**
     * List adapter to be used with RecyclerView containing all instances of aSalesOrderText
     */
    private var adapter: ASalesOrderTextTypeListAdapter? = null

    private lateinit var refreshLayout: SwipeRefreshLayout
    private var actionMode: ActionMode? = null
    private var isInActionMode: Boolean = false
    private val selectedItems = ArrayList<Int>()

    /**
     * View model of the entity type
     */
    private lateinit var viewModel: ASalesOrderTextTypeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTitle = getString(EntitySetListActivity.EntitySetName.ASalesOrderText.titleId)
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
            this.adapter = ASalesOrderTextTypeListAdapter(currentActivity, it)
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
                .get(ASalesOrderTextTypeViewModel::class.java)
        } else {
            ViewModelProvider(currentActivity).get(ASalesOrderTextTypeViewModel::class.java).also {
                it.initialRead{errorMessage ->
                    showError(errorMessage)
                }
            }
        }
        viewModel.observableItems.observe(viewLifecycleOwner, Observer<List<ASalesOrderTextType>> { items ->
            items?.let { entityList ->
                adapter?.let { listAdapter ->
                    listAdapter.setItems(entityList)

                    var item = viewModel.selectedEntity.value?.let { containsItem(entityList, it) }
                    if (item == null) {
                        item = if (entityList.isEmpty()) null else entityList[0]
                    }

                    item?.let {
                        viewModel.inFocusId = listAdapter.getItemIdForASalesOrderTextType(it)
                        if (currentActivity.resources.getBoolean(R.bool.two_pane)) {
                            viewModel.setSelectedEntity(it)
                            if(!isInActionMode && !(currentActivity as ASalesOrderTextActivity).isNavigationDisabled) {
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
    private fun containsItem(items: List<ASalesOrderTextType>, item: ASalesOrderTextType) : ASalesOrderTextType? {
        return items.find { entry ->
            adapter?.getItemIdForASalesOrderTextType(entry) == adapter?.getItemIdForASalesOrderTextType(item)
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
    private fun onDeleteComplete(result: OperationResult<ASalesOrderTextType>) {
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
    private fun setItemIdSelected(itemId: Int): ASalesOrderTextType? {
        viewModel.observableItems.value?.let { aSalesOrderText ->
            if (aSalesOrderText.isNotEmpty()) {
                adapter?.let {
                    viewModel.inFocusId = it.getItemIdForASalesOrderTextType(aSalesOrderText[itemId])
                    return aSalesOrderText[itemId]
                }
            }
        }
        return null
    }

    /** Sets the detail image for the given [viewHolder] */
    private fun setDetailImage(viewHolder: ASalesOrderTextTypeListAdapter.ViewHolder, aSalesOrderTextTypeEntity: ASalesOrderTextType?) {
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
    inner class OnActionModeStartClickListener(internal var holder: ASalesOrderTextTypeListAdapter.ViewHolder) : View.OnClickListener, View.OnLongClickListener {

        override fun onClick(view: View) {
            onAnyKindOfClick()
        }

        override fun onLongClick(view: View): Boolean {
            return onAnyKindOfClick()
        }

        /** callback function for both normal and long click of an entity */
        private fun onAnyKindOfClick(): Boolean {
            val isNavigationDisabled = (activity as ASalesOrderTextActivity).isNavigationDisabled
            if (isNavigationDisabled) {
                Toast.makeText(activity, "Please save your changes first...", Toast.LENGTH_LONG).show()
            } else {
                if (!isInActionMode) {
                    actionMode = (currentActivity as AppCompatActivity).startSupportActionMode(ASalesOrderTextListActionMode())
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
    inner class ASalesOrderTextListActionMode : ActionMode.Callback {
        override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
            isInActionMode = true
            (currentActivity.findViewById<FloatingActionButton>(R.id.fab))?.let {
                it.hide()
            }
            //(currentActivity as ASalesOrderTextActivity).onSetActionModeFlag(isInActionMode)
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
                    val aSalesOrderTextTypeEntity = viewModel.getSelected(0)
                    if (viewModel.numberOfSelected() == 1 && aSalesOrderTextTypeEntity != null) {
                        isInActionMode = false
                        actionMode.finish()
                        viewModel.setSelectedEntity(aSalesOrderTextTypeEntity)
                        if(currentActivity.resources.getBoolean(R.bool.two_pane)) {
                            //make sure 'view' is under 'crt/update',
                            //so after done or back, the right panel has things to view
                            listener?.onFragmentStateChange(UIConstants.EVENT_ITEM_CLICKED, aSalesOrderTextTypeEntity)
                        }
                        listener?.onFragmentStateChange(UIConstants.EVENT_EDIT_ITEM, aSalesOrderTextTypeEntity)
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
    * List adapter to be used with RecyclerView. It contains the set of aSalesOrderText.
    */
    inner class ASalesOrderTextTypeListAdapter(private val context: Context, private val recyclerView: RecyclerView) : RecyclerView.Adapter<ASalesOrderTextTypeListAdapter.ViewHolder>() {

        /** Entire list of ASalesOrderTextType collection */
        private var aSalesOrderText: MutableList<ASalesOrderTextType> = ArrayList()

        /** Flag to indicate whether we have checked retained selected aSalesOrderText */
        private var checkForSelectedOnCreate = false

        init {
            setHasStableIds(true)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ASalesOrderTextTypeListAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.element_entityitem_list, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return aSalesOrderText.size
        }

        override fun getItemId(position: Int): Long {
            return getItemIdForASalesOrderTextType(aSalesOrderText[position])
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            checkForRetainedSelection()

            val aSalesOrderTextTypeEntity = aSalesOrderText[holder.adapterPosition]
            (aSalesOrderTextTypeEntity.getDataValue(ASalesOrderTextType.salesOrder))?.let {
                holder.masterPropertyValue = it.toString()
            }
            populateObjectCell(holder, aSalesOrderTextTypeEntity)

            val isActive = getItemIdForASalesOrderTextType(aSalesOrderTextTypeEntity) == viewModel.inFocusId
            if (isActive) {
                setItemIdSelected(holder.adapterPosition)
            }
            val isASalesOrderTextTypeSelected = viewModel.selectedContains(aSalesOrderTextTypeEntity)
            setViewBackground(holder.objectCell, isASalesOrderTextTypeSelected, isActive)

            holder.view.setOnLongClickListener(OnActionModeStartClickListener(holder))
            setOnClickListener(holder, aSalesOrderTextTypeEntity)

            setOnCheckedChangeListener(holder, aSalesOrderTextTypeEntity)
            holder.isSelected = isASalesOrderTextTypeSelected
            setDetailImage(holder, aSalesOrderTextTypeEntity)
        }

        /**
        * Check to see if there are an retained selected aSalesOrderTextTypeEntity on start.
        * This situation occurs when a rotation with selected aSalesOrderText is triggered by user.
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
        * Computes a stable ID for each ASalesOrderTextType object for use to locate the ViewHolder
        *
        * @param [aSalesOrderTextTypeEntity] to get the items for
        * @return an ID based on the primary key of ASalesOrderTextType
        */
        internal fun getItemIdForASalesOrderTextType(aSalesOrderTextTypeEntity: ASalesOrderTextType): Long {
            return aSalesOrderTextTypeEntity.readLink.hashCode().toLong()
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
                actionMode = (activity as AppCompatActivity).startSupportActionMode(ASalesOrderTextListActionMode())
            }
            if (viewModel.numberOfSelected() > 1) {
                actionMode?.menu?.findItem(R.id.update_item)?.isVisible = false
            }
            actionMode?.title = viewModel.numberOfSelected().toString()
        }

        /**
        * This is called when one of the selected aSalesOrderText has been de-selected
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

        private fun populateObjectCell(viewHolder: ViewHolder, aSalesOrderTextTypeEntity: ASalesOrderTextType) {

            val dataValue = aSalesOrderTextTypeEntity.getDataValue(ASalesOrderTextType.salesOrder)
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
                aSalesOrderTextTypeEntity.inErrorState -> setIcon(R.drawable.ic_error_state, 0, R.string.error_state)
                aSalesOrderTextTypeEntity.isUpdated -> setIcon(R.drawable.ic_updated_state, 0, R.string.updated_state)
                aSalesOrderTextTypeEntity.isLocal -> setIcon(R.drawable.ic_local_state, 0, R.string.local_state)
                else -> setIcon(R.drawable.ic_download_state, 0, R.string.download_state)
                }
                setIcon(R.drawable.default_dot, 1, R.string.attachment_item_content_desc)
                setIcon("!", 2)
            }
        }

        private fun processClickAction(viewHolder: ViewHolder, aSalesOrderTextTypeEntity: ASalesOrderTextType) {
            resetPreviouslyClicked()
            setViewBackground(viewHolder.objectCell, false, true)
            viewModel.inFocusId = getItemIdForASalesOrderTextType(aSalesOrderTextTypeEntity)
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
        * If there are selected aSalesOrderText via long press, clear them as click and long press are mutually exclusive
        * In addition, since we are clearing all selected aSalesOrderText via long press, finish the action mode.
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
        * Set up checkbox value and visibility based on aSalesOrderTextTypeEntity selection status
        *
        * @param [checkBox] to set
        * @param [isASalesOrderTextTypeSelected] true if aSalesOrderTextTypeEntity is selected via long press action
        */
        private fun setCheckBox(checkBox: CheckBox, isASalesOrderTextTypeSelected: Boolean) {
            checkBox.isChecked = isASalesOrderTextTypeSelected
        }

        /**
        * Use DiffUtil to calculate the difference and dispatch them to the adapter
        * Note: Please use background thread for calculation if the list is large to avoid blocking main thread
        */
        @WorkerThread
        fun setItems(currentASalesOrderText: List<ASalesOrderTextType>) {
            if (aSalesOrderText.isEmpty()) {
                aSalesOrderText = java.util.ArrayList(currentASalesOrderText)
                notifyItemRangeInserted(0, currentASalesOrderText.size)
            } else {
                val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun getOldListSize(): Int {
                        return aSalesOrderText.size
                    }

                    override fun getNewListSize(): Int {
                        return currentASalesOrderText.size
                    }

                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        return aSalesOrderText[oldItemPosition].readLink == currentASalesOrderText[newItemPosition].readLink
                    }

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        val aSalesOrderTextTypeEntity = aSalesOrderText[oldItemPosition]
                        return !aSalesOrderTextTypeEntity.isUpdated && currentASalesOrderText[newItemPosition] == aSalesOrderTextTypeEntity
                    }
                })
                aSalesOrderText.clear()
                aSalesOrderText.addAll(currentASalesOrderText)
                result.dispatchUpdatesTo(this)
            }
        }

        /**
        * Set ViewHolder's CheckBox onCheckedChangeListener
        *
        * @param [holder] to set
        * @param [aSalesOrderTextTypeEntity] associated with this ViewHolder
        */
        private fun setOnCheckedChangeListener(holder: ViewHolder, aSalesOrderTextTypeEntity: ASalesOrderTextType) {
            holder.checkBox.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    //(currentActivity as ASalesOrderTextActivity).onUnderDeletion(aSalesOrderTextTypeEntity, true)
                    viewModel.addSelected(aSalesOrderTextTypeEntity)
                    manageActionModeOnCheckedTransition()
                    resetPreviouslyClicked()
                } else {
                    //(currentActivity as ASalesOrderTextActivity).onUnderDeletion(aSalesOrderTextTypeEntity, false)
                    viewModel.removeSelected(aSalesOrderTextTypeEntity)
                    manageActionModeOnUncheckedTransition()
                }
                setViewBackground(holder.objectCell, viewModel.selectedContains(aSalesOrderTextTypeEntity), false)
                setDetailImage(holder, aSalesOrderTextTypeEntity)
            }
        }

        /**
        * Set ViewHolder's view onClickListener
        *
        * @param [holder] to set
        * @param [aSalesOrderTextTypeEntity] associated with this ViewHolder
        */
        private fun setOnClickListener(holder: ViewHolder, aSalesOrderTextTypeEntity: ASalesOrderTextType) {
            holder.view.setOnClickListener { view ->
                val isNavigationDisabled = (currentActivity as ASalesOrderTextActivity).isNavigationDisabled
                if( !isNavigationDisabled ) {
                    resetSelected()
                    resetPreviouslyClicked()
                    processClickAction(holder, aSalesOrderTextTypeEntity)
                    viewModel.setSelectedEntity(aSalesOrderTextTypeEntity)
                    listener?.onFragmentStateChange(UIConstants.EVENT_ITEM_CLICKED, aSalesOrderTextTypeEntity)
                } else {
                    Toast.makeText(currentActivity, "Please save your changes first...", Toast.LENGTH_LONG).show()
                }
            }
        }

        /**
        * Set background of view to indicate aSalesOrderTextTypeEntity selection status
        * Selected and Active are mutually exclusive. Only one can be true
        *
        * @param [view]
        * @param [isASalesOrderTextTypeSelected] - true if aSalesOrderTextTypeEntity is selected via long press action
        * @param [isActive]           - true if aSalesOrderTextTypeEntity is selected via click action
        */
        private fun setViewBackground(view: View, isASalesOrderTextTypeSelected: Boolean, isActive: Boolean) {
            val isMasterDetailView = currentActivity.resources.getBoolean(R.bool.two_pane)
            if (isASalesOrderTextTypeSelected) {
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

            /** Fiori ObjectCell to display aSalesOrderTextTypeEntity in list */
            val objectCell: ObjectCell = view.content

            /** Checkbox for long press selection */
            val checkBox: CheckBox = view.cbx

            override fun toString(): String {
                return super.toString() + " '" + objectCell.description + "'"
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ASalesOrderTextActivity::class.java)
    }
}
