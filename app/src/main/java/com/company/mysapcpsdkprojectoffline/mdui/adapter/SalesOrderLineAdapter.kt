package com.company.mysapcpsdkprojectoffline.mdui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.mysapcpsdkproject.datamodel.OrderLineModel
import com.company.mysapcpsdkprojectoffline.R
import kotlinx.android.synthetic.main.item_order_line.view.*
import kotlinx.android.synthetic.main.item_sales_order.view.*

class SalesOrderLineAdapter(
        private var context: Context,
        private var salesOrderLine: MutableList<OrderLineModel>,
        private val onItemClick: (Int) -> Unit = {}

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view =
                LayoutInflater.from(context).inflate(R.layout.item_order_line, parent, false)
        return ViewHolder(view)


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder.itemView.orderLineIndexTV.text = "${position + 1}"
        holder.itemView.orderLineTitleTV.text = salesOrderLine[position].title

    }

    override fun getItemCount(): Int {
        return salesOrderLine.size
    }

    inner class ViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }


}


