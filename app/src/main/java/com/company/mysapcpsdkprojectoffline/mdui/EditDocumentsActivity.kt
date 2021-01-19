package com.company.mysapcpsdkprojectoffline.mdui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import com.company.mysapcpsdkprojectoffline.util.extension.toast
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.app.SAPWizardApplication
import com.company.mysapcpsdkprojectoffline.mdui.datamodel.DocumentModel
import com.company.mysapcpsdkprojectoffline.util.Util
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.activity_edit_documents.*

class EditDocumentsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var document: DocumentModel

    override fun getLayoutResourceId(): Int = R.layout.activity_edit_documents

    override fun main() {

        if (intent.hasExtra("document")){
            document = intent.getSerializableExtra("document") as DocumentModel
            documentNameET.setText(document.documentName)
            titleTV.text = getString(R.string.edit_document)
            subtitleTV.text = getString(R.string.edit_document)
        }else{
            titleTV.text = getString(R.string.add_document_)
            subtitleTV.text = getString(R.string.add_document_)
        }

        createNewCustomerBTN.setOnClickListener(this)
        saveTV.setOnClickListener(this)
        cancelTV.setOnClickListener(this)

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
            R.id.createNewCustomerBTN -> {

                val msg = isValidDetail()
                if (msg.isEmpty()) {
                    val intent = Intent()
                    intent.putExtra("document_name", documentNameET.text.toString().trim())
                    setResult(RESULT_OK, intent)
                    onBackPressed()
                } else {
                    toast(msg)
                }

            }
            R.id.saveTV -> {
                createNewCustomerBTN.performClick()
            }
            R.id.cancelTV -> {
                onBackPressed()
            }
        }
    }

    private fun isValidDetail(): String {

        var msg = ""

        when {
            documentNameET.text.toString().trim().isEmpty() -> {
                msg = "Please enter document name"
            }
        }
        return msg
    }
}