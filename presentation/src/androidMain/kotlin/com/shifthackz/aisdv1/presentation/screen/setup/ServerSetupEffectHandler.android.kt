package com.shifthackz.aisdv1.presentation.screen.setup

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.shifthackz.aisdv1.core.common.extensions.showToast
import com.shifthackz.aisdv1.presentation.platform.ExternalUrlLauncher
import com.shifthackz.aisdv1.presentation.utils.PermissionUtil

/**
 * Renders the `rememberServerSetupEffectHandler` UI for the SDAI presentation layer.
 *
 * @param urlLauncher URL launcher consumed by the handler.
 * @return Result produced by `rememberServerSetupEffectHandler`.
 * @author Dmitriy Moroz
 */
@Composable
internal actual fun rememberServerSetupEffectHandler(
    urlLauncher: ExternalUrlLauncher,
): (ServerSetupEffect) -> Unit {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val storagePermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        if (!result.values.any { granted -> !granted }) {
            context.showToast("Granted successfully")
        }
    }

    return remember(context, keyboardController, storagePermission, urlLauncher) {
        { effect ->
            when (effect) {
                ServerSetupEffect.HideKeyboard -> keyboardController?.hide()
                ServerSetupEffect.LaunchManageStoragePermission -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        context.startManageAllFilesAccessSettings()
                    } else if (PermissionUtil.checkStoragePermission(context, storagePermission::launch)) {
                        context.showToast("Already Granted")
                    }
                }

                is ServerSetupEffect.OpenUrl -> urlLauncher.openUrl(effect.url)
            }
        }
    }
}

private fun android.content.Context.startManageAllFilesAccessSettings() {
    val packageIntent = Intent(
        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
        Uri.parse("package:$packageName"),
    )
    runCatching {
        startActivity(packageIntent)
    }.getOrElse {
        startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
    }
}
