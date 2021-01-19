package com.company.mysapcpsdkprojectoffline.mdui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.mdui.datamodel.ProductModel
import kotlinx.android.synthetic.main.item_product.view.*

class ProductListAdapter(
        private var context: Context,
        private var productList: MutableList<ProductModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.productNameTV.text = productList[position].productName
        holder.itemView.productDateTV.text = productList[position].productDate
        holder.itemView.productCategoryTV.text = productList[position].productCategory
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ViewHolder(val v: View) : RecyclerView.ViewHolder(v)

}


