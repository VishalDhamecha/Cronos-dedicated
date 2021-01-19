package com.company.mysapcpsdkprojectoffline.mdui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.mdui.adapter.NotificationAdapter
import com.company.mysapcpsdkprojectoffline.mdui.datamodel.NotificationModel
import com.company.mysapcpsdkprojectoffline.mdui.datamodel.OrderTypeModel
import kotlinx.android.synthetic.main.fragment_notification.*


class NotificationFragment : Fragment() {

    private var notificationList: MutableList<NotificationModel> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationList.add(NotificationModel("Order updated successfully.","15/01/2021"))
        notificationList.add(NotificationModel("Document updated successfully.","15/01/2021"))
        notificationList.add(NotificationModel("Order created successfully.","15/01/2021"))
        notificationList.add(NotificationModel("Document added successfully.","15/01/2021"))
        notificationList.add(NotificationModel("Order updated successfully.","15/01/2021"))
        notificationList.add(NotificationModel("Document updated successfully.","15/01/2021"))
        notificationList.add(NotificationModel("Order created successfully.","15/01/2021"))
        notificationList.add(NotificationModel("Document added successfully.","15/01/2021"))
        notificationList.add(NotificationModel("Order updated successfully.","15/01/2021"))
        notificationList.add(NotificationModel("Document updated successfully.","15/01/2021"))
        notificationList.add(NotificationModel("Order created successfully.","15/01/2021"))
        notificationList.add(NotificationModel("Document added successfully.","15/01/2021"))

        notificationREC.adapter = NotificationAdapter(requireContext(), notificationList)

    }
}