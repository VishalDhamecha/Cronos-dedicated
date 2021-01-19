package com.company.mysapcpsdkprojectoffline.app

import android.content.Intent
import android.util.Log

import com.sap.cloud.mobile.flowv2.ext.FlowStateListener
import com.sap.cloud.mobile.foundation.model.AppConfig
import androidx.preference.PreferenceManager
import android.widget.Toast
import com.sap.cloud.mobile.foundation.authentication.AppLifecycleCallbackHandler
import com.sap.cloud.mobile.foundation.settings.policies.LogPolicy
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.Level
import com.company.mysapcpsdkprojectoffline.R
import javax.crypto.Cipher

class WizardFlowStateListener(private val application: SAPWizardApplication) :
    FlowStateListener() {

    override fun onAppConfigRetrieved(appConfig: AppConfig) {
        Log.d(TAG, "onAppConfigRetrieved: $appConfig")
        application.initializeServiceManager(appConfig)
        application.appConfig = appConfig
    }

    override fun onApplicationReset() {
        Log.d(TAG, "onApplicationReset executing...")
        this.application.resetApplication()
        Intent(application, WelcomeActivity::class.java).also {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            application.startActivity(it)
        }
    }

    override fun onApplicationLocked() {
        super.onApplicationLocked()
        application.isApplicationUnlocked = false
    }

    override fun onUnlockWithPasscode(code: CharArray) {
        super.onUnlockWithPasscode(code)
        application.isApplicationUnlocked = true
    }

    override fun onUnlockWithCipher(cipher: Cipher) {
        super.onUnlockWithCipher(cipher)
        application.isApplicationUnlocked = true
    }

    override fun onBoarded() {
        super.onBoarded()
        application.isApplicationUnlocked = true
    }

    override fun onLogSettingsRetrieved(logSettings: LogPolicy) {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(application)
        val existing =
            sharedPreferences.getString(SAPWizardApplication.KEY_LOG_SETTING_PREFERENCE, "")
        val currentSettings = if (existing.isNullOrEmpty()) {
            LogPolicy()
        } else {
            LogPolicy.createFromJsonString(existing)
        }
        if (currentSettings.logLevel != logSettings.logLevel || existing.isNullOrEmpty()) {
            val editor = sharedPreferences.edit()
            editor.putString(
                SAPWizardApplication.KEY_LOG_SETTING_PREFERENCE,
                logSettings.toString()
            )
            editor.apply()
            LogPolicy.setRootLogLevel(logSettings)
            AppLifecycleCallbackHandler.getInstance().activity?.let {
                it.runOnUiThread {
                    val logString = when (LogPolicy.getLogLevel(logSettings)) {
                        Level.ALL -> application.getString(R.string.log_level_path)
                        Level.INFO -> application.getString(R.string.log_level_info)
                        Level.WARN -> application.getString(R.string.log_level_warning)
                        Level.ERROR -> application.getString(R.string.log_level_error)
                        Level.OFF -> application.getString(R.string.log_level_none)
                        else -> application.getString(R.string.log_level_debug)
                    }
                    Toast.makeText(
                        application,
                        String.format(
                            application.getString(R.string.log_level_changed),
                            logString
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                    logger.info(String.format(
                            application.getString(R.string.log_level_changed),
                            logString
                    ))
                }
            }
        }
    }



    companion object {
        private val logger = LoggerFactory.getLogger(WizardFlowStateListener::class.java)
        private val TAG = WizardFlowStateListener::class.simpleName
    }
}
