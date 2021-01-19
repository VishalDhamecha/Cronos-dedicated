package com.company.mysapcpsdkprojectoffline.mdui

import android.content.Intent
import android.graphics.Paint
import android.graphics.PorterDuff
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.application.isradeleon.notify.Notify
import com.bumptech.glide.Glide
import com.company.mysapcpsdkproject.datamodel.OrderLineModel
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.app.SAPWizardApplication
import com.company.mysapcpsdkprojectoffline.mdui.adapter.DocumentAdapter
import com.company.mysapcpsdkprojectoffline.mdui.adapter.SalesOrderLineAdapter
import com.company.mysapcpsdkprojectoffline.mdui.datamodel.DocumentModel
import com.company.mysapcpsdkprojectoffline.util.*
import com.company.mysapcpsdkprojectoffline.util.extension.*
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesordertype.ASalesOrderTypeViewModel
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.irozon.sneaker.Sneaker
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderType
import kotlinx.android.synthetic.main.activity_sales_order_detail.*
import kotlinx.android.synthetic.main.bottomsheet_delete_document.view.*
import kotlinx.android.synthetic.main.bottomsheet_delete_salesorder.view.*
import kotlinx.android.synthetic.main.item_menu.view.*
import kotlinx.android.synthetic.main.topsheet_internet_connected.view.*
import java.util.*

class SalesOrderDetailActivity : BaseActivity(), View.OnClickListener {

    private var salesOrderLine: MutableList<OrderLineModel> = arrayListOf()
    private var documents: MutableList<DocumentModel> = arrayListOf()
    private lateinit var aSalesOrderTypeEntity: ASalesOrderType
    private lateinit var deleteDocBottomSheetView: View
    private lateinit var deleteDocBottomSheetDialog: BottomSheetDialog
    private lateinit var deleteOrderBottomSheetView: View
    private lateinit var deleteOrderBottomSheetDialog: BottomSheetDialog
    private lateinit var documentAdapter: DocumentAdapter
    private var editDocIndex = 0

    /** View model of the entity type that the displayed entity belongs to */
    private lateinit var viewModel: ASalesOrderTypeViewModel

    override fun getLayoutResourceId(): Int = R.layout.activity_sales_order_detail

    override fun main() {

        viewModel = ViewModelProvider(this).get(ASalesOrderTypeViewModel::class.java)

        aSalesOrderTypeEntity = intent.getParcelableExtra("sales_order") as ASalesOrderType

        salesOrderLine.add(OrderLineModel("Ship cargo container B026", "", "", ""))
        salesOrderLine.add(OrderLineModel("Shipping container Air vent (large)", "", "", ""))
        salesOrderLine.add(OrderLineModel("Shipping Container levelling Pads", "", "", ""))

        documents.add(DocumentModel("ISO Certificate", "PDF, 546 kb", false))
        documents.add(DocumentModel("Certificate of origin", "PDF, 546 kb", false))

        orderLineCountTV.text = "(${salesOrderLine.size})"
        documentsCountTV.text = "(${documents.size})"

        orderLineREC.adapter = SalesOrderLineAdapter(this, salesOrderLine)

        documentAdapter = DocumentAdapter(this, documents, onDocDeleteClick = {
            showDeleteDocBottomSheet()
        }, onDocEditClick = {
            editDocIndex = it
            openActivityForResult(EditDocumentsActivity::class.java, EDIT_DOC_CODE) {
                putSerializable("document", documents[it])
            }
        })

        documentsREC.adapter = documentAdapter

        addDocumentTV.paintFlags = addDocumentTV.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        orderLineRL.setOnClickListener(this)
        documentsRL.setOnClickListener(this)
        moreIV.setOnClickListener(this)
        backIV.setOnClickListener(this)
        supplierNameLL.setOnClickListener(this)
        addDocumentLL.setOnClickListener(this)

        setNetworkListener()

        viewModel.deleteResult.observe(this, { result ->
            result.error?.let {
                Toast.makeText(this, getString(R.string.delete_failed_detail), Toast.LENGTH_SHORT).show()
                return@observe
            }
            viewModel.removeAllSelected()
            setResult(DELETE_ORDER_RESULT_CODE)
            finish()
        })
        setData()


    }

    private fun setNetworkListener() {
        isNetworkConnected().observe(this, {
            if (!it && !SAPWizardApplication.isNetworkLost) {
                val sneaker = Sneaker.with(this).autoHide(true).setDuration(5000)
                val view = LayoutInflater.from(this).inflate(R.layout.topsheet_no_internet, sneaker.getView(), false)
                sneaker.sneakCustom(view)
                SAPWizardApplication.isNetworkLost = true
            } else if (it && SAPWizardApplication.isNetworkLost) {
                val sneaker = Sneaker.with(this).autoHide(true).setDuration(5000)
                val view = LayoutInflater.from(this).inflate(R.layout.topsheet_internet_connected, sneaker.getView(), false)
                sneaker.sneakCustom(view)
                SAPWizardApplication.isNetworkLost = false
                Util.scheduleNotification(this)
            }
        })
    }

    private fun setData() {

        customerNameTV.text = aSalesOrderTypeEntity.createdByUser
        orderCreationDateTV.text = "${aSalesOrderTypeEntity.creationDate!!.day}/${aSalesOrderTypeEntity.creationDate!!.month}/${aSalesOrderTypeEntity.creationDate!!.year}"
        orderCreatedByTV.text = aSalesOrderTypeEntity.createdByUser
//        cargoContainerTypeTV.text = aSalesOrderTypeEntity.shippingType
//        orderLastChangeDateTV.text = "${aSalesOrderTypeEntity.lastChangeDate!!.day}/${aSalesOrderTypeEntity.lastChangeDate!!.month}/${aSalesOrderTypeEntity.lastChangeDate!!.year}"

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.orderLineRL -> {
                if (orderLineREC.isVisible()) {
                    orderLineREC.gone()
                    Glide.with(this).load(R.drawable.ic_down_arrow).into(orderLineIV)
                } else {
                    orderLineREC.visible()
                    Glide.with(this).load(R.drawable.ic_up_arrow).into(orderLineIV)
                }
            }
            R.id.documentsRL -> {
                if (documentsREC.isVisible()) {
                    documentsREC.gone()
                    addDocumentLL.gone()
                    Glide.with(this).load(R.drawable.ic_down_arrow).into(documentsIV)
                } else {
                    documentsREC.visible()
                    addDocumentLL.visible()
                    Glide.with(this).load(R.drawable.ic_up_arrow).into(documentsIV)
                }
            }
            R.id.moreIV -> {
                showSalesMoreOption()
            }
            R.id.backIV -> {
                onBackPressed()
            }
            R.id.supplierNameLL -> {
                openActivity(SupplierDetailActivity::class.java) {
                    putParcelable("sales_order", aSalesOrderTypeEntity)
                }
            }
            R.id.addDocumentLL -> {
                openActivityForResult(EditDocumentsActivity::class.java, ADD_DOC_CODE)
            }
        }
    }

    private fun showSalesMoreOption() {
        val popupMenu = popupMenu {
            dropDownHorizontalOffset = -5
            dropDownVerticalOffset = -10
            dropdownGravity = Gravity.BOTTOM or Gravity.END
            section {
                customItem {
                    layoutResId = R.layout.item_menu
                    viewBoundCallback = { view ->
                        view.optionTV.text = getString(R.string.edit)
                        Glide.with(this@SalesOrderDetailActivity).load(R.drawable.ic_edit).into(view.optionIV)
                    }
                    callback = {
                        openActivityForResult(EditSalesOrderActivity::class.java, EDIT_ORDER_CODE) {
                            putParcelable("sales_order", aSalesOrderTypeEntity)
                        }
                    }
                }

            }
            section {
                customItem {
                    layoutResId = R.layout.item_menu
                    viewBoundCallback = { view ->
                        view.optionTV.text = getString(R.string.copy)
                        Glide.with(this@SalesOrderDetailActivity).load(R.drawable.ic_copy).into(view.optionIV)
                    }
                    callback = {

                    }
                }
            }

            section {
                customItem {
                    layoutResId = R.layout.item_menu
                    viewBoundCallback = { view ->
                        view.optionTV.text = getString(R.string.delete)
                        Glide.with(this@SalesOrderDetailActivity).load(R.drawable.ic_trash).into(view.optionIV)
                    }
                    callback = {
                        showDeleteOrderBottomSheet()
                    }
                }
            }

            section {
                customItem {
                    layoutResId = R.layout.item_menu
                    viewBoundCallback = { view ->
                        view.optionTV.text = getString(R.string.download)
                        Glide.with(this@SalesOrderDetailActivity).load(R.drawable.ic_download).into(view.optionIV)
                    }
                    callback = {

                    }
                }
            }

            section {
                customItem {
                    layoutResId = R.layout.item_menu
                    viewBoundCallback = { view ->
                        view.optionTV.text = getString(R.string.preview)
                        Glide.with(this@SalesOrderDetailActivity).load(R.drawable.ic_preview).into(view.optionIV)
                    }
                    callback = {

                    }
                }
            }

            section {
                customItem {
                    layoutResId = R.layout.item_menu
                    viewBoundCallback = { view ->
                        view.optionTV.text = getString(R.string.get_link)
                        Glide.with(this@SalesOrderDetailActivity).load(R.drawable.ic_link).into(view.optionIV)
                    }
                    callback = {

                    }
                }
            }
        }
        popupMenu.setOnDismissListener {
            moreIV.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
            moreIV.setBackgroundResource(0)
        }
        popupMenu.show(this, moreIV)
        moreIV.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        moreIV.setBackgroundResource(R.drawable.rounded_background_white)

    }

    private fun showDeleteDocBottomSheet() {

        deleteDocBottomSheetView = LayoutInflater.from(this).inflate(
                R.layout.bottomsheet_delete_document,
                null
        )

        deleteDocBottomSheetDialog = BottomSheetDialog(this, R.style.SheetDialog)
        deleteDocBottomSheetDialog.setContentView(deleteDocBottomSheetView)

        deleteDocBottomSheetView.backTV.paintFlags = addDocumentTV.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        deleteDocBottomSheetDialog.show()


        deleteDocBottomSheetView.backTV.setOnClickListener {
            deleteDocBottomSheetDialog.dismiss()
        }

    }

    private fun showDeleteOrderBottomSheet() {

        deleteOrderBottomSheetView = LayoutInflater.from(this).inflate(
                R.layout.bottomsheet_delete_salesorder,
                null
        )

        deleteOrderBottomSheetDialog = BottomSheetDialog(this, R.style.SheetDialog)
        deleteOrderBottomSheetDialog.setContentView(deleteOrderBottomSheetView)

        deleteOrderBottomSheetView.cancelTV.paintFlags = addDocumentTV.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        deleteOrderBottomSheetDialog.show()


        deleteOrderBottomSheetView.cancelTV.setOnClickListener {
            deleteOrderBottomSheetDialog.dismiss()
        }

        deleteOrderBottomSheetView.deleteTV.setOnClickListener {
            val list: MutableList<ASalesOrderType> = arrayListOf()
            list.add(aSalesOrderTypeEntity)
            viewModel.delete(list)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_DOC_CODE && resultCode == RESULT_OK && data != null) {
            documents[editDocIndex].documentName = data.getStringExtra("document_name") ?: ""
            documents[editDocIndex].isEdited = true
            documentAdapter.notifyItemChanged(editDocIndex)

            isNetworkConnected().observe(this, {

                if (it) {
                    val sneaker = Sneaker.with(this).autoHide(true).setDuration(3000)
                    val view = LayoutInflater.from(this).inflate(R.layout.topsheet_internet_connected, sneaker.getView(), false)
                    Glide.with(this).load(R.drawable.ic_tick_white).into(view.titleIV)
                    view.orderCreationTV.text = "Document successfully updated"
                    view.infoIV.gone()
                    sneaker.sneakCustom(view)
                } else if (SAPWizardApplication.isNetworkLost) {
                    val sneaker = Sneaker.with(this).autoHide(true).setDuration(3000)
                    val view = LayoutInflater.from(this).inflate(R.layout.topsheet_internet_connected, sneaker.getView(), false)
                    Glide.with(this).load(R.drawable.ic_data_not_sync).into(view.titleIV)
                    view.orderCreationTV.text = "Document saved successfully to your device.  Changes will be synced to the server when the network connection is re-established"
                    view.infoIV.gone()
                    sneaker.sneakCustom(view)
                }
            })
        } else if (requestCode == ADD_DOC_CODE && resultCode == RESULT_OK && data != null) {
            documents.add(DocumentModel(data.getStringExtra("document_name")
                    ?: "", "PDF, 546 kb", true))
            documentAdapter.notifyItemInserted(documents.size - 1)
            documentsCountTV.text = "(${documents.size})"

            isNetworkConnected().observe(this, {

                if (it) {
                    val sneaker = Sneaker.with(this).autoHide(true).setDuration(3000)
                    val view = LayoutInflater.from(this).inflate(R.layout.topsheet_internet_connected, sneaker.getView(), false)
                    Glide.with(this).load(R.drawable.ic_tick_white).into(view.titleIV)
                    view.orderCreationTV.text = "Document successfully added"
                    view.infoIV.gone()
                    sneaker.sneakCustom(view)
                } else if (SAPWizardApplication.isNetworkLost) {
                    val sneaker = Sneaker.with(this).autoHide(true).setDuration(3000)
                    val view = LayoutInflater.from(this).inflate(R.layout.topsheet_internet_connected, sneaker.getView(), false)
                    Glide.with(this).load(R.drawable.ic_data_not_sync).into(view.titleIV)
                    view.orderCreationTV.text = "Document saved successfully to your device.  Changes will be synced to the server when the network connection is re-established"
                    view.infoIV.gone()
                    sneaker.sneakCustom(view)
                }
            })
        } else if (requestCode == EDIT_ORDER_CODE && resultCode == RESULT_OK && data != null) {
            aSalesOrderTypeEntity = data.getParcelableExtra("sales_order") as ASalesOrderType

            customerNameTV.text = aSalesOrderTypeEntity.createdByUser
            orderCreationDateTV.text = "${aSalesOrderTypeEntity.creationDate!!.day}/${aSalesOrderTypeEntity.creationDate!!.month}/${aSalesOrderTypeEntity.creationDate!!.year}"
            orderCreatedByTV.text = aSalesOrderTypeEntity.createdByUser
            setResult(RESULT_OK, data)

            isNetworkConnected().observe(this, {

                if (it) {
                    val sneaker = Sneaker.with(this).autoHide(true).setDuration(3000)
                    val view = LayoutInflater.from(this).inflate(R.layout.topsheet_internet_connected, sneaker.getView(), false)
                    Glide.with(this).load(R.drawable.ic_tick_white).into(view.titleIV)
                    view.orderCreationTV.text = "Order successfully updated"
                    view.infoIV.gone()
                    sneaker.sneakCustom(view)
                } else if (SAPWizardApplication.isNetworkLost) {
                    val sneaker = Sneaker.with(this).autoHide(true).setDuration(3000)
                    val view = LayoutInflater.from(this).inflate(R.layout.topsheet_internet_connected, sneaker.getView(), false)
                    Glide.with(this).load(R.drawable.ic_data_not_sync).into(view.titleIV)
                    view.orderCreationTV.text = "Order saved successfully to your device. Changes will be synced to the server when the network connection is re-established"
                    view.infoIV.gone()
                    sneaker.sneakCustom(view)
                }
            })
        }
        setNetworkListener()
    }

}