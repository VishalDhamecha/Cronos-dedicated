package com.company.mysapcpsdkprojectoffline.util

import android.app.*
import android.content.Context
import android.view.LayoutInflater
import com.company.mysapcpsdkprojectoffline.R


object Util {

    private var dialog: AlertDialog? = null


    fun showProgressDialog(context: Context) {
        hideProgressDialog()
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false) // if you want user to wait for some process to finish,
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.layout_loading_dialog, null)
        builder.setView(v)
        dialog = builder.create()
        dialog!!.show()
    }

    fun hideProgressDialog() {
        if (isProgressDialogShown())
            dialog?.dismiss()
    }

    fun isProgressDialogShown(): Boolean {
        return if (dialog != null)
            dialog!!.isShowing
        else
            false
    }

    fun scheduleNotification(context: Context) {
        NotificationScheduler.setReminder(context, AlarmReceiver::class.java,15000)
    }


}