package com.company.mysapcpsdkprojectoffline.util.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PorterDuff
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
/**
 * Set image from uri
 */
fun AppCompatImageView.setImage(context: Context, url: String = "", placeHolder: Int = 0) {
    Glide.with(context).load(url).placeholder(placeHolder).error(placeHolder).into(this)
}

fun AppCompatImageView.setImage(context: Context, resource: Int) {
    Glide.with(context).load(resource).into(this)
}
