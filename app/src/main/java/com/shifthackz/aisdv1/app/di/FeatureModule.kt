package com.shifthackz.aisdv1.app.di

import com.shifthackz.aisdv1.feature.auth.di.authModule
import com.shifthackz.aisdv1.feature.diffusion.di.diffusionModule

val featureModule = (
        authModule
                + diffusionModule
        ).toTypedArray()
