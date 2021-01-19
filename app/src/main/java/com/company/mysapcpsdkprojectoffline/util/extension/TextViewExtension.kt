package com.company.mysapcpsdkprojectoffline.util.extension

import android.content.Context
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat

fun TextView.setText(text: String) {
    this.text = text
}

fun AppCompatTextView.clear() {
    this.text = ""
}

fun Context.setTextColor(color:Int) : Int{
    return ContextCompat.getColor(this,color)
}