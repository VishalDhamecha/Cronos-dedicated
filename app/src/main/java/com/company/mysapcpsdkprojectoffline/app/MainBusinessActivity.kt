package com.company.mysapcpsdkprojectoffline.app

import android.content.Intent
import android.os.Bundle

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.company.mysapcpsdkprojectoffline.R

import androidx.preference.PreferenceManager
import androidx.work.*
import com.sap.cloud.mobile.flowv2.core.DialogHelper
import com.sap.cloud.mobile.odata.core.Action0
import com.sap.cloud.mobile.odata.core.Action1
import com.sap.cloud.mobile.odata.offline.OfflineODataException
import com.company.mysapcpsdkprojectoffline.service.*
import kotlinx.android.synthetic.main.activity_main_business.*
import org.slf4j.LoggerFactory

import com.company.mysapcpsdkprojectoffline.mdui.EntitySetListActivity
import com.company.mysapcpsdkprojectoffline.mdui.MainActivity


class MainBusinessActivity : AppCompatActivity() {

    private val workManager = WorkManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_business)
        OfflineODataSyncDelegate.setProgressBar(findViewById(R.id.main_bus_progress_bar))
        createNotificationChannel()
    }

    private fun startEntitySetListActivity() {
        if (!WorkerUtils.isExecuting) {
            openODataStore(Action0 {
                startActivity(Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                })
            })
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isOfflineStoreInitialized = sharedPreferences.getBoolean(offlineStoreInitializedKey, false)
        if (isOfflineStoreInitialized) {
            init_offline_label.visibility = View.INVISIBLE
            findViewById<ProgressBar>(R.id.main_bus_progress_bar).visibility = View.INVISIBLE
            findViewById<ProgressBar>(R.id.main_bus_resume_progress_bar).visibility = View.VISIBLE
        } else {
            init_offline_label.visibility = View.VISIBLE
            findViewById<ProgressBar>(R.id.main_bus_progress_bar).visibility = View.VISIBLE
            findViewById<ProgressBar>(R.id.main_bus_resume_progress_bar).visibility = View.INVISIBLE
        }
        startEntitySetListActivity()
    }

    /**
     * Open offline data store by calling workManager to start open request.
     * Display progress and text to advise users of lengthy initial open and wait for completion.
     * @param openDoneHandler - handler to invoke when offline data store is opened successfully
     */
    private fun openODataStore(openDoneHandler: Action0) {
        val successHandler = Action0 {
            val sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val editor = sharedPreferences.edit()
            editor.putBoolean(offlineStoreInitializedKey, true)
            editor.apply()
            openDoneHandler.call()
        }

        val failureHandler = Action1<OfflineODataException> {
            LOGGER.error(it.message)
            DialogHelper(application).showOKOnlyDialog(
                    supportFragmentManager,
                    message = getErrorMessage(it),
                    title = resources.getString(R.string.offline_initial_open_error),
                    positiveAction = {
                        this@MainBusinessActivity.finish()
                    }
            )
        }

        if(!WorkerUtils.isExecuting) {
            WorkerUtils.isExecuting = true
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val isOfflineStoreInitialized = sharedPreferences.getBoolean(offlineStoreInitializedKey, false)
            val inputData = workDataOf(WorkerUtils.IS_OFFLINE_STORE_INITIALIZED to isOfflineStoreInitialized)

            OpenStoreWorker.callerSuccessHandler = successHandler
            OpenStoreWorker.callerFailureHandler = failureHandler

            val constraints = Constraints.Builder()
                    .setRequiresStorageNotLow(true)
                    .build()

            val openRequest = OneTimeWorkRequestBuilder<OpenStoreWorker>()
                    .setConstraints(constraints)
                    .setInputData(inputData)
                    .build()

            workManager.enqueueUniqueWork(
                    "OpenOfflineStore",
                    ExistingWorkPolicy.KEEP,
                    openRequest
            )
            return
        }
        failureHandler.call(OfflineODataException(0, "An offline sync operation is already in progress"))
    }

    private fun getErrorMessage(exception: Exception?): String {
        var errorDetail = resources.getString(R.string.offline_initial_open_error_detail)
        if (exception != null && exception.message != null) {
            if (exception.message.toString().contains("OData server returned HTTP code, 413")) {
                errorDetail = String.format("$errorDetail %s", resources.getString(R.string.offline_backend_big_data_error_detail))
            } else if (exception.message.toString().contains("download operation with status code: 504")) {
                errorDetail = String.format("$errorDetail %s", resources.getString(R.string.offline_backend_time_out_error_detail))
            } else {
                errorDetail = String.format("$errorDetail\n%s", exception.localizedMessage)
            }
        }
        return errorDetail
    }

    /**
     * To send notification, Oreo and later versions (API 26+) require a notification channel
     */
    private fun createNotificationChannel() {
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Offline OData Sync"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(WorkerUtils.OFFLINE_SYNC_CHANNEL_ID, name, importance)

            val notificationManager = getSystemService(NotificationManager::class.java)
            channel.setSound(null, null)
            notificationManager.createNotificationChannel(channel)
        }
    }
    companion object {
        internal const val offlineStoreInitializedKey = "OfflineODataStoreInitialized"
        private val LOGGER = LoggerFactory.getLogger(MainBusinessActivity::class.java)
    }
}
