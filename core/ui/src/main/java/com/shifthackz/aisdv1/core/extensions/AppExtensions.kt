package com.shifthackz.aisdv1.core.extensions

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.widget.Toast

fun Context.isAppInForeground(): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val processes = activityManager.runningAppProcesses ?: return false

    return processes.any { process ->
        process.uid == applicationInfo.uid && process.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }
}

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}
