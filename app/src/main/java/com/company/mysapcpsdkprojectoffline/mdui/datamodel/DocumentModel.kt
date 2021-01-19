package com.company.mysapcpsdkprojectoffline.mdui.datamodel

import java.io.Serializable

data class DocumentModel(
        var documentName: String,
        var documentSize: String,
        var isEdited: Boolean
) : Serializable