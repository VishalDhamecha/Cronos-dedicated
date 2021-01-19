package com.company.mysapcpsdkprojectoffline.app

import android.app.Application
import androidx.preference.PreferenceManager
import com.sap.cloud.mobile.foundation.authentication.AppLifecycleCallbackHandler
import com.sap.cloud.mobile.foundation.model.AppConfig
import com.company.mysapcpsdkprojectoffline.service.SAPServiceManager
import com.company.mysapcpsdkprojectoffline.repository.RepositoryFactory
import com.sap.cloud.mobile.foundation.mobileservices.MobileService
import com.sap.cloud.mobile.foundation.mobileservices.SDKInitializer
import com.sap.cloud.mobile.foundation.logging.Logging
import com.sap.cloud.mobile.foundation.logging.LogService
import ch.qos.logback.classic.Level

class SAPWizardApplication: Application() {

    /** The [AppConfig] of this application */
    var appConfig: AppConfig? = null
    internal var isApplicationUnlocked = false

    /**
     * Manages and provides access to OData stores providing data for the app.
     */
    internal var sapServiceManager: SAPServiceManager? = null

    /**
     * Application-wide RepositoryFactory
     */
    lateinit var repositoryFactory: RepositoryFactory
        private set

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(AppLifecycleCallbackHandler.getInstance())
        initServices()
    }

    fun initializeServiceManager(appConfig: AppConfig) {
       if(sapServiceManager == null){
          sapServiceManager = SAPServiceManager(appConfig, this)
       }
        repositoryFactory =
            RepositoryFactory(sapServiceManager)
    }

    /**
     * Clears all user-specific data and configuration from the application, essentially resetting it to its initial
     * state.
     *
     * If client code wants to handle the reset logic of a service, here is an example:
     *
     *   SDKInitializer.resetServices { service ->
     *       return@resetServices if( service is PushService ) {
     *           PushService.unregisterPushSync(object: CallbackListener {
     *               override fun onSuccess() {
     *               }
     *
     *               override fun onError(p0: Throwable) {
     *               }
     *           })
     *           true
     *       } else {
     *           false
     *       }
     *   }
     */
    fun resetApplication() {
        PreferenceManager.getDefaultSharedPreferences(this).also {
            it.edit().clear().apply()
        }
        appConfig = null
        isApplicationUnlocked = false
        repositoryFactory.reset()
        SDKInitializer.resetServices()
        sapServiceManager?.reset()
        sapServiceManager = null
    }

    private fun initServices() {
        val services = mutableListOf<MobileService>()
        Logging.setConfigurationBuilder(Logging.ConfigurationBuilder().initialLevel(Level.WARN).logToConsole(true).build())
        services.add(LogService())

        SDKInitializer.start(this, * services.toTypedArray())
    }

    companion object {
        const val KEY_LOG_SETTING_PREFERENCE = "key.log.settings.preference"
        var isNetworkLost = false

    }
}
