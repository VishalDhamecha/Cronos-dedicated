package com.company.mysapcpsdkprojectoffline.mdui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.app.SAPWizardApplication
import com.company.mysapcpsdkprojectoffline.mdui.asalesorder.ASalesOrderActivity
import com.company.mysapcpsdkprojectoffline.mdui.asalesorder.ASalesOrderListFragment
import com.company.mysapcpsdkprojectoffline.util.Util
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesordertype.ASalesOrderTypeViewModel
import com.irozon.sneaker.Sneaker
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderType
import kotlinx.android.synthetic.main.activity_edit_sales_order.*
import java.math.BigDecimal

class EditSalesOrderActivity : BaseActivity(), View.OnClickListener {

    private lateinit var aSalesOrderTypeEntity: ASalesOrderType
    private lateinit var aSalesOrderTypeEntityCopy: ASalesOrderType

    /** aSalesOrderTypeEntity ViewModel */
    private lateinit var viewModel: ASalesOrderTypeViewModel


    override fun getLayoutResourceId(): Int = R.layout.activity_edit_sales_order

    override fun main() {

        aSalesOrderTypeEntity = intent.getParcelableExtra("sales_order") as ASalesOrderType

        aSalesOrderTypeEntityCopy = aSalesOrderTypeEntity.copy()
        aSalesOrderTypeEntityCopy.entityTag = aSalesOrderTypeEntity.entityTag
        aSalesOrderTypeEntityCopy.oldEntity = aSalesOrderTypeEntity
        aSalesOrderTypeEntityCopy.editLink = aSalesOrderTypeEntity.editLink

        customerNameET.setText(aSalesOrderTypeEntity.createdByUser)
        orderAmountET.setText("${aSalesOrderTypeEntity.totalNetAmount}")
        saveOrderBTN.setOnClickListener(this)
        saveTV.setOnClickListener(this)
        cancelTV.setOnClickListener(this)

        viewModel = ViewModelProvider(this).get(ASalesOrderTypeViewModel::class.java)
        viewModel.createResult.observe(this, { result ->


        })
        viewModel.updateResult.observe(this, { result ->
            if (result.error != null) {
                Toast.makeText(this, result.error!!.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                viewModel.selectedEntity.value = aSalesOrderTypeEntityCopy
            }

        })


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

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.saveOrderBTN -> {
                aSalesOrderTypeEntityCopy.totalNetAmount = BigDecimal(orderAmountET.text.toString().trim())
                aSalesOrderTypeEntityCopy.createdByUser = customerNameET.text.toString().trim()
                viewModel.update(aSalesOrderTypeEntityCopy)
                val intent = Intent().putExtra("sales_order", aSalesOrderTypeEntityCopy)
                setResult(RESULT_OK, intent)
                finish()
            }
            R.id.saveTV -> {
                saveOrderBTN.performClick()
            }
            R.id.cancelTV -> {
                onBackPressed()
            }
        }

    }
}