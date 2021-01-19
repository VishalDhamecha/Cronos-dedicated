package com.company.mysapcpsdkprojectoffline.mdui.asalesorderheaderprelement

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
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesorderheaderprelementtype.ASalesOrderHeaderPrElementTypeViewModel
import com.company.mysapcpsdkprojectoffline.repository.OperationResult
import com.company.mysapcpsdkprojectoffline.mdui.UIConstants
import com.company.mysapcpsdkprojectoffline.mdui.EntityKeyUtil
import com.company.mysapcpsdkprojectoffline.mdui.EntitySetListActivity.EntitySetName
import com.company.mysapcpsdkprojectoffline.mdui.EntitySetListActivity
import com.company.mysapcpsdkprojectoffline.mdui.InterfacedFragment
import com.sap.cloud.android.odata.api_sales_order_srv_entities.API_SALES_ORDER_SRV_EntitiesMetadata.EntitySets
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderHeaderPrElementType
import com.sap.cloud.mobile.fiori.`object`.ObjectCell
import com.sap.cloud.mobile.fiori.`object`.ObjectHeader
import com.sap.cloud.mobile.odata.EntityValue
import kotlinx.android.synthetic.main.element_entityitem_list.view.*
import org.slf4j.LoggerFactory

/**
 * An activity representing a list of ASalesOrderHeaderPrElementType. This activity has different presentations for handset and tablet-size
 * devices. On handsets, the activity presents a list of items, which when touched, lead to a view representing
 * ASalesOrderHeaderPrElementType details. On tablets, the activity presents the list of ASalesOrderHeaderPrElementType and ASalesOrderHeaderPrElementType details side-by-side using two
 * vertical panes.
 */

class ASalesOrderHeaderPrElementListFragment : InterfacedFragment<ASalesOrderHeaderPrElementType>() {

    /**
     * List adapter to be used with RecyclerView containing all instances of aSalesOrderHeaderPrElement
     */
    private var adapter: ASalesOrderHeaderPrElementTypeListAdapter? = null

    private lateinit var refreshLayout: SwipeRefreshLayout
    private var actionMode: ActionMode? = null
    private var isInActionMode: Boolean = false
    private val selectedItems = ArrayList<Int>()

    /**
     * View model of the entity type
     */
    private lateinit var viewModel: ASalesOrderHeaderPrElementTypeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTitle = getString(EntitySetListActivity.EntitySetName.ASalesOrderHeaderPrElement.titleId)
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
            this.adapter = ASalesOrderHeaderPrElementTypeListAdapter(currentActivity, it)
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
                .get(ASalesOrderHeaderPrElementTypeViewModel::class.java)
        } else {
            ViewModelProvider(currentActivity).get(ASalesOrderHeaderPrElementTypeViewModel::class.java).also {
                it.initialRead{errorMessage ->
                    showError(errorMessage)
                }
            }
        }
        viewModel.observableItems.observe(viewLifecycleOwner, Observer<List<ASalesOrderHeaderPrElementType>> { items ->
            items?.let { entityList ->
                adapter?.let { listAdapter ->
                    listAdapter.setItems(entityList)

                    var item = viewModel.selectedEntity.value?.let { containsItem(entityList, it) }
                    if (item == null) {
                        item = if (entityList.isEmpty()) null else entityList[0]
                    }

                    item?.let {
                        viewModel.inFocusId = listAdapter.getItemIdForASalesOrderHeaderPrElementType(it)
                        if (currentActivity.resources.getBoolean(R.bool.two_pane)) {
                            viewModel.setSelectedEntity(it)
                            if(!isInActionMode && !(currentActivity as ASalesOrderHeaderPrElementActivity).isNavigationDisabled) {
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
    private fun containsItem(items: List<ASalesOrderHeaderPrElementType>, item: ASalesOrderHeaderPrElementType) : ASalesOrderHeaderPrElementType? {
        return items.find { entry ->
            adapter?.getItemIdForASalesOrderHeaderPrElementType(entry) == adapter?.getItemIdForASalesOrderHeaderPrElementType(item)
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
    private fun onDeleteComplete(result: OperationResult<ASalesOrderHeaderPrElementType>) {
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
    private fun setItemIdSelected(itemId: Int): ASalesOrderHeaderPrElementType? {
        viewModel.observableItems.value?.let { aSalesOrderHeaderPrElement ->
            if (aSalesOrderHeaderPrElement.isNotEmpty()) {
                adapter?.let {
                    viewModel.inFocusId = it.getItemIdForASalesOrderHeaderPrElementType(aSalesOrderHeaderPrElement[itemId])
                    return aSalesOrderHeaderPrElement[itemId]
                }
            }
        }
        return null
    }

    /** Sets the detail image for the given [viewHolder] */
    private fun setDetailImage(viewHolder: ASalesOrderHeaderPrElementTypeListAdapter.ViewHolder, aSalesOrderHeaderPrElementTypeEntity: ASalesOrderHeaderPrElementType?) {
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
    inner class OnActionModeStartClickListener(internal var holder: ASalesOrderHeaderPrElementTypeListAdapter.ViewHolder) : View.OnClickListener, View.OnLongClickListener {

        override fun onClick(view: View) {
            onAnyKindOfClick()
        }

        override fun onLongClick(view: View): Boolean {
            return onAnyKindOfClick()
        }

        /** callback function for both normal and long click of an entity */
        private fun onAnyKindOfClick(): Boolean {
            val isNavigationDisabled = (activity as ASalesOrderHeaderPrElementActivity).isNavigationDisabled
            if (isNavigationDisabled) {
                Toast.makeText(activity, "Please save your changes first...", Toast.LENGTH_LONG).show()
            } else {
                if (!isInActionMode) {
                    actionMode = (currentActivity as AppCompatActivity).startSupportActionMode(ASalesOrderHeaderPrElementListActionMode())
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
    inner class ASalesOrderHeaderPrElementListActionMode : ActionMode.Callback {
        override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
            isInActionMode = true
            (currentActivity.findViewById<FloatingActionButton>(R.id.fab))?.let {
                it.hide()
            }
            //(currentActivity as ASalesOrderHeaderPrElementActivity).onSetActionModeFlag(isInActionMode)
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
                    val aSalesOrderHeaderPrElementTypeEntity = viewModel.getSelected(0)
                    if (viewModel.numberOfSelected() == 1 && aSalesOrderHeaderPrElementTypeEntity != null) {
                        isInActionMode = false
                        actionMode.finish()
                        viewModel.setSelectedEntity(aSalesOrderHeaderPrElementTypeEntity)
                        if(currentActivity.resources.getBoolean(R.bool.two_pane)) {
                            //make sure 'view' is under 'crt/update',
                            //so after done or back, the right panel has things to view
                            listener?.onFragmentStateChange(UIConstants.EVENT_ITEM_CLICKED, aSalesOrderHeaderPrElementTypeEntity)
                        }
                        listener?.onFragmentStateChange(UIConstants.EVENT_EDIT_ITEM, aSalesOrderHeaderPrElementTypeEntity)
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
    * List adapter to be used with RecyclerView. It contains the set of aSalesOrderHeaderPrElement.
    */
    inner class ASalesOrderHeaderPrElementTypeListAdapter(private val context: Context, private val recyclerView: RecyclerView) : RecyclerView.Adapter<ASalesOrderHeaderPrElementTypeListAdapter.ViewHolder>() {

        /** Entire list of ASalesOrderHeaderPrElementType collection */
        private var aSalesOrderHeaderPrElement: MutableList<ASalesOrderHeaderPrElementType> = ArrayList()

        /** Flag to indicate whether we have checked retained selected aSalesOrderHeaderPrElement */
        private var checkForSelectedOnCreate = false

        init {
            setHasStableIds(true)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ASalesOrderHeaderPrElementTypeListAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.element_entityitem_list, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return aSalesOrderHeaderPrElement.size
        }

        override fun getItemId(position: Int): Long {
            return getItemIdForASalesOrderHeaderPrElementType(aSalesOrderHeaderPrElement[position])
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            checkForRetainedSelection()

            val aSalesOrderHeaderPrElementTypeEntity = aSalesOrderHeaderPrElement[holder.adapterPosition]
            (aSalesOrderHeaderPrElementTypeEntity.getDataValue(ASalesOrderHeaderPrElementType.conditionType))?.let {
                holder.masterPropertyValue = it.toString()
            }
            populateObjectCell(holder, aSalesOrderHeaderPrElementTypeEntity)

            val isActive = getItemIdForASalesOrderHeaderPrElementType(aSalesOrderHeaderPrElementTypeEntity) == viewModel.inFocusId
            if (isActive) {
                setItemIdSelected(holder.adapterPosition)
            }
            val isASalesOrderHeaderPrElementTypeSelected = viewModel.selectedContains(aSalesOrderHeaderPrElementTypeEntity)
            setViewBackground(holder.objectCell, isASalesOrderHeaderPrElementTypeSelected, isActive)

            holder.view.setOnLongClickListener(OnActionModeStartClickListener(holder))
            setOnClickListener(holder, aSalesOrderHeaderPrElementTypeEntity)

            setOnCheckedChangeListener(holder, aSalesOrderHeaderPrElementTypeEntity)
            holder.isSelected = isASalesOrderHeaderPrElementTypeSelected
            setDetailImage(holder, aSalesOrderHeaderPrElementTypeEntity)
        }

        /**
        * Check to see if there are an retained selected aSalesOrderHeaderPrElementTypeEntity on start.
        * This situation occurs when a rotation with selected aSalesOrderHeaderPrElement is triggered by user.
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
        * Computes a stable ID for each ASalesOrderHeaderPrElementType object for use to locate the ViewHolder
        *
        * @param [aSalesOrderHeaderPrElementTypeEntity] to get the items for
        * @return an ID based on the primary key of ASalesOrderHeaderPrElementType
        */
        internal fun getItemIdForASalesOrderHeaderPrElementType(aSalesOrderHeaderPrElementTypeEntity: ASalesOrderHeaderPrElementType): Long {
            return aSalesOrderHeaderPrElementTypeEntity.readLink.hashCode().toLong()
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
                actionMode = (activity as AppCompatActivity).startSupportActionMode(ASalesOrderHeaderPrElementListActionMode())
            }
            if (viewModel.numberOfSelected() > 1) {
                actionMode?.menu?.findItem(R.id.update_item)?.isVisible = false
            }
            actionMode?.title = viewModel.numberOfSelected().toString()
        }

        /**
        * This is called when one of the selected aSalesOrderHeaderPrElement has been de-selected
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

        private fun populateObjectCell(viewHolder: ViewHolder, aSalesOrderHeaderPrElementTypeEntity: ASalesOrderHeaderPrElementType) {

            val dataValue = aSalesOrderHeaderPrElementTypeEntity.getDataValue(ASalesOrderHeaderPrElementType.conditionType)
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
                aSalesOrderHeaderPrElementTypeEntity.inErrorState -> setIcon(R.drawable.ic_error_state, 0, R.string.error_state)
                aSalesOrderHeaderPrElementTypeEntity.isUpdated -> setIcon(R.drawable.ic_updated_state, 0, R.string.updated_state)
                aSalesOrderHeaderPrElementTypeEntity.isLocal -> setIcon(R.drawable.ic_local_state, 0, R.string.local_state)
                else -> setIcon(R.drawable.ic_download_state, 0, R.string.download_state)
                }
                setIcon(R.drawable.default_dot, 1, R.string.attachment_item_content_desc)
                setIcon("!", 2)
            }
        }

        private fun processClickAction(viewHolder: ViewHolder, aSalesOrderHeaderPrElementTypeEntity: ASalesOrderHeaderPrElementType) {
            resetPreviouslyClicked()
            setViewBackground(viewHolder.objectCell, false, true)
            viewModel.inFocusId = getItemIdForASalesOrderHeaderPrElementType(aSalesOrderHeaderPrElementTypeEntity)
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
        * If there are selected aSalesOrderHeaderPrElement via long press, clear them as click and long press are mutually exclusive
        * In addition, since we are clearing all selected aSalesOrderHeaderPrElement via long press, finish the action mode.
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
        * Set up checkbox value and visibility based on aSalesOrderHeaderPrElementTypeEntity selection status
        *
        * @param [checkBox] to set
        * @param [isASalesOrderHeaderPrElementTypeSelected] true if aSalesOrderHeaderPrElementTypeEntity is selected via long press action
        */
        private fun setCheckBox(checkBox: CheckBox, isASalesOrderHeaderPrElementTypeSelected: Boolean) {
            checkBox.isChecked = isASalesOrderHeaderPrElementTypeSelected
        }

        /**
        * Use DiffUtil to calculate the difference and dispatch them to the adapter
        * Note: Please use background thread for calculation if the list is large to avoid blocking main thread
        */
        @WorkerThread
        fun setItems(currentASalesOrderHeaderPrElement: List<ASalesOrderHeaderPrElementType>) {
            if (aSalesOrderHeaderPrElement.isEmpty()) {
                aSalesOrderHeaderPrElement = java.util.ArrayList(currentASalesOrderHeaderPrElement)
                notifyItemRangeInserted(0, currentASalesOrderHeaderPrElement.size)
            } else {
                val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun getOldListSize(): Int {
                        return aSalesOrderHeaderPrElement.size
                    }

                    override fun getNewListSize(): Int {
                        return currentASalesOrderHeaderPrElement.size
                    }

                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        return aSalesOrderHeaderPrElement[oldItemPosition].readLink == currentASalesOrderHeaderPrElement[newItemPosition].readLink
                    }

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        val aSalesOrderHeaderPrElementTypeEntity = aSalesOrderHeaderPrElement[oldItemPosition]
                        return !aSalesOrderHeaderPrElementTypeEntity.isUpdated && currentASalesOrderHeaderPrElement[newItemPosition] == aSalesOrderHeaderPrElementTypeEntity
                    }
                })
                aSalesOrderHeaderPrElement.clear()
                aSalesOrderHeaderPrElement.addAll(currentASalesOrderHeaderPrElement)
                result.dispatchUpdatesTo(this)
            }
        }

        /**
        * Set ViewHolder's CheckBox onCheckedChangeListener
        *
        * @param [holder] to set
        * @param [aSalesOrderHeaderPrElementTypeEntity] associated with this ViewHolder
        */
        private fun setOnCheckedChangeListener(holder: ViewHolder, aSalesOrderHeaderPrElementTypeEntity: ASalesOrderHeaderPrElementType) {
            holder.checkBox.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    //(currentActivity as ASalesOrderHeaderPrElementActivity).onUnderDeletion(aSalesOrderHeaderPrElementTypeEntity, true)
                    viewModel.addSelected(aSalesOrderHeaderPrElementTypeEntity)
                    manageActionModeOnCheckedTransition()
                    resetPreviouslyClicked()
                } else {
                    //(currentActivity as ASalesOrderHeaderPrElementActivity).onUnderDeletion(aSalesOrderHeaderPrElementTypeEntity, false)
                    viewModel.removeSelected(aSalesOrderHeaderPrElementTypeEntity)
                    manageActionModeOnUncheckedTransition()
                }
                setViewBackground(holder.objectCell, viewModel.selectedContains(aSalesOrderHeaderPrElementTypeEntity), false)
                setDetailImage(holder, aSalesOrderHeaderPrElementTypeEntity)
            }
        }

        /**
        * Set ViewHolder's view onClickListener
        *
        * @param [holder] to set
        * @param [aSalesOrderHeaderPrElementTypeEntity] associated with this ViewHolder
        */
        private fun setOnClickListener(holder: ViewHolder, aSalesOrderHeaderPrElementTypeEntity: ASalesOrderHeaderPrElementType) {
            holder.view.setOnClickListener { view ->
                val isNavigationDisabled = (currentActivity as ASalesOrderHeaderPrElementActivity).isNavigationDisabled
                if( !isNavigationDisabled ) {
                    resetSelected()
                    resetPreviouslyClicked()
                    processClickAction(holder, aSalesOrderHeaderPrElementTypeEntity)
                    viewModel.setSelectedEntity(aSalesOrderHeaderPrElementTypeEntity)
                    listener?.onFragmentStateChange(UIConstants.EVENT_ITEM_CLICKED, aSalesOrderHeaderPrElementTypeEntity)
                } else {
                    Toast.makeText(currentActivity, "Please save your changes first...", Toast.LENGTH_LONG).show()
                }
            }
        }

        /**
        * Set background of view to indicate aSalesOrderHeaderPrElementTypeEntity selection status
        * Selected and Active are mutually exclusive. Only one can be true
        *
        * @param [view]
        * @param [isASalesOrderHeaderPrElementTypeSelected] - true if aSalesOrderHeaderPrElementTypeEntity is selected via long press action
        * @param [isActive]           - true if aSalesOrderHeaderPrElementTypeEntity is selected via click action
        */
        private fun setViewBackground(view: View, isASalesOrderHeaderPrElementTypeSelected: Boolean, isActive: Boolean) {
            val isMasterDetailView = currentActivity.resources.getBoolean(R.bool.two_pane)
            if (isASalesOrderHeaderPrElementTypeSelected) {
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

            /** Fiori ObjectCell to display aSalesOrderHeaderPrElementTypeEntity in list */
            val objectCell: ObjectCell = view.content

            /** Checkbox for long press selection */
            val checkBox: CheckBox = view.cbx

            override fun toString(): String {
                return super.toString() + " '" + objectCell.description + "'"
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ASalesOrderHeaderPrElementActivity::class.java)
    }
}
