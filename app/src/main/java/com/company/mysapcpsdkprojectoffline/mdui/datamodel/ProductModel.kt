package com.company.mysapcpsdkprojectoffline.mdui.datamodel

import java.io.Serializable

data class ProductModel(
        var productName: String,
        var productCategory: String,
        var productDate: String
) : Serializable