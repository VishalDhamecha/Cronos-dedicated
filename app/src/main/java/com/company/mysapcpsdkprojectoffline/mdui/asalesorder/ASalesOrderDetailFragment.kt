package com.company.mysapcpsdkprojectoffline.mdui.asalesorder

import androidx.lifecycle.Observer
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.databinding.FragmentAsalesorderDetailBinding
import com.company.mysapcpsdkprojectoffline.mdui.EntityKeyUtil
import com.company.mysapcpsdkprojectoffline.mdui.InterfacedFragment
import com.company.mysapcpsdkprojectoffline.mdui.UIConstants
import com.company.mysapcpsdkprojectoffline.repository.OperationResult
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesordertype.ASalesOrderTypeViewModel
import com.sap.cloud.android.odata.api_sales_order_srv_entities.API_SALES_ORDER_SRV_EntitiesMetadata.EntitySets;
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderType
import com.sap.cloud.mobile.fiori.indicator.FioriProgressBar
import com.sap.cloud.mobile.fiori.`object`.ObjectHeader
import kotlinx.android.synthetic.main.activity_entityitem.view.*

import com.company.mysapcpsdkprojectoffline.mdui.asalesorderitem.ASalesOrderItemActivity
import com.company.mysapcpsdkprojectoffline.mdui.asalesorderheaderpartner.ASalesOrderHeaderPartnerActivity
import com.company.mysapcpsdkprojectoffline.mdui.aslsordpaymentplanitemdetails.ASlsOrdPaymentPlanItemDetailsActivity
import com.company.mysapcpsdkprojectoffline.mdui.asalesorderheaderprelement.ASalesOrderHeaderPrElementActivity
import com.company.mysapcpsdkprojectoffline.mdui.asalesordertext.ASalesOrderTextActivity

/**
 * A fragment representing a single ASalesOrderType detail screen.
 * This fragment is contained in an ASalesOrderActivity.
 */
class ASalesOrderDetailFragment : InterfacedFragment<ASalesOrderType>() {

    /** Generated data binding class based on layout file */
    private lateinit var binding: FragmentAsalesorderDetailBinding

    /** ASalesOrderType entity to be displayed */
    private lateinit var aSalesOrderTypeEntity: ASalesOrderType

    /** Fiori ObjectHeader component used when entity is to be displayed on phone */
    private var objectHeader: ObjectHeader? = null

    /** View model of the entity type that the displayed entity belongs to */
    private lateinit var viewModel: ASalesOrderTypeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menu = R.menu.itemlist_view_options
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return setupDataBinding(inflater, container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            currentActivity = it
            viewModel = ViewModelProvider(it).get(ASalesOrderTypeViewModel::class.java)
            viewModel.deleteResult.observe(viewLifecycleOwner, Observer { result ->
                onDeleteComplete(result!!)
            })

            viewModel.selectedEntity.observe(viewLifecycleOwner, Observer { entity ->
                aSalesOrderTypeEntity = entity
                binding.setASalesOrderType(entity)
                setupObjectHeader()
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.update_item -> {
                listener?.onFragmentStateChange(UIConstants.EVENT_EDIT_ITEM, aSalesOrderTypeEntity)
                true
            }
            R.id.delete_item -> {
                listener?.onFragmentStateChange(UIConstants.EVENT_ASK_DELETE_CONFIRMATION,null)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Completion callback for delete operation
     *
     * @param [result] of the operation
     */
    private fun onDeleteComplete(result: OperationResult<ASalesOrderType>) {
        progressBar?.let {
            it.visibility = View.INVISIBLE
        }
        viewModel.removeAllSelected()
        result.error?.let {
            showError(getString(R.string.delete_failed_detail))
            return
        }
        listener?.onFragmentStateChange(UIConstants.EVENT_DELETION_COMPLETED, aSalesOrderTypeEntity)
    }


    @Suppress("UNUSED", "UNUSED_PARAMETER") // parameter is needed because of the xml binding
    fun onNavigationClickedToASalesOrderItem_to_Item(view: View) {
        val intent = Intent(currentActivity, ASalesOrderItemActivity::class.java)
        intent.putExtra("parent", aSalesOrderTypeEntity)
        intent.putExtra("navigation", "to_Item")
        startActivity(intent)
    }

    @Suppress("UNUSED", "UNUSED_PARAMETER") // parameter is needed because of the xml binding
    fun onNavigationClickedToASalesOrderHeaderPartner_to_Partner(view: View) {
        val intent = Intent(currentActivity, ASalesOrderHeaderPartnerActivity::class.java)
        intent.putExtra("parent", aSalesOrderTypeEntity)
        intent.putExtra("navigation", "to_Partner")
        startActivity(intent)
    }

    @Suppress("UNUSED", "UNUSED_PARAMETER") // parameter is needed because of the xml binding
    fun onNavigationClickedToASlsOrdPaymentPlanItemDetails_to_PaymentPlanItemDetails(view: View) {
        val intent = Intent(currentActivity, ASlsOrdPaymentPlanItemDetailsActivity::class.java)
        intent.putExtra("parent", aSalesOrderTypeEntity)
        intent.putExtra("navigation", "to_PaymentPlanItemDetails")
        startActivity(intent)
    }

    @Suppress("UNUSED", "UNUSED_PARAMETER") // parameter is needed because of the xml binding
    fun onNavigationClickedToASalesOrderHeaderPrElement_to_PricingElement(view: View) {
        val intent = Intent(currentActivity, ASalesOrderHeaderPrElementActivity::class.java)
        intent.putExtra("parent", aSalesOrderTypeEntity)
        intent.putExtra("navigation", "to_PricingElement")
        startActivity(intent)
    }

    @Suppress("UNUSED", "UNUSED_PARAMETER") // parameter is needed because of the xml binding
    fun onNavigationClickedToASalesOrderText_to_Text(view: View) {
        val intent = Intent(currentActivity, ASalesOrderTextActivity::class.java)
        intent.putExtra("parent", aSalesOrderTypeEntity)
        intent.putExtra("navigation", "to_Text")
        startActivity(intent)
    }

    /**
     * Set up databinding for this view
     *
     * @param [inflater] layout inflater from onCreateView
     * @param [container] view group from onCreateView
     *
     * @return [View] rootView from generated databinding code
     */
    private fun setupDataBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentAsalesorderDetailBinding.inflate(inflater, container, false)
        binding.handler = this
        return binding.root
    }

    /**
     * Set detail image of ObjectHeader.
     * When the entity does not provides picture, set the first character of the masterProperty.
     */
    private fun setDetailImage(objectHeader: ObjectHeader, aSalesOrderTypeEntity: ASalesOrderType) {
        if (aSalesOrderTypeEntity.getDataValue(ASalesOrderType.salesOrderType) != null && aSalesOrderTypeEntity.getDataValue(ASalesOrderType.salesOrderType).toString().isNotEmpty()) {
            objectHeader.detailImageCharacter = aSalesOrderTypeEntity.getDataValue(ASalesOrderType.salesOrderType).toString().substring(0, 1)
        } else {
            objectHeader.detailImageCharacter = "?"
        }
    }

    /**
     * Setup ObjectHeader with an instance of aSalesOrderTypeEntity
     */
    private fun setupObjectHeader() {
        val secondToolbar = currentActivity.findViewById<Toolbar>(R.id.secondaryToolbar)
        if (secondToolbar != null) {
            secondToolbar.setTitle(aSalesOrderTypeEntity.entityType.localName)
        } else {
            currentActivity.setTitle(aSalesOrderTypeEntity.entityType.localName)
        }

        // Object Header is not available in tablet mode
        objectHeader = currentActivity.findViewById(R.id.objectHeader)
        val dataValue = aSalesOrderTypeEntity.getDataValue(ASalesOrderType.salesOrderType)

        objectHeader?.let {
            it.apply {
                headline = dataValue?.toString()
                subheadline = EntityKeyUtil.getOptionalEntityKey(aSalesOrderTypeEntity)
                body = "You can set the header body text here."
                footnote = "You can set the header footnote here."
                description = "You can add a detailed item description here."
            }
            it.setTag("#tag1", 0)
            it.setTag("#tag3", 2)
            it.setTag("#tag2", 1)

            setDetailImage(it, aSalesOrderTypeEntity)
            it.visibility = View.VISIBLE
        }
    }
}
