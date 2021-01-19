package com.company.mysapcpsdkprojectoffline.util.extension

import android.app.Activity
import android.app.AlertDialog


fun showAlertDialog(context: Activity, dialogBuilder: AlertDialog.Builder.() -> Unit) {
    val builder = AlertDialog.Builder(context)
    builder.dialogBuilder()
    builder.setCancelable(false)
    val dialog = builder.create()

    dialog.show()
}

fun AlertDialog.Builder.positiveButton(text: String, handleClick: (which: Int) -> Unit = {}) {
    this.setPositiveButton(text) { _, which -> handleClick(which) }
}

fun AlertDialog.Builder.negativeButton(text: String, handleClick: (which: Int) -> Unit = {}) {
    this.setNegativeButton(text) { _, which -> handleClick(which) }
}


