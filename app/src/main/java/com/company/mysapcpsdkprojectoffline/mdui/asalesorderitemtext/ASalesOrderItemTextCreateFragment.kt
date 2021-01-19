package com.company.mysapcpsdkprojectoffline.mdui.asalesorderitemtext

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.databinding.FragmentAsalesorderitemtextCreateBinding
import com.company.mysapcpsdkprojectoffline.mdui.BundleKeys
import com.company.mysapcpsdkprojectoffline.mdui.InterfacedFragment
import com.company.mysapcpsdkprojectoffline.mdui.UIConstants
import com.company.mysapcpsdkprojectoffline.repository.OperationResult
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesorderitemtexttype.ASalesOrderItemTextTypeViewModel
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderItemTextType
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
 * This fragment is either contained in a [ASalesOrderItemTextListActivity] in two-pane mode (on tablets) or a
 * [ASalesOrderItemTextDetailActivity] on handsets.
 *
 * Arguments: Operation: [OP_CREATE | OP_UPDATE]
 *            ASalesOrderItemTextType if Operation is update
 */
class ASalesOrderItemTextCreateFragment : InterfacedFragment<ASalesOrderItemTextType>() {

    /** ASalesOrderItemTextType object and it's copy: the modifications are done on the copied object. */
    private lateinit var aSalesOrderItemTextTypeEntity: ASalesOrderItemTextType
    private lateinit var aSalesOrderItemTextTypeEntityCopy: ASalesOrderItemTextType

    /** DataBinding generated class */
    private lateinit var binding: FragmentAsalesorderitemtextCreateBinding

    /** Indicate what operation to be performed */
    private lateinit var operation: String

    /** aSalesOrderItemTextTypeEntity ViewModel */
    private lateinit var viewModel: ASalesOrderItemTextTypeViewModel

    /** The update menu item */
    private lateinit var updateMenuItem: MenuItem

    private val isASalesOrderItemTextTypeValid: Boolean
        get() {
            var isValid = true
            view?.findViewById<LinearLayout>(R.id.create_update_asalesorderitemtexttype)?.let { linearLayout ->
                for (i in 0 until linearLayout.childCount) {
                    val simplePropertyFormCell = linearLayout.getChildAt(i) as SimplePropertyFormCell
                    val propertyName = simplePropertyFormCell.tag as String
                    val property = EntityTypes.aSalesOrderItemTextType.getProperty(propertyName)
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
                    UIConstants.OP_CREATE -> resources.getString(R.string.title_create_fragment, EntityTypes.aSalesOrderItemTextType.localName)
                    else -> resources.getString(R.string.title_update_fragment) + " " + EntityTypes.aSalesOrderItemTextType.localName

                }
            }
        }

        activity?.let {
            (it as ASalesOrderItemTextActivity).isNavigationDisabled = true
            viewModel = ViewModelProvider(it).get(ASalesOrderItemTextTypeViewModel::class.java)
            viewModel.createResult.observe(this, Observer { result -> onComplete(result!!) })
            viewModel.updateResult.observe(this, Observer { result -> onComplete(result!!) })

            if (operation == UIConstants.OP_CREATE) {
                aSalesOrderItemTextTypeEntity = createASalesOrderItemTextType()
            } else {
                aSalesOrderItemTextTypeEntity = viewModel.selectedEntity.value!!
            }

            val workingCopy = savedInstanceState?.getParcelable<ASalesOrderItemTextType>(KEY_WORKING_COPY)
            if (workingCopy == null) {
                aSalesOrderItemTextTypeEntityCopy = aSalesOrderItemTextTypeEntity.copy()
                aSalesOrderItemTextTypeEntityCopy.setEntityTag(aSalesOrderItemTextTypeEntity.getEntityTag())
                aSalesOrderItemTextTypeEntityCopy.setOldEntity(aSalesOrderItemTextTypeEntity)
                aSalesOrderItemTextTypeEntityCopy.editLink = aSalesOrderItemTextTypeEntity.editLink
            } else {
                aSalesOrderItemTextTypeEntityCopy = workingCopy
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
        outState.putParcelable(KEY_WORKING_COPY, aSalesOrderItemTextTypeEntityCopy)
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
        if (!isASalesOrderItemTextTypeValid) {
            return false
        }
        (currentActivity as ASalesOrderItemTextActivity).isNavigationDisabled = false
        progressBar?.visibility = View.VISIBLE
        when (operation) {
            UIConstants.OP_CREATE -> {
                viewModel.create(aSalesOrderItemTextTypeEntityCopy)
            }
            UIConstants.OP_UPDATE -> viewModel.update(aSalesOrderItemTextTypeEntityCopy)
        }
        return true
    }

    /**
     * Create a new ASalesOrderItemTextType instance and initialize properties to its default values
     * Nullable property will remain null
     * For offline, keys will be unset to avoid collision should more than one is created locally
     * @return new ASalesOrderItemTextType instance
     */
    private fun createASalesOrderItemTextType(): ASalesOrderItemTextType {
        val entity = ASalesOrderItemTextType(true)
        entity.unsetDataValue(ASalesOrderItemTextType.salesOrder)
        entity.unsetDataValue(ASalesOrderItemTextType.salesOrderItem)
        entity.unsetDataValue(ASalesOrderItemTextType.language)
        entity.unsetDataValue(ASalesOrderItemTextType.longTextID)
        return entity
    }

    /** Callback function to complete processing when updateResult or createResult events fired */
    private fun onComplete(result: OperationResult<ASalesOrderItemTextType>) {
        progressBar?.visibility = View.INVISIBLE
        enableUpdateMenuItem(true)
        if (result.error != null) {
            (currentActivity as ASalesOrderItemTextActivity).isNavigationDisabled = true
            handleError(result)
        } else {
            if (operation == UIConstants.OP_UPDATE && !currentActivity.resources.getBoolean(R.bool.two_pane)) {
                viewModel.selectedEntity.value = aSalesOrderItemTextTypeEntityCopy
            }
            if (currentActivity.resources.getBoolean(R.bool.two_pane)) {
                val listFragment = currentActivity.supportFragmentManager.findFragmentByTag(UIConstants.LIST_FRAGMENT_TAG)
                (listFragment as ASalesOrderItemTextListFragment).refreshListData()
            }
            (currentActivity as ASalesOrderItemTextActivity).onBackPressed()
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
        binding = FragmentAsalesorderitemtextCreateBinding.inflate(inflater, container, false)
        binding.setASalesOrderItemTextType(aSalesOrderItemTextTypeEntityCopy)
        return binding.root
    }

    /**
     * Notify user of error encountered while execution the operation
     *
     * @param [result] operation result with error
     */
    private fun handleError(result: OperationResult<ASalesOrderItemTextType>) {
        val errorMessage = when (result.operation) {
            OperationResult.Operation.UPDATE -> getString(R.string.update_failed_detail)
            OperationResult.Operation.CREATE -> getString(R.string.create_failed_detail)
            else -> throw AssertionError()
        }
        showError(errorMessage)
    }


    companion object {
        private val KEY_WORKING_COPY = "WORKING_COPY"
        private val LOGGER = LoggerFactory.getLogger(ASalesOrderItemTextActivity::class.java)
    }
}
