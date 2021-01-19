package com.company.mysapcpsdkprojectoffline.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.app.MainBusinessActivity
import com.company.mysapcpsdkprojectoffline.mdui.EntitySetListActivity


object WorkerUtils {
    internal const val NOTIFICATION_ID = 1
    internal const val OFFLINE_SYNC_CHANNEL_ID = "Offline OData Channel"
    internal const val IS_OFFLINE_STORE_INITIALIZED = "Offline Store Initialization Check"
    internal const val OFFLINE_SYNC_ON_DEMAND = "OfflineDataStore"

    enum class Operation {
        OPEN, SYNC
    }

    private val lock = Any()

    /**
     * Test and set the execution in progress flag
     * This makes sure that we have only one outstanding offline sync operation at a given time
     * @return true if there is active operation and false otherwise
     */
    @Volatile
    internal var isExecuting = false
        set(value) {
            synchronized(lock){
                field = value
            }
        }
        get() {
            synchronized(lock) {
                return field
            }
        }

    /***
     * Creates a notification builder which can be used to update the ongoing notification.
     * @param applicationContext
     * @param operation
     * @return notificationBuilder
     */
    internal fun createNotificationBuilder(applicationContext: Context, operation: Operation): NotificationCompat.Builder {

        val builder = NotificationCompat.Builder(applicationContext, OFFLINE_SYNC_CHANNEL_ID)
        val bigTextStyle = NotificationCompat.BigTextStyle()

        bigTextStyle.setBigContentTitle("Syncing Data.")
        builder.setStyle(bigTextStyle)
        builder.setWhen(System.currentTimeMillis())
        builder.setSmallIcon(R.drawable.ic_sync)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(OFFLINE_SYNC_CHANNEL_ID)
        }
        builder.setProgress(100, 0, false)

        // Clicking the notification will return to the app
        val intent = when (operation) {
            Operation.OPEN -> {
                Intent(applicationContext, MainBusinessActivity::class.java)
            }
            Operation.SYNC -> {
                Intent(applicationContext, EntitySetListActivity::class.java)
            }
        }
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        builder.setContentIntent(pendingIntent)

        return builder
    }
}
