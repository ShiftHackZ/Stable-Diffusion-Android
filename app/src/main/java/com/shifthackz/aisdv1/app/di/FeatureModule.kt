package com.shifthackz.aisdv1.app.di

import com.shifthackz.aisdv1.feature.auth.di.authModule
import com.shifthackz.aisdv1.feature.diffusion.di.diffusionModule
import org.koin.core.module.Module

val featureModule: Array<Module> = (authModule + diffusionModule).toTypedArray()
