package com.company.mysapcpsdkprojectoffline.mdui.datamodel

import java.io.Serializable

data class NotificationModel(
        var notificationTitle: String,
        var notificationDate: String
) : Serializable