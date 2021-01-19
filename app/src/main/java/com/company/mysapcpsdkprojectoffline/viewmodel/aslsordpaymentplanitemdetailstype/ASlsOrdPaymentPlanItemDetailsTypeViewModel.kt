package com.company.mysapcpsdkprojectoffline.viewmodel.aslsordpaymentplanitemdetailstype

import android.app.Application
import android.os.Parcelable

import com.company.mysapcpsdkprojectoffline.viewmodel.EntityViewModel
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASlsOrdPaymentPlanItemDetailsType
import com.sap.cloud.android.odata.api_sales_order_srv_entities.API_SALES_ORDER_SRV_EntitiesMetadata.EntitySets

/*
 * Represents View model for ASlsOrdPaymentPlanItemDetailsType
 *
 * Having an entity view model for each <T> allows the ViewModelProvider to cache and return the view model of that
 * type. This is because the ViewModelStore of ViewModelProvider cannot not be able to tell the difference between
 * EntityViewModel<type1> and EntityViewModel<type2>.
 */
class ASlsOrdPaymentPlanItemDetailsTypeViewModel(application: Application): EntityViewModel<ASlsOrdPaymentPlanItemDetailsType>(application, EntitySets.aSlsOrdPaymentPlanItemDetails, ASlsOrdPaymentPlanItemDetailsType.paymentPlan) {
    /**
     * Constructor for a specific view model with navigation data.
     * @param [navigationPropertyName] - name of the navigation property
     * @param [entityData] - parent entity (starting point of the navigation)
     */
    constructor(application: Application, navigationPropertyName: String, entityData: Parcelable): this(application) {
        EntityViewModel<ASlsOrdPaymentPlanItemDetailsType>(application, EntitySets.aSlsOrdPaymentPlanItemDetails, ASlsOrdPaymentPlanItemDetailsType.paymentPlan, navigationPropertyName, entityData)
    }
}
