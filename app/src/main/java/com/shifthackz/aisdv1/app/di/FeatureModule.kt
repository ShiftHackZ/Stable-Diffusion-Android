package com.shifthackz.aisdv1.app.di

import com.shifthackz.aisdv1.feature.auth.di.authModule
import com.shifthackz.aisdv1.feature.diffusion.di.diffusionModule
import com.shifthackz.aisdv1.feature.mediapipe.di.mediaPipeModule

val featureModule = arrayOf(
    authModule,
    diffusionModule,
    mediaPipeModule,
)
