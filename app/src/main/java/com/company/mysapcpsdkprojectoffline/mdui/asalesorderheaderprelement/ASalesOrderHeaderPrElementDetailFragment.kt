package com.company.mysapcpsdkprojectoffline.mdui.asalesorderheaderprelement

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
import com.company.mysapcpsdkprojectoffline.databinding.FragmentAsalesorderheaderprelementDetailBinding
import com.company.mysapcpsdkprojectoffline.mdui.EntityKeyUtil
import com.company.mysapcpsdkprojectoffline.mdui.InterfacedFragment
import com.company.mysapcpsdkprojectoffline.mdui.UIConstants
import com.company.mysapcpsdkprojectoffline.repository.OperationResult
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesorderheaderprelementtype.ASalesOrderHeaderPrElementTypeViewModel
import com.sap.cloud.android.odata.api_sales_order_srv_entities.API_SALES_ORDER_SRV_EntitiesMetadata.EntitySets;
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderHeaderPrElementType
import com.sap.cloud.mobile.fiori.indicator.FioriProgressBar
import com.sap.cloud.mobile.fiori.`object`.ObjectHeader
import kotlinx.android.synthetic.main.activity_entityitem.view.*

import com.company.mysapcpsdkprojectoffline.mdui.asalesorder.ASalesOrderActivity

/**
 * A fragment representing a single ASalesOrderHeaderPrElementType detail screen.
 * This fragment is contained in an ASalesOrderHeaderPrElementActivity.
 */
class ASalesOrderHeaderPrElementDetailFragment : InterfacedFragment<ASalesOrderHeaderPrElementType>() {

    /** Generated data binding class based on layout file */
    private lateinit var binding: FragmentAsalesorderheaderprelementDetailBinding

    /** ASalesOrderHeaderPrElementType entity to be displayed */
    private lateinit var aSalesOrderHeaderPrElementTypeEntity: ASalesOrderHeaderPrElementType

    /** Fiori ObjectHeader component used when entity is to be displayed on phone */
    private var objectHeader: ObjectHeader? = null

    /** View model of the entity type that the displayed entity belongs to */
    private lateinit var viewModel: ASalesOrderHeaderPrElementTypeViewModel
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
            viewModel = ViewModelProvider(it).get(ASalesOrderHeaderPrElementTypeViewModel::class.java)
            viewModel.deleteResult.observe(viewLifecycleOwner, Observer { result ->
                onDeleteComplete(result!!)
            })

            viewModel.selectedEntity.observe(viewLifecycleOwner, Observer { entity ->
                aSalesOrderHeaderPrElementTypeEntity = entity
                binding.setASalesOrderHeaderPrElementType(entity)
                setupObjectHeader()
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.update_item -> {
                listener?.onFragmentStateChange(UIConstants.EVENT_EDIT_ITEM, aSalesOrderHeaderPrElementTypeEntity)
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
    private fun onDeleteComplete(result: OperationResult<ASalesOrderHeaderPrElementType>) {
        progressBar?.let {
            it.visibility = View.INVISIBLE
        }
        viewModel.removeAllSelected()
        result.error?.let {
            showError(getString(R.string.delete_failed_detail))
            return
        }
        listener?.onFragmentStateChange(UIConstants.EVENT_DELETION_COMPLETED, aSalesOrderHeaderPrElementTypeEntity)
    }


    @Suppress("UNUSED", "UNUSED_PARAMETER") // parameter is needed because of the xml binding
    fun onNavigationClickedToASalesOrder_to_SalesOrder(view: View) {
        val intent = Intent(currentActivity, ASalesOrderActivity::class.java)
        intent.putExtra("parent", aSalesOrderHeaderPrElementTypeEntity)
        intent.putExtra("navigation", "to_SalesOrder")
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
        binding = FragmentAsalesorderheaderprelementDetailBinding.inflate(inflater, container, false)
        binding.handler = this
        return binding.root
    }

    /**
     * Set detail image of ObjectHeader.
     * When the entity does not provides picture, set the first character of the masterProperty.
     */
    private fun setDetailImage(objectHeader: ObjectHeader, aSalesOrderHeaderPrElementTypeEntity: ASalesOrderHeaderPrElementType) {
        if (aSalesOrderHeaderPrElementTypeEntity.getDataValue(ASalesOrderHeaderPrElementType.conditionType) != null && aSalesOrderHeaderPrElementTypeEntity.getDataValue(ASalesOrderHeaderPrElementType.conditionType).toString().isNotEmpty()) {
            objectHeader.detailImageCharacter = aSalesOrderHeaderPrElementTypeEntity.getDataValue(ASalesOrderHeaderPrElementType.conditionType).toString().substring(0, 1)
        } else {
            objectHeader.detailImageCharacter = "?"
        }
    }

    /**
     * Setup ObjectHeader with an instance of aSalesOrderHeaderPrElementTypeEntity
     */
    private fun setupObjectHeader() {
        val secondToolbar = currentActivity.findViewById<Toolbar>(R.id.secondaryToolbar)
        if (secondToolbar != null) {
            secondToolbar.setTitle(aSalesOrderHeaderPrElementTypeEntity.entityType.localName)
        } else {
            currentActivity.setTitle(aSalesOrderHeaderPrElementTypeEntity.entityType.localName)
        }

        // Object Header is not available in tablet mode
        objectHeader = currentActivity.findViewById(R.id.objectHeader)
        val dataValue = aSalesOrderHeaderPrElementTypeEntity.getDataValue(ASalesOrderHeaderPrElementType.conditionType)

        objectHeader?.let {
            it.apply {
                headline = dataValue?.toString()
                subheadline = EntityKeyUtil.getOptionalEntityKey(aSalesOrderHeaderPrElementTypeEntity)
                body = "You can set the header body text here."
                footnote = "You can set the header footnote here."
                description = "You can add a detailed item description here."
            }
            it.setTag("#tag1", 0)
            it.setTag("#tag3", 2)
            it.setTag("#tag2", 1)

            setDetailImage(it, aSalesOrderHeaderPrElementTypeEntity)
            it.visibility = View.VISIBLE
        }
    }
}
