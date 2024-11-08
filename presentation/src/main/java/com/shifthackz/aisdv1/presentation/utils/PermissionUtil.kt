package com.shifthackz.aisdv1.presentation.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

object PermissionUtil {

    fun checkStoragePermission(
        context: Context,
        onLaunch: (missingPermissions: Array<String>) -> Unit = {},
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return false
        }
        val missingPermissions = buildList {
            Manifest.permission.READ_EXTERNAL_STORAGE.takeIf {
                ActivityCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }?.let(::add)

            Manifest.permission.WRITE_EXTERNAL_STORAGE.takeIf {
                ActivityCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }?.let(::add)
        }
        if (missingPermissions.isEmpty()) return true
        onLaunch(missingPermissions.toTypedArray())
        return false
    }

    fun checkNotificationPermission(
        context: Context,
        onLaunch: (missingPermission: String) -> Unit = {},
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onLaunch(Manifest.permission.POST_NOTIFICATIONS)
            return false
        }
        return true
    }

    fun checkCameraPermission(
        context: Context,
        onLaunch: (missingPermission: String) -> Unit,
    ): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onLaunch(Manifest.permission.CAMERA)
            return false
        }
        return true
    }
}
