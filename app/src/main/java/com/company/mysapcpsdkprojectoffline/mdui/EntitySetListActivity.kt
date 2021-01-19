package com.company.mysapcpsdkprojectoffline.mdui

import com.company.mysapcpsdkprojectoffline.app.SAPWizardApplication

import androidx.work.WorkManager
import com.sap.cloud.mobile.flowv2.core.DialogHelper
import com.company.mysapcpsdkprojectoffline.service.SAPServiceManager
import com.sap.cloud.mobile.fiori.indicator.FioriProgressBar
import com.sap.cloud.mobile.odata.core.Action0
import com.sap.cloud.mobile.odata.core.Action1
import com.sap.cloud.mobile.odata.offline.OfflineODataException
import com.company.mysapcpsdkprojectoffline.service.OfflineODataSyncDelegate
import com.company.mysapcpsdkprojectoffline.service.WorkerUtils
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem

import java.util.ArrayList
import java.util.HashMap
import com.company.mysapcpsdkprojectoffline.mdui.asalesorder.ASalesOrderActivity
import com.company.mysapcpsdkprojectoffline.mdui.asalesorderheaderpartner.ASalesOrderHeaderPartnerActivity
import com.company.mysapcpsdkprojectoffline.mdui.asalesorderheaderprelement.ASalesOrderHeaderPrElementActivity
import com.company.mysapcpsdkprojectoffline.mdui.asalesorderitem.ASalesOrderItemActivity
import com.company.mysapcpsdkprojectoffline.mdui.asalesorderitempartner.ASalesOrderItemPartnerActivity
import com.company.mysapcpsdkprojectoffline.mdui.asalesorderitemprelement.ASalesOrderItemPrElementActivity
import com.company.mysapcpsdkprojectoffline.mdui.asalesorderitemtext.ASalesOrderItemTextActivity
import com.company.mysapcpsdkprojectoffline.mdui.asalesorderscheduleline.ASalesOrderScheduleLineActivity
import com.company.mysapcpsdkprojectoffline.mdui.asalesordertext.ASalesOrderTextActivity
import com.company.mysapcpsdkprojectoffline.mdui.aslsordpaymentplanitemdetails.ASlsOrdPaymentPlanItemDetailsActivity
import org.slf4j.LoggerFactory
import com.company.mysapcpsdkprojectoffline.R

import kotlinx.android.synthetic.main.activity_entity_set_list.*
import kotlinx.android.synthetic.main.element_entity_set_list.view.*

/*
 * An activity to display the list of all entity types from the OData service
 */
class EntitySetListActivity : AppCompatActivity() {
    private val entitySetNames = ArrayList<String>()
    private val entitySetNameMap = HashMap<String, EntitySetName>()


    /* Fiori progress bar for busy indication if either update or delete action is clicked upon */
    private var progressBar: FioriProgressBar? = null

    private var sapServiceManager: SAPServiceManager? = null

    enum class EntitySetName constructor(val entitySetName: String, val titleId: Int, val iconId: Int) {
        ASalesOrder("ASalesOrder", R.string.eset_asalesorder,
            BLUE_ANDROID_ICON),
        ASalesOrderHeaderPartner("ASalesOrderHeaderPartner", R.string.eset_asalesorderheaderpartner,
            WHITE_ANDROID_ICON),
        ASalesOrderHeaderPrElement("ASalesOrderHeaderPrElement", R.string.eset_asalesorderheaderprelement,
            BLUE_ANDROID_ICON),
        ASalesOrderItem("ASalesOrderItem", R.string.eset_asalesorderitem,
            WHITE_ANDROID_ICON),
        ASalesOrderItemPartner("ASalesOrderItemPartner", R.string.eset_asalesorderitempartner,
            BLUE_ANDROID_ICON),
        ASalesOrderItemPrElement("ASalesOrderItemPrElement", R.string.eset_asalesorderitemprelement,
            WHITE_ANDROID_ICON),
        ASalesOrderItemText("ASalesOrderItemText", R.string.eset_asalesorderitemtext,
            BLUE_ANDROID_ICON),
        ASalesOrderScheduleLine("ASalesOrderScheduleLine", R.string.eset_asalesorderscheduleline,
            WHITE_ANDROID_ICON),
        ASalesOrderText("ASalesOrderText", R.string.eset_asalesordertext,
            BLUE_ANDROID_ICON),
        ASlsOrdPaymentPlanItemDetails("ASlsOrdPaymentPlanItemDetails", R.string.eset_aslsordpaymentplanitemdetails,
            WHITE_ANDROID_ICON)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sapServiceManager = (application as SAPWizardApplication).sapServiceManager
        setContentView(R.layout.activity_entity_set_list)
        val toolbar = findViewById<Toolbar>(R.id.toolbar) // to avoid ambiguity
        setSupportActionBar(toolbar)

        entitySetNames.clear()
        entitySetNameMap.clear()
        for (entitySet in EntitySetName.values()) {
            val entitySetTitle = resources.getString(entitySet.titleId)
            entitySetNames.add(entitySetTitle)
            entitySetNameMap[entitySetTitle] = entitySet
        }

        val listView = entity_list
        val adapter = EntitySetListAdapter(this, R.layout.element_entity_set_list, entitySetNames)

        listView.adapter = adapter

        listView.setOnItemClickListener listView@{ _, _, position, _ ->
            val entitySetName = entitySetNameMap[adapter.getItem(position)!!]
            val context = this@EntitySetListActivity
            val intent: Intent = when (entitySetName) {
                EntitySetName.ASalesOrder -> Intent(context, ASalesOrderActivity::class.java)
                EntitySetName.ASalesOrderHeaderPartner -> Intent(context, ASalesOrderHeaderPartnerActivity::class.java)
                EntitySetName.ASalesOrderHeaderPrElement -> Intent(context, ASalesOrderHeaderPrElementActivity::class.java)
                EntitySetName.ASalesOrderItem -> Intent(context, ASalesOrderItemActivity::class.java)
                EntitySetName.ASalesOrderItemPartner -> Intent(context, ASalesOrderItemPartnerActivity::class.java)
                EntitySetName.ASalesOrderItemPrElement -> Intent(context, ASalesOrderItemPrElementActivity::class.java)
                EntitySetName.ASalesOrderItemText -> Intent(context, ASalesOrderItemTextActivity::class.java)
                EntitySetName.ASalesOrderScheduleLine -> Intent(context, ASalesOrderScheduleLineActivity::class.java)
                EntitySetName.ASalesOrderText -> Intent(context, ASalesOrderTextActivity::class.java)
                EntitySetName.ASlsOrdPaymentPlanItemDetails -> Intent(context, ASlsOrdPaymentPlanItemDetailsActivity::class.java)
                else -> return@listView
            }
            context.startActivity(intent)
        }
    }

    inner class EntitySetListAdapter internal constructor(context: Context, resource: Int, entitySetNames: List<String>)
                    : ArrayAdapter<String>(context, resource, entitySetNames) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            val entitySetName = entitySetNameMap[getItem(position)!!]
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.element_entity_set_list, parent, false)
            }
            val entitySetCell = view!!.entity_set_name
            entitySetCell.headline = entitySetName?.titleId?.let {
                context.resources.getString(it)
            }
            entitySetName?.iconId?.let { entitySetCell.setDetailImage(it) }
            return view
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, SETTINGS_SCREEN_ITEM, 0, R.string.menu_item_settings)
        menu.add(0, SYNC_ACTION_ITEM, 1, R.string.synchronize_action)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        LOGGER.debug("onOptionsItemSelected: " + item.title)
        return when (item.itemId) {
            SETTINGS_SCREEN_ITEM -> {
                LOGGER.debug("settings screen menu item selected.")
                val intent = Intent(this, SettingsActivity::class.java)
                this.startActivityForResult(intent, SETTINGS_SCREEN_ITEM)
                true
            }
            SYNC_ACTION_ITEM -> {
                synchronize()
                true
            }
            else -> false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LOGGER.debug("EntitySetListActivity::onActivityResult, request code: $requestCode result code: $resultCode")
        if (requestCode == SETTINGS_SCREEN_ITEM) {
            LOGGER.debug("Calling AppState to retrieve settings after settings screen is closed.")
        }
    }

    override fun onStop() {
        if (WorkerUtils.isExecuting) {
            sapServiceManager?.offlineODataSyncDelegate?.notificationManager?.cancel(WorkerUtils.NOTIFICATION_ID)
            WorkManager.getInstance(application).cancelUniqueWork(WorkerUtils.OFFLINE_SYNC_ON_DEMAND)
        }
        super.onStop()
    }

    private fun synchronize() {
        if (progressBar == null) {
            progressBar = window.decorView.findViewById(R.id.sync_indeterminate)
        }

        progressBar!!.visibility = View.VISIBLE

        OfflineODataSyncDelegate.setProgressBar(progressBar!!)

        try {
            sapServiceManager?.synchronize(
                    Action0 {
                        this@EntitySetListActivity.runOnUiThread {
                            progressBar!!.visibility = View.INVISIBLE
                        }
                    },
                    Action1 {
                        this@EntitySetListActivity.runOnUiThread {
                            progressBar!!.visibility = View.INVISIBLE
                            DialogHelper(this@EntitySetListActivity).showOKOnlyDialog(
                                fragmentManager = supportFragmentManager,
                                message = getString(R.string.synchronize_failure_detail)
                            )
                        }
                    })
        } catch (e: OfflineODataException) {
             DialogHelper(this@EntitySetListActivity).showOKOnlyDialog(
                 fragmentManager = supportFragmentManager,
                 message = "An offline sync operation is already in progress"
             )
        }
    }

    companion object {
        private const val SETTINGS_SCREEN_ITEM = 200
        private const val SYNC_ACTION_ITEM = 300
        private val LOGGER = LoggerFactory.getLogger(EntitySetListActivity::class.java)
        private const val BLUE_ANDROID_ICON = R.drawable.ic_android_blue
        private const val WHITE_ANDROID_ICON = R.drawable.ic_android_white
    }
}
