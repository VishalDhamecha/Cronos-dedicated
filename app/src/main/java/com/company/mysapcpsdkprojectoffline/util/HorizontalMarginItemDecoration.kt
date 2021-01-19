package com.company.mysapcpsdkprojectoffline.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

class HorizontalMarginItemDecoration(context: Context, @DimenRes horizontalMarginInDpLeft: Int,@DimenRes horizontalMarginInDpRight: Int) :
    RecyclerView.ItemDecoration() {


    private val horizontalMarginLeftInPx: Int =
        context.resources.getDimension(horizontalMarginInDpLeft).toInt()
    private val horizontalMarginRightInPx: Int =
            context.resources.getDimension(horizontalMarginInDpRight).toInt()

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.right = horizontalMarginRightInPx
        outRect.left = horizontalMarginLeftInPx
    }

}