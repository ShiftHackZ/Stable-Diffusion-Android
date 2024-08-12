package com.shifthackz.aisdv1.feature.sdxl

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.termux.shared.termux.TermuxConstants.TERMUX_APP.TERMUX_SERVICE


class PluginResultsService : IntentService(PLUGIN_SERVICE_LABEL) {
    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return

        Log.d(LOG_TAG, PLUGIN_SERVICE_LABEL + " received execution result")

        val resultBundle = intent.getBundleExtra(TERMUX_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE)
        if (resultBundle == null) {
            Log.e(
                LOG_TAG,
                "The intent does not contain the result bundle at the \"" + TERMUX_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE + "\" key."
            )
            return
        }

        val executionId = intent.getIntExtra(EXTRA_EXECUTION_ID, 0)

        Log.d(
            LOG_TAG,
            """Execution id $executionId result:
stdout:
```
${resultBundle.getString(TERMUX_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE_STDOUT, "")}
```
stdout_original_length: `${resultBundle.getString(TERMUX_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE_STDOUT_ORIGINAL_LENGTH)}`
stderr:
```
${resultBundle.getString(TERMUX_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE_STDERR, "")}
```
stderr_original_length: `${resultBundle.getString(TERMUX_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE_STDERR_ORIGINAL_LENGTH)}`
exitCode: `${resultBundle.getInt(TERMUX_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE_EXIT_CODE)}`
errCode: `${resultBundle.getInt(TERMUX_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE_ERR)}`
errmsg: `${resultBundle.getString(TERMUX_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE_ERRMSG, "")}`"""
        )
    }

    companion object {
        const val EXTRA_EXECUTION_ID: String = "execution_id"

        private var EXECUTION_ID = 1000

        const val PLUGIN_SERVICE_LABEL: String = "PluginResultsService"

        private const val LOG_TAG = "PluginResultsService"

        @get:Synchronized
        val nextExecutionId: Int
            get() = EXECUTION_ID++
    }
}