package com.company.mysapcpsdkprojectoffline.service

import androidx.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import androidx.work.*
import com.company.mysapcpsdkprojectoffline.app.SAPWizardApplication
import com.sap.cloud.android.odata.api_sales_order_srv_entities.API_SALES_ORDER_SRV_Entities
import com.sap.cloud.mobile.foundation.model.AppConfig
import com.sap.cloud.mobile.foundation.common.ClientProvider
import com.sap.cloud.mobile.foundation.common.EncryptionUtil
import com.sap.cloud.mobile.odata.core.Action0
import com.sap.cloud.mobile.odata.core.Action1
import com.sap.cloud.mobile.odata.core.AndroidSystem
import com.sap.cloud.mobile.odata.offline.OfflineODataDefiningQuery
import com.sap.cloud.mobile.odata.offline.OfflineODataException
import com.sap.cloud.mobile.odata.offline.OfflineODataParameters
import com.sap.cloud.mobile.odata.offline.OfflineODataProvider
import java.net.URL
import java.util.Arrays
import org.slf4j.LoggerFactory

/**
 * This class represents the Mobile Application backed by an OData service for offline use.
 *
 * @param [application] SAP wizard application
 * @param [appConfig] Application Configuration data
 */
class SAPServiceManager (private var appConfig: AppConfig, private var application: SAPWizardApplication) {

    /*
     * Offline line OData Provider
     */
    private var provider: OfflineODataProvider? = null

    internal val offlineODataSyncDelegate = OfflineODataSyncDelegate()

    /** OData service for interacting with local OData Provider */
    var aPI_SALES_ORDER_SRV_Entities: API_SALES_ORDER_SRV_Entities? = null
        private set
        get() {
            return field ?: throw IllegalStateException("SAPServiceManager was not initialized")
        }

    /**
     * This call can only be made when the user is authenticated (if required) as it depends
     * on application store for encryption keys and ClientProvider
     * @return OfflineODataProvider
     */
    fun retrieveProvider(): OfflineODataProvider? {
        if (provider == null) {
            initializeOffline()
        }
        return provider
    }

    /*
     * Create OfflineODataProvider
     * This is a blocking call, no data will be transferred until open, download, upload
     */
    private fun initializeOffline() {
        AndroidSystem.setContext(application)
        val serviceUrl = appConfig.serviceUrl
        try {
            val url = URL(serviceUrl + CONNECTION_ID_API_SALES_ORDER_SRV_ENTITIES)

            val offlineODataParameters = OfflineODataParameters()
            offlineODataParameters.isEnableRepeatableRequests = true
            offlineODataParameters.storeName = OFFLINE_DATASTORE
            val encryptionKeyBytes = EncryptionUtil.getEncryptionKey(OFFLINE_DATASTORE_ENCRYPTION_KEY_ALIAS)
            val key = Base64.encodeToString(encryptionKeyBytes, Base64.NO_WRAP)
            offlineODataParameters.storeEncryptionKey = key
            Arrays.fill(encryptionKeyBytes, 0.toByte())

            // Set the default application version
            val customheaders = offlineODataParameters.customHeaders
            customheaders[APP_VERSION_HEADER] = appConfig.applicationVersion
            // In case of offlineODataParameters.customHeaders returning a new object if customHeaders from offlineODataParameters is null, set again as below
            offlineODataParameters.setCustomHeaders(customheaders)
            provider = OfflineODataProvider(url, offlineODataParameters, ClientProvider.get(), offlineODataSyncDelegate)
            val aSalesOrderQuery = OfflineODataDefiningQuery("A_SalesOrder", "A_SalesOrder", false)
            provider!!.addDefiningQuery(aSalesOrderQuery)
            val aSalesOrderHeaderPartnerQuery = OfflineODataDefiningQuery("A_SalesOrderHeaderPartner", "A_SalesOrderHeaderPartner", false)
            provider!!.addDefiningQuery(aSalesOrderHeaderPartnerQuery)
            val aSalesOrderHeaderPrElementQuery = OfflineODataDefiningQuery("A_SalesOrderHeaderPrElement", "A_SalesOrderHeaderPrElement", false)
            provider!!.addDefiningQuery(aSalesOrderHeaderPrElementQuery)
            val aSalesOrderItemQuery = OfflineODataDefiningQuery("A_SalesOrderItem", "A_SalesOrderItem", false)
            provider!!.addDefiningQuery(aSalesOrderItemQuery)
            val aSalesOrderItemPartnerQuery = OfflineODataDefiningQuery("A_SalesOrderItemPartner", "A_SalesOrderItemPartner", false)
            provider!!.addDefiningQuery(aSalesOrderItemPartnerQuery)
            val aSalesOrderItemPrElementQuery = OfflineODataDefiningQuery("A_SalesOrderItemPrElement", "A_SalesOrderItemPrElement", false)
            provider!!.addDefiningQuery(aSalesOrderItemPrElementQuery)
            val aSalesOrderItemTextQuery = OfflineODataDefiningQuery("A_SalesOrderItemText", "A_SalesOrderItemText", false)
            provider!!.addDefiningQuery(aSalesOrderItemTextQuery)
//            val aSalesOrderScheduleLineQuery = OfflineODataDefiningQuery("A_SalesOrderScheduleLine", "A_SalesOrderScheduleLine", false)
//            provider!!.addDefiningQuery(aSalesOrderScheduleLineQuery)
            val aSalesOrderTextQuery = OfflineODataDefiningQuery("A_SalesOrderText", "A_SalesOrderText", false)
            provider!!.addDefiningQuery(aSalesOrderTextQuery)
            val aSlsOrdPaymentPlanItemDetailsQuery = OfflineODataDefiningQuery("A_SlsOrdPaymentPlanItemDetails", "A_SlsOrdPaymentPlanItemDetails", false)
            provider!!.addDefiningQuery(aSlsOrdPaymentPlanItemDetailsQuery)


            aPI_SALES_ORDER_SRV_Entities = API_SALES_ORDER_SRV_Entities(provider!!)

        } catch (e: Exception) {
            LOGGER.error("Exception encountered setting up offline store: " + e.message)
            Log.e("SAPServiceManager", "In initializeOffline: " + e.message)
        }
    }

    /**
     * Synchronize local offline data store with Server
     * Upload - local changes
     * Download - server changes
     * @param syncSuccessHandler
     * @param syncFailureHandler
     */
    fun synchronize(syncSuccessHandler: Action0, syncFailureHandler: Action1<OfflineODataException>) {

        val successHandler = Action0 {
                        application.repositoryFactory.reset()
                        syncSuccessHandler.call()
                    }

        val failureHandler = Action1<OfflineODataException> { error ->
                        application.repositoryFactory.reset()
                        syncFailureHandler.call(error)
                    }

        if (!WorkerUtils.isExecuting) {
            WorkerUtils.isExecuting = true

            SyncStoreWorker.callerSuccessHandler = successHandler
            SyncStoreWorker.callerFailureHandler = failureHandler

            val constraints = Constraints.Builder()
                    .setRequiresStorageNotLow(true)
                    .build()

            val syncRequest = OneTimeWorkRequestBuilder<SyncStoreWorker>()
                    .setConstraints(constraints)
                    .build()

            WorkManager.getInstance(application).enqueueUniqueWork(
                    WorkerUtils.OFFLINE_SYNC_ON_DEMAND,
                    ExistingWorkPolicy.KEEP,
                    syncRequest
            )

            return
        }
        throw OfflineODataException(0, "An offline sync operation is already in progress")
    }

    /*
     * Close and remove offline data store
     */
    fun reset() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
        sharedPreferences.edit().clear().apply()

        try {
            AndroidSystem.setContext(application)
            provider?.apply { this.close() }
            OfflineODataProvider.clear(OFFLINE_DATASTORE)
        } catch (e: OfflineODataException) {
            Log.e("SAPServiceManager", e.message ?: "")
            LOGGER.error("Unable to reset Offline Data Store. Encountered exception: " + e.message)
        } finally {
            provider = null
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SAPServiceManager::class.java)

        /* Name of the offline data file on the application file space */
        private const val OFFLINE_DATASTORE = "OfflineDataStore"
        private const val OFFLINE_DATASTORE_ENCRYPTION_KEY_ALIAS = "Offline_DataStore_EncryptionKey_Alias"

        /* Header name for application version */
        private const val APP_VERSION_HEADER = "X-APP-VERSION"

        /*
         * Connection ID of Mobile Application
         */
        const val CONNECTION_ID_API_SALES_ORDER_SRV_ENTITIES = "dimosv.salesandmarketing"
    }
}
