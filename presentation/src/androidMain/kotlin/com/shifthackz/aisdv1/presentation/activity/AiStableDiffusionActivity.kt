package com.shifthackz.aisdv1.presentation.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.presentation.app.AiSdApp
import com.shifthackz.aisdv1.presentation.utils.PermissionUtil
import org.koin.android.ext.android.inject

class AiStableDiffusionActivity : AppCompatActivity() {

    private val preferenceManager: PreferenceManager by inject()

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        debugLog("Notification permission is ${if (granted) "GRANTED" else "DENIED"}.")
    }

    private val storagePermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (!result.values.any { !it }) {
            preferenceManager.saveToMediaStore = true
        }
        debugLog("Storage permission is ${result}.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            val fadeOutAnimation = ObjectAnimator.ofFloat(
                splashScreenViewProvider.view,
                View.ALPHA,
                1f,
                0f
            )
            fadeOutAnimation.duration = 500L
            fadeOutAnimation.doOnEnd {
                PermissionUtil.checkNotificationPermission(this, notificationPermission::launch)
                PermissionUtil.checkStoragePermission(this, storagePermission::launch)
                splashScreenViewProvider.remove()
            }
            fadeOutAnimation.start()
        }
        setContent {
            AiSdApp()
        }
    }
}
