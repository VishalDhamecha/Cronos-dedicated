package com.company.mysapcpsdkprojectoffline.mdui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.company.mysapcpsdkprojectoffline.mdui.SalesOrderDetailActivity
import com.company.mysapcpsdkprojectoffline.mdui.adapter.SalesOrderListAdapter
import com.company.mysapcpsdkprojectoffline.util.extension.openActivity
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.app.SAPWizardApplication
import com.company.mysapcpsdkprojectoffline.service.SAPServiceManager
import com.company.mysapcpsdkprojectoffline.util.DELETE_ORDER_RESULT_CODE
import com.company.mysapcpsdkprojectoffline.util.EDIT_ORDER_CODE
import com.company.mysapcpsdkprojectoffline.util.Util
import com.company.mysapcpsdkprojectoffline.util.extension.openActivityForResult
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesordertype.ASalesOrderTypeViewModel
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderType
import kotlinx.android.synthetic.main.fragment_sales_order.*


class SalesOrderFragment : Fragment() {

    private var salesOrderList: MutableList<ASalesOrderType> = arrayListOf()

    /**
     * Service manager to provide root URL of OData Service for Glide to load images if there are media resources
     * associated with the entity type
     */
    private var sapServiceManager: SAPServiceManager? = null

    /**
     * List adapter to be used with RecyclerView containing all instances of aSalesOrder
     */
    private var adapter: SalesOrderListAdapter? = null

    /**
     * View model of the entity type
     */
    private lateinit var viewModel: ASalesOrderTypeViewModel

    /**
     * list item click position
     */
    private var listItemClickPosition = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sales_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sapServiceManager = (requireActivity().application as SAPWizardApplication).sapServiceManager


        Util.showProgressDialog(requireContext())
        prepareViewModel()


    }

    /** Initializes the view model and add observers on it */
    private fun prepareViewModel() {
        viewModel = ViewModelProvider(this).get(ASalesOrderTypeViewModel::class.java).also {
            it.initialRead { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.observableItems.observe(viewLifecycleOwner, { items ->

            Util.hideProgressDialog()
            salesOrderList.addAll(items)
            adapter = SalesOrderListAdapter(requireContext(), salesOrderList, onItemClick = {
                openActivityForResult(SalesOrderDetailActivity::class.java, EDIT_ORDER_CODE) {
                    listItemClickPosition = it
                    putParcelable("sales_order", salesOrderList[it])
                    viewModel.setSelectedEntity(salesOrderList[it])
                }
            })
            salesOrderRV.adapter = adapter
            totalOrderTV.text = "Sales orders (${salesOrderList.size})"
        })

        viewModel.readResult.observe(viewLifecycleOwner, {


        })

        viewModel.deleteResult.observe(viewLifecycleOwner, {

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EDIT_ORDER_CODE && resultCode == RESULT_OK && data != null) {
            val aSalesOrderTypeEntity = data.getParcelableExtra("sales_order") as ASalesOrderType
            salesOrderList[listItemClickPosition] = aSalesOrderTypeEntity
            adapter?.notifyItemChanged(listItemClickPosition)
        } else if (requestCode == EDIT_ORDER_CODE && resultCode == DELETE_ORDER_RESULT_CODE) {
            salesOrderList.removeAt(listItemClickPosition)
            adapter?.notifyItemRemoved(listItemClickPosition)
        }

    }
}