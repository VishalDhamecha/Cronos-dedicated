package com.company.mysapcpsdkprojectoffline.mdui

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.company.mysapcpsdkprojectoffline.mdui.adapter.ContactPerssonListAdapter
import com.company.mysapcpsdkprojectoffline.util.extension.gone
import com.company.mysapcpsdkprojectoffline.util.extension.isVisible
import com.company.mysapcpsdkprojectoffline.util.extension.visible
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.app.SAPWizardApplication
import com.company.mysapcpsdkprojectoffline.util.CustomTypefaceSpan
import com.company.mysapcpsdkprojectoffline.util.Util
import com.irozon.sneaker.Sneaker
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderType
import kotlinx.android.synthetic.main.activity_supplier_detail.*
import kotlinx.android.synthetic.main.activity_supplier_detail.backIV
import kotlinx.android.synthetic.main.item_document.view.*

class SupplierDetailActivity : BaseActivity(), View.OnClickListener {

    private var contactPersons: MutableList<String> = arrayListOf()
    private lateinit var aSalesOrderTypeEntity: ASalesOrderType

    private var riskTitle = "Average: "
    private var riskDescription = "Upper medium grade"

    override fun getLayoutResourceId(): Int = R.layout.activity_supplier_detail

    override fun main() {

        aSalesOrderTypeEntity = intent.getParcelableExtra("sales_order") as ASalesOrderType

        contactPersons.add("")
        contactPersons.add("")
        contactPersons.add("")
        contactPersons.add("")
        contactPersons.add("")
        contactPersonREC.adapter = ContactPerssonListAdapter(this, contactPersons)

        val foregroundSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.black))
        val risk = riskTitle + riskDescription
        val spannable = SpannableString(
                risk
        )
        spannable.setSpan(foregroundSpan, riskTitle.length, risk.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        averageRiskTV.text = spannable
        customerNameTV.text = aSalesOrderTypeEntity.createdByUser

        backIV.setOnClickListener(this)
        riskAssessmentRL.setOnClickListener(this)
        contactInfoRL.setOnClickListener(this)
        contactPersonRL.setOnClickListener(this)


        isNetworkConnected().observe(this, {

            if (!it && !SAPWizardApplication.isNetworkLost) {
                val sneaker = Sneaker.with(this).autoHide(true).setDuration(5000)
                val view = LayoutInflater.from(this).inflate(R.layout.topsheet_no_internet, sneaker.getView(), false)
                sneaker.sneakCustom(view)
            } else if (it && SAPWizardApplication.isNetworkLost) {
                val sneaker = Sneaker.with(this).autoHide(true).setDuration(5000)
                val view = LayoutInflater.from(this).inflate(R.layout.topsheet_internet_connected, sneaker.getView(), false)
                sneaker.sneakCustom(view)
            }

        })


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.backIV -> {
                onBackPressed()
            }
            R.id.contactInfoRL -> {
                if (contactInfoCard.isVisible()) {
                    contactInfoCard.gone()
                    Glide.with(this).load(R.drawable.ic_down_arrow).into(contactInfoIV)
                } else {
                    contactInfoCard.visible()
                    Glide.with(this).load(R.drawable.ic_up_arrow).into(contactInfoIV)
                }
            }
            R.id.contactPersonRL -> {
                if (contactPersonCard.isVisible()) {
                    contactPersonCard.gone()
                    Glide.with(this).load(R.drawable.ic_down_arrow).into(contactPersonIV)
                } else {
                    contactPersonCard.visible()
                    Glide.with(this).load(R.drawable.ic_up_arrow).into(contactPersonIV)
                }
            }
            R.id.riskAssessmentRL -> {
                if (riskAssessmentCard.isVisible()) {
                    riskAssessmentCard.gone()
                    Glide.with(this).load(R.drawable.ic_down_arrow).into(riskAssessmentIV)
                } else {
                    riskAssessmentCard.visible()
                    Glide.with(this).load(R.drawable.ic_up_arrow).into(riskAssessmentIV)
                }
            }
        }
    }
}