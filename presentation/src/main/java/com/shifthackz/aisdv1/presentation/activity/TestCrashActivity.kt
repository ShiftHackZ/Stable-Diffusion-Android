package com.shifthackz.aisdv1.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.domain.appbuild.BuildType
import org.koin.android.ext.android.inject

class TestCrashActivity : ComponentActivity() {

    private class FossSdAiV1TestException : Exception("Test FOSS SDAI v1 exception")
    private class GooglePlayCrashlyticsSdAiV1TestException :
        Exception("Google Play Crashlytics PROPRIETARY SDAI v1 exception")

    private val buildInfoProvider: BuildInfoProvider by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appCrash: () -> Unit = {
            val exception = when (buildInfoProvider.buildType) {
                BuildType.FOSS -> FossSdAiV1TestException()
                BuildType.GOOGLE_PLAY -> GooglePlayCrashlyticsSdAiV1TestException()
            }
            exception.printStackTrace()
            throw exception
        }

        setContent {
            Column(Modifier.fillMaxSize()) {
                Text("DBG: ${buildInfoProvider.isDebug}")
                Text("VER: ${buildInfoProvider.version}")
                Text("NUM: ${buildInfoProvider.buildNumber}")
                Text("----------------------------------------")

                Text("TYPE: ${buildInfoProvider.buildType}")

                Button(
                    modifier = Modifier.padding(vertical = 32.dp),
                    onClick = appCrash,
                ) {
                    Text("Simulate Test Crash")
                }
            }
        }
    }
}
