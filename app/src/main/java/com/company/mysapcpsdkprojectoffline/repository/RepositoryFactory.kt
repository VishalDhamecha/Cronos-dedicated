package com.company.mysapcpsdkprojectoffline.repository

import com.company.mysapcpsdkprojectoffline.service.SAPServiceManager

import com.sap.cloud.android.odata.api_sales_order_srv_entities.API_SALES_ORDER_SRV_EntitiesMetadata.EntitySets
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderType
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderHeaderPartnerType
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderHeaderPrElementType
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderItemType
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderItemPartnerType
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderItemPrElementType
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderItemTextType
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderScheduleLineType
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASalesOrderTextType
import com.sap.cloud.android.odata.api_sales_order_srv_entities.ASlsOrdPaymentPlanItemDetailsType

import com.sap.cloud.mobile.odata.EntitySet
import com.sap.cloud.mobile.odata.EntityValue
import com.sap.cloud.mobile.odata.Property

import java.util.WeakHashMap

/*
 * Repository factory to construct repository for an entity set
 */
class RepositoryFactory
/**
 * Construct a RepositoryFactory instance. There should only be one repository factory and used
 * throughout the life of the application to avoid caching entities multiple times.
 * @param sapServiceManager - Service manager for interaction with OData service
 */
(private val sapServiceManager: SAPServiceManager?) {
    private val repositories: WeakHashMap<String, Repository<out EntityValue>> = WeakHashMap()

    /**
     * Construct or return an existing repository for the specified entity set
     * @param entitySet - entity set for which the repository is to be returned
     * @param orderByProperty - if specified, collection will be sorted ascending with this property
     * @return a repository for the entity set
     */
    fun getRepository(entitySet: EntitySet, orderByProperty: Property?): Repository<out EntityValue> {
        val aPI_SALES_ORDER_SRV_Entities = sapServiceManager?.aPI_SALES_ORDER_SRV_Entities
        val key = entitySet.localName
        var repository: Repository<out EntityValue>? = repositories[key]
        if (repository == null) {
            repository = when (key) {
                EntitySets.aSalesOrder.localName -> Repository<ASalesOrderType>(aPI_SALES_ORDER_SRV_Entities!!, EntitySets.aSalesOrder, orderByProperty)
                EntitySets.aSalesOrderHeaderPartner.localName -> Repository<ASalesOrderHeaderPartnerType>(aPI_SALES_ORDER_SRV_Entities!!, EntitySets.aSalesOrderHeaderPartner, orderByProperty)
                EntitySets.aSalesOrderHeaderPrElement.localName -> Repository<ASalesOrderHeaderPrElementType>(aPI_SALES_ORDER_SRV_Entities!!, EntitySets.aSalesOrderHeaderPrElement, orderByProperty)
                EntitySets.aSalesOrderItem.localName -> Repository<ASalesOrderItemType>(aPI_SALES_ORDER_SRV_Entities!!, EntitySets.aSalesOrderItem, orderByProperty)
                EntitySets.aSalesOrderItemPartner.localName -> Repository<ASalesOrderItemPartnerType>(aPI_SALES_ORDER_SRV_Entities!!, EntitySets.aSalesOrderItemPartner, orderByProperty)
                EntitySets.aSalesOrderItemPrElement.localName -> Repository<ASalesOrderItemPrElementType>(aPI_SALES_ORDER_SRV_Entities!!, EntitySets.aSalesOrderItemPrElement, orderByProperty)
                EntitySets.aSalesOrderItemText.localName -> Repository<ASalesOrderItemTextType>(aPI_SALES_ORDER_SRV_Entities!!, EntitySets.aSalesOrderItemText, orderByProperty)
                EntitySets.aSalesOrderScheduleLine.localName -> Repository<ASalesOrderScheduleLineType>(aPI_SALES_ORDER_SRV_Entities!!, EntitySets.aSalesOrderScheduleLine, orderByProperty)
                EntitySets.aSalesOrderText.localName -> Repository<ASalesOrderTextType>(aPI_SALES_ORDER_SRV_Entities!!, EntitySets.aSalesOrderText, orderByProperty)
                EntitySets.aSlsOrdPaymentPlanItemDetails.localName -> Repository<ASlsOrdPaymentPlanItemDetailsType>(aPI_SALES_ORDER_SRV_Entities!!, EntitySets.aSlsOrdPaymentPlanItemDetails, orderByProperty)
                else -> throw AssertionError("Fatal error, entity set[$key] missing in generated code")
            }
            repositories[key] = repository
        }
        return repository
    }

    /**
     * Get rid of all cached repositories
     */
    fun reset() {
        repositories.clear()
    }
}
