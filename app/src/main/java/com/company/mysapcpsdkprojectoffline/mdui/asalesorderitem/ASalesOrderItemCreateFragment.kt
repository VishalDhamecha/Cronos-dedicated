package com.company.mysapcpsdkprojectoffline.mdui.asalesorderitem

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.databinding.FragmentAsalesorderitemCreateBinding
import com.company.mysapcpsdkprojectoffline.mdui.BundleKeys
import com.company.mysapcpsdkprojectoffline.mdui.InterfacedFragment
import com.company.mysapcpsdkprojectoffline.mdui.UIConstants
import com.company.mysapcpsdkprojectoffline.repository.OperationResult
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesorderitemtype.ASalesOrderItemTypeViewModel
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderItemType
import com.sap.cloud.android.odata.api_sales_order_srv_entities.API_SALES_ORDER_SRV_EntitiesMetadata.EntityTypes
import com.sap.cloud.android.odata.api_sales_order_srv_entities.API_SALES_ORDER_SRV_EntitiesMetadata.EntitySets
import com.sap.cloud.mobile.fiori.formcell.SimplePropertyFormCell
import com.sap.cloud.mobile.fiori.`object`.ObjectHeader
import com.sap.cloud.mobile.odata.Property
import org.slf4j.LoggerFactory

/**
 * A fragment that is used for both update and create for users to enter values for the properties. When used for
 * update, an instance of the entity is required. In the case of create, a new instance of the entity with defaults will
 * be created. The default values may not be acceptable for the OData service.
 * This fragment is either contained in a [ASalesOrderItemListActivity] in two-pane mode (on tablets) or a
 * [ASalesOrderItemDetailActivity] on handsets.
 *
 * Arguments: Operation: [OP_CREATE | OP_UPDATE]
 *            ASalesOrderItemType if Operation is update
 */
class ASalesOrderItemCreateFragment : InterfacedFragment<ASalesOrderItemType>() {

    /** ASalesOrderItemType object and it's copy: the modifications are done on the copied object. */
    private lateinit var aSalesOrderItemTypeEntity: ASalesOrderItemType
    private lateinit var aSalesOrderItemTypeEntityCopy: ASalesOrderItemType

    /** DataBinding generated class */
    private lateinit var binding: FragmentAsalesorderitemCreateBinding

    /** Indicate what operation to be performed */
    private lateinit var operation: String

    /** aSalesOrderItemTypeEntity ViewModel */
    private lateinit var viewModel: ASalesOrderItemTypeViewModel

    /** The update menu item */
    private lateinit var updateMenuItem: MenuItem

    private val isASalesOrderItemTypeValid: Boolean
        get() {
            var isValid = true
            view?.findViewById<LinearLayout>(R.id.create_update_asalesorderitemtype)?.let { linearLayout ->
                for (i in 0 until linearLayout.childCount) {
                    val simplePropertyFormCell = linearLayout.getChildAt(i) as SimplePropertyFormCell
                    val propertyName = simplePropertyFormCell.tag as String
                    val property = EntityTypes.aSalesOrderItemType.getProperty(propertyName)
                    val value = simplePropertyFormCell.value.toString()
                    if (!isValidProperty(property, value)) {
                        simplePropertyFormCell.setTag(R.id.TAG_HAS_MANDATORY_ERROR, true)
                        val errorMessage = resources.getString(R.string.mandatory_warning)
                        simplePropertyFormCell.isErrorEnabled = true
                        simplePropertyFormCell.error = errorMessage
                        isValid = false
                    } else {
                        if (simplePropertyFormCell.isErrorEnabled) {
                            val hasMandatoryError = simplePropertyFormCell.getTag(R.id.TAG_HAS_MANDATORY_ERROR) as Boolean
                            if (!hasMandatoryError) {
                                isValid = false
                            } else {
                                simplePropertyFormCell.isErrorEnabled = false
                            }
                        }
                        simplePropertyFormCell.setTag(R.id.TAG_HAS_MANDATORY_ERROR, false)
                    }
                }
            }
            return isValid
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menu = R.menu.itemlist_edit_options
        setHasOptionsMenu(true)

        arguments?.let {
            (it.getString(BundleKeys.OPERATION))?.let { operationType ->
                operation = operationType
                activityTitle = when (operationType) {
                    UIConstants.OP_CREATE -> resources.getString(R.string.title_create_fragment, EntityTypes.aSalesOrderItemType.localName)
                    else -> resources.getString(R.string.title_update_fragment) + " " + EntityTypes.aSalesOrderItemType.localName

                }
            }
        }

        activity?.let {
            (it as ASalesOrderItemActivity).isNavigationDisabled = true
            viewModel = ViewModelProvider(it).get(ASalesOrderItemTypeViewModel::class.java)
            viewModel.createResult.observe(this, Observer { result -> onComplete(result!!) })
            viewModel.updateResult.observe(this, Observer { result -> onComplete(result!!) })

            if (operation == UIConstants.OP_CREATE) {
                aSalesOrderItemTypeEntity = createASalesOrderItemType()
            } else {
                aSalesOrderItemTypeEntity = viewModel.selectedEntity.value!!
            }

            val workingCopy = savedInstanceState?.getParcelable<ASalesOrderItemType>(KEY_WORKING_COPY)
            if (workingCopy == null) {
                aSalesOrderItemTypeEntityCopy = aSalesOrderItemTypeEntity.copy()
                aSalesOrderItemTypeEntityCopy.setEntityTag(aSalesOrderItemTypeEntity.getEntityTag())
                aSalesOrderItemTypeEntityCopy.setOldEntity(aSalesOrderItemTypeEntity)
                aSalesOrderItemTypeEntityCopy.editLink = aSalesOrderItemTypeEntity.editLink
            } else {
                aSalesOrderItemTypeEntityCopy = workingCopy
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        currentActivity.findViewById<ObjectHeader>(R.id.objectHeader)?.let {
            it.visibility = View.GONE
        }
        val rootView = setupDataBinding(inflater, container)
        return rootView
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_item -> {
                updateMenuItem = item
                enableUpdateMenuItem(false)
                onSaveItem()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(secondaryToolbar != null) secondaryToolbar!!.setTitle(activityTitle) else activity?.setTitle(activityTitle)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(KEY_WORKING_COPY, aSalesOrderItemTypeEntityCopy)
        super.onSaveInstanceState(outState)
    }

    /** Enables the update menu item based on [enable] */
    private fun enableUpdateMenuItem(enable : Boolean = true) {
        updateMenuItem.also {
            it.isEnabled = enable
            it.icon.alpha = if(enable) 255 else 130
        }
    }

    /** Saves the entity */
    private fun onSaveItem(): Boolean {
        if (!isASalesOrderItemTypeValid) {
            return false
        }
        (currentActivity as ASalesOrderItemActivity).isNavigationDisabled = false
        progressBar?.visibility = View.VISIBLE
        when (operation) {
            UIConstants.OP_CREATE -> {
                viewModel.create(aSalesOrderItemTypeEntityCopy)
            }
            UIConstants.OP_UPDATE -> viewModel.update(aSalesOrderItemTypeEntityCopy)
        }
        return true
    }

    /**
     * Create a new ASalesOrderItemType instance and initialize properties to its default values
     * Nullable property will remain null
     * For offline, keys will be unset to avoid collision should more than one is created locally
     * @return new ASalesOrderItemType instance
     */
    private fun createASalesOrderItemType(): ASalesOrderItemType {
        val entity = ASalesOrderItemType(true)
        entity.unsetDataValue(ASalesOrderItemType.salesOrder)
        entity.unsetDataValue(ASalesOrderItemType.salesOrderItem)
        return entity
    }

    /** Callback function to complete processing when updateResult or createResult events fired */
    private fun onComplete(result: OperationResult<ASalesOrderItemType>) {
        progressBar?.visibility = View.INVISIBLE
        enableUpdateMenuItem(true)
        if (result.error != null) {
            (currentActivity as ASalesOrderItemActivity).isNavigationDisabled = true
            handleError(result)
        } else {
            if (operation == UIConstants.OP_UPDATE && !currentActivity.resources.getBoolean(R.bool.two_pane)) {
                viewModel.selectedEntity.value = aSalesOrderItemTypeEntityCopy
            }
            if (currentActivity.resources.getBoolean(R.bool.two_pane)) {
                val listFragment = currentActivity.supportFragmentManager.findFragmentByTag(UIConstants.LIST_FRAGMENT_TAG)
                (listFragment as ASalesOrderItemListFragment).refreshListData()
            }
            (currentActivity as ASalesOrderItemActivity).onBackPressed()
        }
    }

    /** Simple validation: checks the presence of mandatory fields. */
    private fun isValidProperty(property: Property, value: String): Boolean {
        return !(!property.isNullable && value.isEmpty())
    }

    /**
     * Set up data binding for this view
     *
     * @param [inflater] layout inflater from onCreateView
     * @param [container] view group from onCreateView
     *
     * @return rootView from generated data binding code
     */
    private fun setupDataBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentAsalesorderitemCreateBinding.inflate(inflater, container, false)
        binding.setASalesOrderItemType(aSalesOrderItemTypeEntityCopy)
        return binding.root
    }

    /**
     * Notify user of error encountered while execution the operation
     *
     * @param [result] operation result with error
     */
    private fun handleError(result: OperationResult<ASalesOrderItemType>) {
        val errorMessage = when (result.operation) {
            OperationResult.Operation.UPDATE -> getString(R.string.update_failed_detail)
            OperationResult.Operation.CREATE -> getString(R.string.create_failed_detail)
            else -> throw AssertionError()
        }
        showError(errorMessage)
    }


    companion object {
        private val KEY_WORKING_COPY = "WORKING_COPY"
        private val LOGGER = LoggerFactory.getLogger(ASalesOrderItemActivity::class.java)
    }
}
