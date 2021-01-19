package com.company.mysapcpsdkprojectoffline.mdui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.mdui.datamodel.NotificationModel
import kotlinx.android.synthetic.main.item_notification.view.*


class NotificationAdapter(
        private var context: Context,
        private var notifications: MutableList<NotificationModel>
        ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view =
                LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.notificationTitleTV.text = notifications[position].notificationTitle
        holder.itemView.notificationDateTV.text = notifications[position].notificationDate
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    inner class ViewHolder(val v: View) : RecyclerView.ViewHolder(v) {

    }


}


