package com.company.mysapcpsdkprojectoffline.mdui.aslsordpaymentplanitemdetails

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.databinding.FragmentAslsordpaymentplanitemdetailsCreateBinding
import com.company.mysapcpsdkprojectoffline.mdui.BundleKeys
import com.company.mysapcpsdkprojectoffline.mdui.InterfacedFragment
import com.company.mysapcpsdkprojectoffline.mdui.UIConstants
import com.company.mysapcpsdkprojectoffline.repository.OperationResult
import com.company.mysapcpsdkprojectoffline.viewmodel.aslsordpaymentplanitemdetailstype.ASlsOrdPaymentPlanItemDetailsTypeViewModel
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASlsOrdPaymentPlanItemDetailsType
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
 * This fragment is either contained in a [ASlsOrdPaymentPlanItemDetailsListActivity] in two-pane mode (on tablets) or a
 * [ASlsOrdPaymentPlanItemDetailsDetailActivity] on handsets.
 *
 * Arguments: Operation: [OP_CREATE | OP_UPDATE]
 *            ASlsOrdPaymentPlanItemDetailsType if Operation is update
 */
class ASlsOrdPaymentPlanItemDetailsCreateFragment : InterfacedFragment<ASlsOrdPaymentPlanItemDetailsType>() {

    /** ASlsOrdPaymentPlanItemDetailsType object and it's copy: the modifications are done on the copied object. */
    private lateinit var aSlsOrdPaymentPlanItemDetailsTypeEntity: ASlsOrdPaymentPlanItemDetailsType
    private lateinit var aSlsOrdPaymentPlanItemDetailsTypeEntityCopy: ASlsOrdPaymentPlanItemDetailsType

    /** DataBinding generated class */
    private lateinit var binding: FragmentAslsordpaymentplanitemdetailsCreateBinding

    /** Indicate what operation to be performed */
    private lateinit var operation: String

    /** aSlsOrdPaymentPlanItemDetailsTypeEntity ViewModel */
    private lateinit var viewModel: ASlsOrdPaymentPlanItemDetailsTypeViewModel

    /** The update menu item */
    private lateinit var updateMenuItem: MenuItem

    private val isASlsOrdPaymentPlanItemDetailsTypeValid: Boolean
        get() {
            var isValid = true
            view?.findViewById<LinearLayout>(R.id.create_update_aslsordpaymentplanitemdetailstype)?.let { linearLayout ->
                for (i in 0 until linearLayout.childCount) {
                    val simplePropertyFormCell = linearLayout.getChildAt(i) as SimplePropertyFormCell
                    val propertyName = simplePropertyFormCell.tag as String
                    val property = EntityTypes.aSlsOrdPaymentPlanItemDetailsType.getProperty(propertyName)
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
                    UIConstants.OP_CREATE -> resources.getString(R.string.title_create_fragment, EntityTypes.aSlsOrdPaymentPlanItemDetailsType.localName)
                    else -> resources.getString(R.string.title_update_fragment) + " " + EntityTypes.aSlsOrdPaymentPlanItemDetailsType.localName

                }
            }
        }

        activity?.let {
            (it as ASlsOrdPaymentPlanItemDetailsActivity).isNavigationDisabled = true
            viewModel = ViewModelProvider(it).get(ASlsOrdPaymentPlanItemDetailsTypeViewModel::class.java)
            viewModel.createResult.observe(this, Observer { result -> onComplete(result!!) })
            viewModel.updateResult.observe(this, Observer { result -> onComplete(result!!) })

            if (operation == UIConstants.OP_CREATE) {
                aSlsOrdPaymentPlanItemDetailsTypeEntity = createASlsOrdPaymentPlanItemDetailsType()
            } else {
                aSlsOrdPaymentPlanItemDetailsTypeEntity = viewModel.selectedEntity.value!!
            }

            val workingCopy = savedInstanceState?.getParcelable<ASlsOrdPaymentPlanItemDetailsType>(KEY_WORKING_COPY)
            if (workingCopy == null) {
                aSlsOrdPaymentPlanItemDetailsTypeEntityCopy = aSlsOrdPaymentPlanItemDetailsTypeEntity.copy()
                aSlsOrdPaymentPlanItemDetailsTypeEntityCopy.setEntityTag(aSlsOrdPaymentPlanItemDetailsTypeEntity.getEntityTag())
                aSlsOrdPaymentPlanItemDetailsTypeEntityCopy.setOldEntity(aSlsOrdPaymentPlanItemDetailsTypeEntity)
                aSlsOrdPaymentPlanItemDetailsTypeEntityCopy.editLink = aSlsOrdPaymentPlanItemDetailsTypeEntity.editLink
            } else {
                aSlsOrdPaymentPlanItemDetailsTypeEntityCopy = workingCopy
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
        outState.putParcelable(KEY_WORKING_COPY, aSlsOrdPaymentPlanItemDetailsTypeEntityCopy)
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
        if (!isASlsOrdPaymentPlanItemDetailsTypeValid) {
            return false
        }
        (currentActivity as ASlsOrdPaymentPlanItemDetailsActivity).isNavigationDisabled = false
        progressBar?.visibility = View.VISIBLE
        when (operation) {
            UIConstants.OP_CREATE -> {
                viewModel.create(aSlsOrdPaymentPlanItemDetailsTypeEntityCopy)
            }
            UIConstants.OP_UPDATE -> viewModel.update(aSlsOrdPaymentPlanItemDetailsTypeEntityCopy)
        }
        return true
    }

    /**
     * Create a new ASlsOrdPaymentPlanItemDetailsType instance and initialize properties to its default values
     * Nullable property will remain null
     * For offline, keys will be unset to avoid collision should more than one is created locally
     * @return new ASlsOrdPaymentPlanItemDetailsType instance
     */
    private fun createASlsOrdPaymentPlanItemDetailsType(): ASlsOrdPaymentPlanItemDetailsType {
        val entity = ASlsOrdPaymentPlanItemDetailsType(true)
        entity.unsetDataValue(ASlsOrdPaymentPlanItemDetailsType.salesOrder)
        entity.unsetDataValue(ASlsOrdPaymentPlanItemDetailsType.paymentPlanItem)
        return entity
    }

    /** Callback function to complete processing when updateResult or createResult events fired */
    private fun onComplete(result: OperationResult<ASlsOrdPaymentPlanItemDetailsType>) {
        progressBar?.visibility = View.INVISIBLE
        enableUpdateMenuItem(true)
        if (result.error != null) {
            (currentActivity as ASlsOrdPaymentPlanItemDetailsActivity).isNavigationDisabled = true
            handleError(result)
        } else {
            if (operation == UIConstants.OP_UPDATE && !currentActivity.resources.getBoolean(R.bool.two_pane)) {
                viewModel.selectedEntity.value = aSlsOrdPaymentPlanItemDetailsTypeEntityCopy
            }
            if (currentActivity.resources.getBoolean(R.bool.two_pane)) {
                val listFragment = currentActivity.supportFragmentManager.findFragmentByTag(UIConstants.LIST_FRAGMENT_TAG)
                (listFragment as ASlsOrdPaymentPlanItemDetailsListFragment).refreshListData()
            }
            (currentActivity as ASlsOrdPaymentPlanItemDetailsActivity).onBackPressed()
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
        binding = FragmentAslsordpaymentplanitemdetailsCreateBinding.inflate(inflater, container, false)
        binding.setASlsOrdPaymentPlanItemDetailsType(aSlsOrdPaymentPlanItemDetailsTypeEntityCopy)
        return binding.root
    }

    /**
     * Notify user of error encountered while execution the operation
     *
     * @param [result] operation result with error
     */
    private fun handleError(result: OperationResult<ASlsOrdPaymentPlanItemDetailsType>) {
        val errorMessage = when (result.operation) {
            OperationResult.Operation.UPDATE -> getString(R.string.update_failed_detail)
            OperationResult.Operation.CREATE -> getString(R.string.create_failed_detail)
            else -> throw AssertionError()
        }
        showError(errorMessage)
    }


    companion object {
        private val KEY_WORKING_COPY = "WORKING_COPY"
        private val LOGGER = LoggerFactory.getLogger(ASlsOrdPaymentPlanItemDetailsActivity::class.java)
    }
}
