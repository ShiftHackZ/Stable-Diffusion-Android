package com.shifthackz.aisdv1.feature.sdxl

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.termux.shared.models.ExecutionCommand
import com.termux.shared.shell.TermuxShellEnvironmentClient
import com.termux.shared.shell.TermuxTask
import com.termux.shared.termux.TermuxConstants
import com.termux.shared.termux.TermuxConstants.TERMUX_APP.RUN_COMMAND_SERVICE


class TmuxStub {

    fun stub1(context: Context) {
        val LOG_TAG = "MainActivity"

        val intent = Intent()
        intent.setClassName(
            TermuxConstants.TERMUX_PACKAGE_NAME,
            TermuxConstants.TERMUX_APP.RUN_COMMAND_SERVICE_NAME
        )
        intent.setAction(RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND)
        intent.putExtra(
            RUN_COMMAND_SERVICE.EXTRA_COMMAND_PATH,
            "/data/data/com.termux/files/usr/bin/top"
        )
        intent.putExtra(RUN_COMMAND_SERVICE.EXTRA_ARGUMENTS, arrayOf("-n", "2"))
        intent.putExtra(RUN_COMMAND_SERVICE.EXTRA_WORKDIR, "/data/data/com.termux/files/home")
        intent.putExtra(RUN_COMMAND_SERVICE.EXTRA_BACKGROUND, false)
        intent.putExtra(RUN_COMMAND_SERVICE.EXTRA_SESSION_ACTION, "0")
        intent.putExtra(RUN_COMMAND_SERVICE.EXTRA_COMMAND_LABEL, "top command")
        intent.putExtra(
            RUN_COMMAND_SERVICE.EXTRA_COMMAND_DESCRIPTION,
            "Runs the top command to show processes using the most resources."
        )


// Create the intent for the IntentService class that should be sent the result by TermuxService
        val pluginResultsServiceIntent = Intent(
            context,
            PluginResultsService::class.java
        )


// Generate a unique execution id for this execution command
        val executionId: Int = PluginResultsService.nextExecutionId


// Optional put an extra that uniquely identifies the command internally for your app.
// This can be an Intent extra as well with more extras instead of just an int.
        pluginResultsServiceIntent.putExtra(PluginResultsService.EXTRA_EXECUTION_ID, executionId)


// Create the PendingIntent that will be used by TermuxService to send result of
// commands back to the IntentService
// Note that the requestCode (currently executionId) must be unique for each pending
// intent, even if extras are different, otherwise only the result of only the first
// execution will be returned since pending intent will be cancelled by android
// after the first result has been sent back via the pending intent and termux
// will not be able to send more.
        val pendingIntent = PendingIntent.getService(
            context, executionId,
            pluginResultsServiceIntent,
            PendingIntent.FLAG_ONE_SHOT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0)
        )
        intent.putExtra(RUN_COMMAND_SERVICE.EXTRA_PENDING_INTENT, pendingIntent)

        try {
            // Send command intent for execution
            Log.d(LOG_TAG, "Sending execution command with id $executionId")
            context.startService(intent)
        } catch (e: Exception) {
            Log.e(
                LOG_TAG,
                "Failed to start execution command with id " + executionId + ": " + e.message
            )
        }
    }

    fun stub(context: Context) {
        val command = "echo Hello, Tmux!"

        val exe = ExecutionCommand(
            5598,
            TermuxConstants.TERMUX_BIN_PREFIX_DIR_PATH + "/echo",
//            arrayOf("\"hello\""),
            null,
            null, null, true, false)

        val task = TermuxTask.execute(
            context,
            exe,
            null,
            TermuxShellEnvironmentClient(),
            true
        )

        val result = exe.resultData?.stdout.toString()

        debugLog("TMUX tas : $task")
        debugLog("TMUX exe : $exe")
        debugLog("TMUX OUT : $result")
    }
}
