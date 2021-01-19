package com.company.mysapcpsdkprojectoffline.mdui

import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.Preference

import com.company.mysapcpsdkprojectoffline.R
import com.sap.cloud.mobile.flowv2.model.FlowType
import com.sap.cloud.mobile.flowv2.core.DialogHelper
import com.sap.cloud.mobile.flowv2.core.Flow.Companion.start
import com.sap.cloud.mobile.flowv2.model.FlowConstants
import com.sap.cloud.mobile.flowv2.core.FlowContextRegistry

import androidx.preference.PreferenceManager

import androidx.preference.ListPreference
import ch.qos.logback.classic.Level
import com.sap.cloud.mobile.foundation.settings.policies.LogPolicy
import android.util.Log
import com.company.mysapcpsdkprojectoffline.app.SAPWizardApplication
import com.sap.cloud.mobile.foundation.logging.Logging
import android.widget.Toast
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/** This fragment represents the settings screen. */
class SettingsFragment : PreferenceFragmentCompat(), Logging.UploadListener {

    private var logLevelPreference: ListPreference? = null
    private val levelValues: Array<String> = arrayOf(
        Level.ALL.levelInt.toString(),
        Level.DEBUG.levelInt.toString(),
        Level.INFO.levelInt.toString(),
        Level.WARN.levelInt.toString(),
        Level.ERROR.levelInt.toString(),
        Level.OFF.levelInt.toString()
    )


    private var changePassCodePreference: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        logLevelPreference = findPreference(getString(R.string.log_level))
        prepareLogSetting(logLevelPreference!!)

        // Upload log
        val logUploadPreference : Preference = findPreference(getString(R.string.upload_log))!!
        logUploadPreference.setOnPreferenceClickListener {
            logUploadPreference.isEnabled = false
            Logging.upload()
            false
        }

        changePassCodePreference = findPreference(getString(R.string.manage_passcode))
        changePassCodePreference!!.setOnPreferenceClickListener {
            changePassCodePreference!!.isEnabled = false
            val flowContext =
                FlowContextRegistry.flowContext.copy(flowType = FlowType.CHANGEPASSCODE)
            start(this, flowContext)
            false
        }

        // Reset App
        val resetAppPreference : Preference = findPreference(getString(R.string.reset_app))!!
        resetAppPreference.setOnPreferenceClickListener {
            DialogHelper(
                requireContext(),
                themeResId = R.style.OnboardingDefaultTheme_Dialog_Alert
            ).showDialogWithCancelAction(
                fragmentManager = requireActivity().supportFragmentManager,
                title = requireContext().getString(R.string.reset_app),
                message = requireContext().getString(R.string.reset_app_confirmation),
                positiveButtonCaption = requireContext().getString(R.string.confirm_yes),
                negativeButtonCaption = requireContext().getString(R.string.confirm_no),
                positiveAction = {
                    val flowContext =
                        FlowContextRegistry.flowContext.copy(flowType = FlowType.RESET)
                    start(this, flowContext)
                },
                negativeAction = { Unit }
            )
            false
        }
    }

    override fun onResume() {
        super.onResume()
        Logging.addUploadListener(this)
        logLevelPreference = findPreference(getString(R.string.log_level))
        prepareLogSetting(logLevelPreference!!)
    }

    override fun onPause() {
        super.onPause()
        Logging.removeUploadListener(this)
    }

    override fun onSuccess() {
        enableLogUploadButton()
        Toast.makeText(activity, R.string.log_upload_ok, Toast.LENGTH_LONG).show()
        LOGGER.info("Log is uploaded to the server.")
    }

    override fun onError(throwable: Throwable) {
        enableLogUploadButton()
        val message = throwable.localizedMessage ?: getString(R.string.log_upload_failed)
        DialogHelper(requireContext()).showOKOnlyDialog(
            fragmentManager = requireActivity().supportFragmentManager,
            message = message
        )
        LOGGER.error("Log upload failed with error message: $message")
    }

    override fun onProgress(i: Int) {
        // You could add a progress indicator and update it from here
    }

    private fun enableLogUploadButton() {
        val logUploadPreference : Preference = findPreference(getString(R.string.upload_log))!!
        logUploadPreference.isEnabled = true
    }

    private fun logStrings() = mapOf<Level, String>(
        Level.ALL to getString(R.string.log_level_path),
        Level.DEBUG to getString(R.string.log_level_debug),
        Level.INFO to getString(R.string.log_level_info),
        Level.WARN to getString(R.string.log_level_warning),
        Level.ERROR to getString(R.string.log_level_error),
        Level.OFF to getString(R.string.log_level_none)
    )

    private fun prepareLogSetting(logLevelPreference: ListPreference) {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
        val str: String? = sharedPreferences.getString(
            SAPWizardApplication.KEY_LOG_SETTING_PREFERENCE,
            LogPolicy().toString()
        )
        val settings = LogPolicy.createFromJsonString(str!!)
        Log.d(TAG, "log settings: $settings")
        logLevelPreference.entries = logStrings().values.toTypedArray()
        logLevelPreference.entryValues = levelValues
        logLevelPreference.isPersistent = true
        logLevelPreference.summary = logStrings()[LogPolicy.getLogLevel(settings)]
        logLevelPreference.value = LogPolicy.getLogLevel(settings).levelInt.toString()
        logLevelPreference.setOnPreferenceChangeListener { preference, newValue ->
            val logLevel = Level.toLevel(Integer.valueOf(newValue as String))
            val newSettings = settings.copy(logLevel = LogPolicy.getLogLevelString(logLevel))
            sharedPreferences.edit()
                .putString(SAPWizardApplication.KEY_LOG_SETTING_PREFERENCE, newSettings.toString())
                .apply()
            LogPolicy.setRootLogLevel(newSettings)
            preference.summary = logStrings()[LogPolicy.getLogLevel(newSettings)]
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FlowConstants.FLOW_ACTIVITY_REQUEST_CODE) {
            changePassCodePreference!!.isEnabled = true
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(SettingsFragment::class.java)
        private val TAG = SettingsFragment::class.simpleName
    }
}
