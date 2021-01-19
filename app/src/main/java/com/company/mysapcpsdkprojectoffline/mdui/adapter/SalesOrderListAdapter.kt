package com.company.mysapcpsdkprojectoffline.mdui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.mysapcpsdkprojectoffline.R
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderType
import kotlinx.android.synthetic.main.item_sales_order.view.*

class SalesOrderListAdapter(
        private var context: Context,
        private var salesOrderList: MutableList<ASalesOrderType>,
        private val onItemClick: (Int) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_sales_order, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.customerNameTV.text = salesOrderList[position].createdByUser
        holder.itemView.orderAmountTV.text = "€${salesOrderList[position].totalNetAmount}"
        holder.itemView.orderDateTV.text = "${salesOrderList[position].creationDate?.day}/${salesOrderList[position].creationDate?.month}/${salesOrderList[position].creationDate?.year}"
    }

    override fun getItemCount(): Int {
        return salesOrderList.size
    }

    inner class ViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }


}


