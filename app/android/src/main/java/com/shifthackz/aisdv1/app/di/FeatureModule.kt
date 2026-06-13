package com.shifthackz.aisdv1.app.di

import com.shifthackz.aisdv1.feature.auth.di.authModule
import com.shifthackz.aisdv1.feature.coreml.di.coreMlModule
import com.shifthackz.aisdv1.feature.mediapipe.di.mediaPipeModule
import com.shifthackz.aisdv1.feature.onnx.di.onnxModule
import com.shifthackz.aisdv1.feature.sdxl.di.sdxlModule

val featureModule = arrayOf(
    authModule,
    coreMlModule,
    onnxModule,
    mediaPipeModule,
    sdxlModule,
)
