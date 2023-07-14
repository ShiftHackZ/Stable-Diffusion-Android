package com.shifthackz.aisdv1.feature.ads

import android.app.Activity
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.common.log.errorLog

import java.lang.Exception

/**
 * Implements EU User Consent Policy for Google Ad Mob.
 *
 * Docs: https://developers.google.com/admob/android/privacy
 */
internal class Ump {

    fun request(activity: Activity, onConsentAllowed: () -> Unit = {}) {
        val params = ConsentRequestParameters
            .Builder()
            .setTagForUnderAgeOfConsent(false)
            .apply {
                if (BuildConfig.DEBUG) {
                    setConsentDebugSettings(
                        ConsentDebugSettings.Builder(activity)
                            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                            .addTestDeviceHashedId("${System.currentTimeMillis()}")
                            .setForceTesting(true)
                            .build()
                    )
                }
            }
            .build()

        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        consentInformation.requestConsentInfoUpdate(activity, params, {
            debugLog("Request consent info form availability: ${consentInformation.isConsentFormAvailable}")
            if (consentInformation.isConsentFormAvailable) {
                loadForm(activity, consentInformation, onConsentAllowed)
            } else {
                onConsentAllowed.invoke()
            }
        }, { error ->
            errorLog(Exception(error.message), error.message)
        });
    }

    private fun loadForm(
        activity: Activity,
        consentInformation: ConsentInformation,
        onLoadAllowed: () -> Unit,
    ) {
        UserMessagingPlatform.loadConsentForm(
            activity,
            { consentForm ->
                if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                    consentForm.show(activity) {
                        if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.OBTAINED) {
                            onLoadAllowed.invoke()
                        }
                        loadForm(activity, consentInformation, onLoadAllowed)
                    }
                }
            },
            { error ->
                errorLog(Exception(error.message), error.message)
            },
        )
    }
}
