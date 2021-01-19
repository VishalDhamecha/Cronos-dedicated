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

class SyncStoreWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    private val sapServiceManager = (applicationContext as SAPWizardApplication).sapServiceManager
    private val offlineSyncDelegate = sapServiceManager?.offlineODataSyncDelegate
    private val notificationManager = ctx.getSystemService(NotificationManager::class.java)
    private val notificationBuilder = WorkerUtils.createNotificationBuilder(applicationContext as SAPWizardApplication, WorkerUtils.Operation.SYNC)
    private var isSuccessful = false

    override suspend fun doWork(): Result {
        offlineSyncDelegate?.notificationBuilder = notificationBuilder
        offlineSyncDelegate?.notificationManager = notificationManager
        offlineSyncDelegate?.operation = WorkerUtils.Operation.SYNC
        LOGGER.debug("Syncing offline store")
        val cyclicBarrier = CyclicBarrier(2)

        sapServiceManager?.retrieveProvider()!!.upload(
                {
                    sapServiceManager.retrieveProvider()!!.download(
                            {
                                isSuccessful = true
                                WorkerUtils.isExecuting = false
                                callerSuccessHandler?.call()
                                cyclicBarrier.await()
                            },
                            { exception ->
                                isSuccessful = false
                                WorkerUtils.isExecuting = false
                                callerFailureHandler?.call(exception)
                                LOGGER.error("Exception encountered downloading to local store: " + exception.message)
                                cyclicBarrier.await()
                            })
                },
                { exception ->
                    isSuccessful = false
                    WorkerUtils.isExecuting = false
                    callerFailureHandler?.call(exception)
                    LOGGER.error("Exception encountered uploading from local store: " + exception.message)
                    cyclicBarrier.await()
                }
        )

        cyclicBarrier.await()
        return if (isSuccessful) Result.success() else Result.failure()
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SyncStoreWorker::class.java)
        internal var callerSuccessHandler: Action0? = null
        internal var callerFailureHandler: Action1<OfflineODataException>? = null
    }
}
