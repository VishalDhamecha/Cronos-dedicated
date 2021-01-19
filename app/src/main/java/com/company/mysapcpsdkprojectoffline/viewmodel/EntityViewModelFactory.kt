package com.company.mysapcpsdkprojectoffline.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.os.Parcelable

import com.company.mysapcpsdkprojectoffline.viewmodel.asalesordertype.ASalesOrderTypeViewModel
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesorderheaderpartnertype.ASalesOrderHeaderPartnerTypeViewModel
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesorderheaderprelementtype.ASalesOrderHeaderPrElementTypeViewModel
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesorderitemtype.ASalesOrderItemTypeViewModel
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesorderitempartnertype.ASalesOrderItemPartnerTypeViewModel
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesorderitemprelementtype.ASalesOrderItemPrElementTypeViewModel
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesorderitemtexttype.ASalesOrderItemTextTypeViewModel
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesorderschedulelinetype.ASalesOrderScheduleLineTypeViewModel
import com.company.mysapcpsdkprojectoffline.viewmodel.asalesordertexttype.ASalesOrderTextTypeViewModel
import com.company.mysapcpsdkprojectoffline.viewmodel.aslsordpaymentplanitemdetailstype.ASlsOrdPaymentPlanItemDetailsTypeViewModel

/**
 * Custom factory class, which can create view models for entity subsets, which are
 * reached from a parent entity through a navigation property.
 *
 * @param application parent application
 * @param navigationPropertyName name of the navigation link
 * @param entityData parent entity
 */
class EntityViewModelFactory (
        val application: Application, // name of the navigation property
        val navigationPropertyName: String, // parent entity
        val entityData: Parcelable) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass.simpleName) {
			"ASalesOrderTypeViewModel" -> ASalesOrderTypeViewModel(application, navigationPropertyName, entityData) as T
            			"ASalesOrderHeaderPartnerTypeViewModel" -> ASalesOrderHeaderPartnerTypeViewModel(application, navigationPropertyName, entityData) as T
            			"ASalesOrderHeaderPrElementTypeViewModel" -> ASalesOrderHeaderPrElementTypeViewModel(application, navigationPropertyName, entityData) as T
            			"ASalesOrderItemTypeViewModel" -> ASalesOrderItemTypeViewModel(application, navigationPropertyName, entityData) as T
            			"ASalesOrderItemPartnerTypeViewModel" -> ASalesOrderItemPartnerTypeViewModel(application, navigationPropertyName, entityData) as T
            			"ASalesOrderItemPrElementTypeViewModel" -> ASalesOrderItemPrElementTypeViewModel(application, navigationPropertyName, entityData) as T
            			"ASalesOrderItemTextTypeViewModel" -> ASalesOrderItemTextTypeViewModel(application, navigationPropertyName, entityData) as T
            			"ASalesOrderScheduleLineTypeViewModel" -> ASalesOrderScheduleLineTypeViewModel(application, navigationPropertyName, entityData) as T
            			"ASalesOrderTextTypeViewModel" -> ASalesOrderTextTypeViewModel(application, navigationPropertyName, entityData) as T
             else -> ASlsOrdPaymentPlanItemDetailsTypeViewModel(application, navigationPropertyName, entityData) as T
        }
    }
}
