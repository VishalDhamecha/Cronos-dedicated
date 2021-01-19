package com.company.mysapcpsdkprojectoffline.viewmodel.asalesorderheaderpartnertype

import android.app.Application
import android.os.Parcelable

import com.company.mysapcpsdkprojectoffline.viewmodel.EntityViewModel
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderHeaderPartnerType
import com.sap.cloud.android.odata.api_sales_order_srv_entities.API_SALES_ORDER_SRV_EntitiesMetadata.EntitySets

/*
 * Represents View model for ASalesOrderHeaderPartnerType
 *
 * Having an entity view model for each <T> allows the ViewModelProvider to cache and return the view model of that
 * type. This is because the ViewModelStore of ViewModelProvider cannot not be able to tell the difference between
 * EntityViewModel<type1> and EntityViewModel<type2>.
 */
class ASalesOrderHeaderPartnerTypeViewModel(application: Application): EntityViewModel<ASalesOrderHeaderPartnerType>(application, EntitySets.aSalesOrderHeaderPartner, ASalesOrderHeaderPartnerType.customer) {
    /**
     * Constructor for a specific view model with navigation data.
     * @param [navigationPropertyName] - name of the navigation property
     * @param [entityData] - parent entity (starting point of the navigation)
     */
    constructor(application: Application, navigationPropertyName: String, entityData: Parcelable): this(application) {
        EntityViewModel<ASalesOrderHeaderPartnerType>(application, EntitySets.aSalesOrderHeaderPartner, ASalesOrderHeaderPartnerType.customer, navigationPropertyName, entityData)
    }
}
