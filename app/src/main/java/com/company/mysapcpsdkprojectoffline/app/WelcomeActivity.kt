package com.company.mysapcpsdkprojectoffline.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.sap.cloud.mobile.fiori.onboarding.LaunchScreen
import com.sap.cloud.mobile.fiori.onboarding.ext.LaunchScreenSettings
import com.sap.cloud.mobile.flowv2.core.DialogHelper
import com.sap.cloud.mobile.flowv2.core.Flow
import com.sap.cloud.mobile.flowv2.core.FlowContextBuilder
import com.sap.cloud.mobile.flowv2.model.FlowConstants
import com.sap.cloud.mobile.foundation.configurationprovider.*
import com.sap.cloud.mobile.foundation.model.AppConfig

import com.company.mysapcpsdkprojectoffline.R

class WelcomeActivity : AppCompatActivity() {
    private var isFlowStarted: Boolean = false
    private val fManager = this.supportFragmentManager

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runnable = Runnable {
            if (!isFlowStarted) {
                startConfigurationLoader()
            }
        }


        setContentView(R.layout.activity_welcome)
    }

    override fun onResume() {
        super.onResume()
        handler = Handler()
        handler.postDelayed(runnable, 2000L)
    }

    override fun onPause() {
        super.onPause()
        if (::handler.isInitialized)
            handler.removeCallbacks(runnable)
    }

    private fun startConfigurationLoader() {
        val callback: ConfigurationLoaderCallback = object : ConfigurationLoaderCallback() {
            override fun onCompletion(providerIdentifier: ProviderIdentifier?, success: Boolean) {
                if (success) {
                    startFlow(this@WelcomeActivity)
                } else {
                    DialogHelper(application, R.style.OnboardingDefaultTheme_Dialog_Alert)
                            .showOKOnlyDialog(
                                    fManager,
                                    resources.getString(R.string.config_loader_complete_error_description),
                                    null, null, null
                            )
                }
            }

            override fun onError(configurationLoader: ConfigurationLoader, providerIdentifier: ProviderIdentifier, userInputs: UserInputs, configurationProviderError: ConfigurationProviderError) {
                DialogHelper(application, R.style.OnboardingDefaultTheme_Dialog_Alert)
                        .showOKOnlyDialog(
                                fManager, String.format(resources.getString(
                                R.string.config_loader_on_error_description),
                                providerIdentifier.toString(), configurationProviderError.errorMessage
                        ),
                                null, null, null
                        )
                configurationLoader.processRequestedInputs(UserInputs())
            }

            override fun onInputRequired(configurationLoader: ConfigurationLoader, userInputs: UserInputs) {
                configurationLoader.processRequestedInputs(UserInputs())
            }
        }
        val providers = arrayOf<ConfigurationProvider>(FileConfigurationProvider(this, "sap_mobile_services"))
        this.runOnUiThread {
            val loader = ConfigurationLoader(this, callback, providers)
            loader.loadConfiguration()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FlowConstants.FLOW_ACTIVITY_REQUEST_CODE) {
            isFlowStarted = false
            if (resultCode == Activity.RESULT_OK) {
                startActivity(Intent(this, MainBusinessActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                })
            }
        }
    }

    private fun prepareAppConfig(): AppConfig? {
        return try {
            val configData = DefaultPersistenceMethod.getPersistedConfiguration(this)
            AppConfig.createAppConfigFromJsonString(configData.toString())
        } catch (ex: ConfigurationPersistenceException) {
            DialogHelper(this, R.style.OnboardingDefaultTheme_Dialog_Alert)
                    .showOKOnlyDialog(
                            fManager,
                            resources.getString(R.string.config_data_build_json_description),
                            null, null, null
                    )
            null
        } catch (ex: Exception) {
            DialogHelper(this, R.style.OnboardingDefaultTheme_Dialog_Alert)
                    .showOKOnlyDialog(
                            fManager,
                            ex.localizedMessage ?: resources.getString(R.string.error_unknown_app_config),
                            null, null, null
                    )
            null
        }
    }

    internal fun startFlow(activity: Activity) {
        val appConfig = prepareAppConfig() ?: return
        val flowContext =
                FlowContextBuilder()
                    .setApplication(appConfig)
                    .setFlowStateListener(WizardFlowStateListener(activity.application as SAPWizardApplication))
                    .build()
        Flow.start(activity, flowContext = flowContext)
        isFlowStarted = true
    }
}
