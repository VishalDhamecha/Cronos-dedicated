package com.company.mysapcpsdkprojectoffline.mdui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.mysapcpsdkprojectoffline.mdui.datamodel.OrderTypeModel
import com.company.mysapcpsdkprojectoffline.R
import kotlinx.android.synthetic.main.item_order_type.view.*

class OrderTypePagerAdapter(
        val context: Context,
        private val orderType: MutableList<OrderTypeModel>,
        private val onItemClick: (Int) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View = LayoutInflater.from(context)
                .inflate(R.layout.item_order_type, parent, false)
        return OrderTypeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return orderType.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder.itemView.orderTypeTitleTV.text = orderType[position].title
        holder.itemView.orderTypeTotalAmountTV.text = orderType[position].amount

//        if (position == selectedPosition) {
//            holder.itemView.orderCardType.setCardBackgroundColor(ContextCompat.getColor(context, R.color.order_card_selected))
//        } else {
//            holder.itemView.orderCardType.setCardBackgroundColor(ContextCompat.getColor(context, R.color.order_card_unselected))
//        }

    }

    inner class OrderTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {

            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }
}