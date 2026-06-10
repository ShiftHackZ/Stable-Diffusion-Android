package com.shifthackz.aisdv1.presentation.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

/**
 * Provides the `PermissionUtil` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object PermissionUtil {

    /**
     * Executes the `checkStoragePermission` step in the SDAI presentation layer.
     *
     * @param context Android context used by the operation.
     * @param onLaunch callback invoked by the component.
     * @return Result produced by `checkStoragePermission`.
     * @author Dmitriy Moroz
     */
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

    /**
     * Executes the `checkNotificationPermission` step in the SDAI presentation layer.
     *
     * @param context Android context used by the operation.
     * @param onLaunch callback invoked by the component.
     * @return Result produced by `checkNotificationPermission`.
     * @author Dmitriy Moroz
     */
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

    /**
     * Executes the `checkCameraPermission` step in the SDAI presentation layer.
     *
     * @param context Android context used by the operation.
     * @param onLaunch callback invoked by the component.
     * @return Result produced by `checkCameraPermission`.
     * @author Dmitriy Moroz
     */
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
