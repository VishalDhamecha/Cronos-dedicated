package com.company.mysapcpsdkprojectoffline.util.extension

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment


fun Activity.copyToClipBoard(label: String, value: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, value)
    clipboard.setPrimaryClip(clip)
}

fun Fragment.copyToClipBoard(label: String, value: String) {
    activity?.copyToClipBoard(label, value)
}


fun <T> Context.openActivity(it: Class<T>, vararg flags: Int, extras: Bundle.() -> Unit = {}) {
    val intent = Intent(this, it)
    if (flags.isNotEmpty()) {
        for (i in flags.indices){
            intent.addFlags(flags[i])
        }
    }
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
}

fun <T> Activity.openActivityAndFinish(it: Class<T>, vararg flags: Int, extras: Bundle.() -> Unit = {}) {
    val intent = Intent(this, it)
    if (flags.isNotEmpty()) {
        for (i in flags.indices){
            intent.addFlags(flags[i])
        }
    }
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
    finish()
}

fun <T> Activity.openActivityForResult(
    it: Class<T>,
    requestCode: Int,
    vararg flags: Int,
    extras: Bundle.() -> Unit = {}
) {
    val intent = Intent(this, it)
    if (flags.isNotEmpty()) {
        for (i in flags.indices){
            intent.addFlags(flags[i])
        }
    }
    intent.putExtras(Bundle().apply(extras))
    startActivityForResult(intent, requestCode)
}

fun <T> Fragment.openActivityForResult(
    it: Class<T>,
    requestCode: Int,
    vararg flags: Int,
    extras: Bundle.() -> Unit = {}
) {
    val intent = Intent(this.requireContext(), it)
    if (flags.isNotEmpty()) {
        for (i in flags.indices){
            intent.addFlags(flags[i])
        }
    }
    intent.putExtras(Bundle().apply(extras))
    startActivityForResult(intent, requestCode)
}


fun Activity.copytoClipBoard(label: String, value: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, value)
    clipboard.setPrimaryClip(clip)
}

fun Fragment.copytoClipBoard(label: String, value: String) {
    activity?.copytoClipBoard(label, value)
}

