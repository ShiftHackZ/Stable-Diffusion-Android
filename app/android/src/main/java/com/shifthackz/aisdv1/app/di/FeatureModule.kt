package com.shifthackz.aisdv1.app.di

import com.shifthackz.aisdv1.feature.auth.di.authModule
import com.shifthackz.aisdv1.feature.onnx.di.onnxModule
import com.shifthackz.aisdv1.feature.mediapipe.di.mediaPipeModule

val featureModule = arrayOf(
    authModule,
    onnxModule,
    mediaPipeModule,
)
