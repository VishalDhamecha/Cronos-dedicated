package com.company.mysapcpsdkprojectoffline.mdui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.mysapcpsdkprojectoffline.R
import kotlinx.android.synthetic.main.item_sales_order.view.*

class ContactPerssonListAdapter(
        private var context: Context,
        private var contactPersonList: MutableList<String>,
        private val onItemClick: (Int) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_contact_person, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


    }

    override fun getItemCount(): Int {
        return contactPersonList.size
    }

    inner class ViewHolder(val v: View) : RecyclerView.ViewHolder(v) {

    }


}


