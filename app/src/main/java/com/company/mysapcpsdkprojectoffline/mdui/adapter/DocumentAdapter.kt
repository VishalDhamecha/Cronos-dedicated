package com.company.mysapcpsdkprojectoffline.mdui.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.company.mysapcpsdkprojectoffline.mdui.datamodel.DocumentModel
import com.company.mysapcpsdkprojectoffline.util.CustomTypefaceSpan
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.util.extension.gone
import com.company.mysapcpsdkprojectoffline.util.extension.visible
import com.github.zawadz88.materialpopupmenu.popupMenu
import kotlinx.android.synthetic.main.item_document.view.*
import kotlinx.android.synthetic.main.item_menu.view.*


class DocumentAdapter(
        private var context: Context,
        private var documents: MutableList<DocumentModel>,
        private val onItemClick: (Int) -> Unit = {},
        private val onDocDeleteClick: (Int) -> Unit = {},
        private val onDocEditClick: (Int) -> Unit = {},

        ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var regular: Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view =
                LayoutInflater.from(context).inflate(R.layout.item_document, parent, false)
        return ViewHolder(view)


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


//        val foregroundSpan = ForegroundColorSpan(ContextCompat.getColor(context,R.color.text_color_gray))
//        val docName = documents[position].documentName + " ( " + documents[position].documentSize + " )"
//        val spannable = SpannableString(
//                docName
//        )
//        spannable.setSpan(foregroundSpan,documents[position].documentName.length,docName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        spannable.setSpan(
//                regular?.let { CustomTypefaceSpan("", it) },
//                documents[position].documentName.length,docName.length,
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//        spannable.setSpan(RelativeSizeSpan(0.9f),  documents[position].documentName.length,docName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.itemView.documentNameTV.text = documents[position].documentName
//        holder.itemView.documentSizeTV.text = "(${documents[position].documentSize})"

        if (documents[position].isEdited){
            holder.itemView.documentLL.background = ContextCompat.getDrawable(context,R.drawable.border_red_round_corner)
            holder.itemView.editIndicatorView.visible()
        }else{
            holder.itemView.documentLL.background = null
            holder.itemView.editIndicatorView.gone()
        }

    }

    override fun getItemCount(): Int {
        return documents.size
    }

    inner class ViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
            itemView.documentOptionIV.setOnClickListener {
                val popupMenu = popupMenu {
                    dropDownHorizontalOffset = -5
                    dropDownVerticalOffset = -10
                    dropdownGravity = Gravity.BOTTOM or Gravity.END
                    section {
                        customItem {
                            layoutResId = R.layout.item_menu
                            viewBoundCallback = { view ->
                                view.optionTV.text = context.getString(R.string.edit)
                                Glide.with(context).load(R.drawable.ic_edit).into(view.optionIV)
                            }
                            callback = {
                                onDocEditClick(adapterPosition)
                            }
                        }

                    }
                    section {
                        customItem {
                            layoutResId = R.layout.item_menu
                            viewBoundCallback = { view ->
                                view.optionTV.text = context.getString(R.string.copy)
                                Glide.with(context).load(R.drawable.ic_copy).into(view.optionIV)
                            }
                            callback = {

                            }
                        }
                    }

                    section {
                        customItem {
                            layoutResId = R.layout.item_menu
                            viewBoundCallback = { view ->
                                view.optionTV.text = context.getString(R.string.delete)
                                Glide.with(context).load(R.drawable.ic_trash).into(view.optionIV)
                            }
                            callback = {
                                onDocDeleteClick(adapterPosition)
                            }
                        }
                    }
                }
                popupMenu.setOnDismissListener {
                    itemView.documentOptionIV.setBackgroundResource(R.drawable.rounded_background_light_blue)
                }
                popupMenu.show(context, itemView.documentOptionIV)
                itemView.documentOptionIV.setBackgroundResource(R.drawable.rounded_background_white)
            }
        }
    }


}


