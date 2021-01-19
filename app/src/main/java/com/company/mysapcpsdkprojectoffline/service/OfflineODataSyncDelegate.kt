package com.company.mysapcpsdkprojectoffline.service

import android.app.NotificationManager
import android.widget.ProgressBar
import androidx.core.app.NotificationCompat

import com.sap.cloud.mobile.odata.offline.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class OfflineODataSyncDelegate: OfflineODataProviderDelegate {

    internal lateinit var notificationBuilder: NotificationCompat.Builder
    internal lateinit var notificationManager: NotificationManager
    internal lateinit var operation: WorkerUtils.Operation
    private var startPointForSync = 0

    override fun updateOpenProgress(p0: OfflineODataProvider, p1: OfflineODataProviderOperationProgress) {
        GlobalScope.launch(Dispatchers.Main){
            updateProgressStep(p1)
        }
    }

    override fun updateDownloadProgress(p0: OfflineODataProvider, p1: OfflineODataProviderDownloadProgress) {
        GlobalScope.launch(Dispatchers.Main){
            updateProgressStep(p1)
        }
    }

    override fun updateFailedRequest(p0: OfflineODataProvider, p1: OfflineODataFailedRequest) {
    }

    override fun updateSendStoreProgress(p0: OfflineODataProvider, p1: OfflineODataProviderOperationProgress) {
        GlobalScope.launch(Dispatchers.Main){
            updateProgressStep(p1)
        }
    }

    override fun updateUploadProgress(p0: OfflineODataProvider, p1: OfflineODataProviderOperationProgress) {
        GlobalScope.launch(Dispatchers.Main){
            updateProgressStep(p1)
        }
    }

    override fun getCloudProgressPullInterval(): Int {
        return 500
    }

    private fun updateProgressStep(progress: OfflineODataProviderOperationProgress) {
        when (operation) {
            WorkerUtils.Operation.SYNC -> {
                totalNumberOfSteps = 40
                if (progress.currentStepNumber > previousStep) {
                    currentStepNumber = totalNumberOfSteps / 2 * progress.currentStepNumber / progress.totalNumberOfSteps + startPointForSync
                    progressBar.max = totalNumberOfSteps
                    progressBar.progress = currentStepNumber
                    previousStep = progress.currentStepNumber
                    val notification = notificationBuilder.setProgress(totalNumberOfSteps, currentStepNumber, false)
                            .build()
                    notificationManager.notify(WorkerUtils.NOTIFICATION_ID, notification)
                    if (totalNumberOfSteps == currentStepNumber) {
                        notificationManager.cancel(WorkerUtils.NOTIFICATION_ID)
                    }
                }
                if (progress.currentStepNumber == progress.totalNumberOfSteps) {
                    previousStep = 0
                    currentStepNumber = 0
                    startPointForSync = if (startPointForSync == 0) {
                        totalNumberOfSteps / 2
                    } else {
                        0
                    }
                }
            }
            WorkerUtils.Operation.OPEN -> {
                if (progress.currentStepNumber > previousStep) {
                    totalNumberOfSteps = progress.totalNumberOfSteps
                    currentStepNumber = progress.currentStepNumber
                    progressBar.max = totalNumberOfSteps
                    progressBar.progress = currentStepNumber
                    previousStep = progress.currentStepNumber
                    val notification = notificationBuilder.setProgress(totalNumberOfSteps, currentStepNumber, false)
                            .build()
                    notificationManager.notify(WorkerUtils.NOTIFICATION_ID, notification)
                }
                if (progress.currentStepNumber == progress.totalNumberOfSteps) {
                    previousStep = 0
                    currentStepNumber = 0
                }
            }
        }
    }

    companion object {
        private lateinit var progressBar: ProgressBar
        private var currentStepNumber = 0
        private var totalNumberOfSteps = 100
        private var previousStep = currentStepNumber

        @JvmStatic
        fun setProgressBar(pBar: ProgressBar) {
            progressBar = pBar
            progressBar.max = totalNumberOfSteps
            progressBar.progress = currentStepNumber
        }
    }
}
