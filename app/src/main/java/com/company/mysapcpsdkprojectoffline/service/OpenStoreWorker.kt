package com.company.mysapcpsdkprojectoffline.service

import android.app.NotificationManager
import android.content.Context
import androidx.work.*
import com.sap.cloud.mobile.odata.core.Action0
import com.sap.cloud.mobile.odata.core.Action1
import com.sap.cloud.mobile.odata.offline.OfflineODataException
import com.company.mysapcpsdkprojectoffline.app.SAPWizardApplication
import org.slf4j.LoggerFactory
import java.util.concurrent.CyclicBarrier

class OpenStoreWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    private val sapServiceManager = (applicationContext as SAPWizardApplication).sapServiceManager
    private val offlineSyncDelegate = sapServiceManager?.offlineODataSyncDelegate
    private val notificationManager = ctx.getSystemService(NotificationManager::class.java)
    private val notificationBuilder = WorkerUtils.createNotificationBuilder(applicationContext as SAPWizardApplication, WorkerUtils.Operation.OPEN)

    private var isSuccessful = false

    override suspend fun doWork(): Result {
        val isOfflineStoreInitialized = inputData.getBoolean(WorkerUtils.IS_OFFLINE_STORE_INITIALIZED, false)

        if (!isOfflineStoreInitialized) {
            val foregroundInfo = ForegroundInfo(WorkerUtils.NOTIFICATION_ID, notificationBuilder.build())
            setForeground(foregroundInfo)
            offlineSyncDelegate?.notificationBuilder = notificationBuilder
            offlineSyncDelegate?.notificationManager = notificationManager
            offlineSyncDelegate?.operation = WorkerUtils.Operation.OPEN
        }
        LOGGER.debug("Opening offline store")
        val cyclicBarrier = CyclicBarrier(2)

        sapServiceManager?.retrieveProvider()!!.open(
                {
                    isSuccessful = true
                    WorkerUtils.isExecuting = false
                    callerSuccessHandler?.call()
                    cyclicBarrier.await()
                },
                {exception ->
                    isSuccessful = false
                    WorkerUtils.isExecuting = false
                    callerFailureHandler?.call(exception)
                    cyclicBarrier.await()
                }
        )

        cyclicBarrier.await()
        return if (isSuccessful) Result.success() else Result.failure()
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(OpenStoreWorker::class.java)
        internal var callerSuccessHandler: Action0? = null
        internal var callerFailureHandler: Action1<OfflineODataException>? = null
    }
}
