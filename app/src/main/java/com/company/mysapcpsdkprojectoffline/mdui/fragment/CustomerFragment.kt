package com.company.mysapcpsdkprojectoffline.mdui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.mdui.adapter.ProductListAdapter
import com.company.mysapcpsdkprojectoffline.mdui.datamodel.NotificationModel
import com.company.mysapcpsdkprojectoffline.mdui.datamodel.ProductModel
import kotlinx.android.synthetic.main.fragment_customer.*


class CustomerFragment : Fragment() {

    private var productList: MutableList<ProductModel> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productList.add(ProductModel("ROH","NORM","15/01/2021"))
        productList.add(ProductModel("FERT","NORM","15/01/2021"))
        productList.add(ProductModel("IBAU","NORM","15/01/2021"))
        productList.add(ProductModel("UNBW","NORM","15/01/2021"))
        productList.add(ProductModel("ROH","NORM","15/01/2021"))
        productList.add(ProductModel("FERT","NORM","15/01/2021"))
        productList.add(ProductModel("IBAU","NORM","15/01/2021"))
        productList.add(ProductModel("UNBW","NORM","15/01/2021"))
        productList.add(ProductModel("ROH","NORM","15/01/2021"))
        productList.add(ProductModel("FERT","NORM","15/01/2021"))
        productList.add(ProductModel("IBAU","NORM","15/01/2021"))
        productList.add(ProductModel("UNBW","NORM","15/01/2021"))

        productREC.adapter = ProductListAdapter(requireContext(),productList)

    }
}