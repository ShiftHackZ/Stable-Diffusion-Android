package com.shifthackz.aisdv1.app.di

import com.shifthackz.aisdv1.nonfree.admob.di.adMobModule
import com.shifthackz.aisdv1.nonfree.iap.di.iapModule
import com.shifthackz.aisdv1.nonfree.localization.di.nonFreeLocalizationModule
import com.shifthackz.aisdv1.nonfree.sdaicloud.di.sdaiCloudModule
import com.shifthackz.aisdv1.nonfree.sdaicloud.di.sdaiCloudPlatformModule
import com.shifthackz.aisdv1.nonfree.sdaiclouduikit.di.sdaiCloudUiKitModule
import org.koin.core.module.Module

internal val monetizationFeatureModule: Array<Module> = arrayOf(
    nonFreeLocalizationModule,
    adMobModule,
    iapModule,
    sdaiCloudPlatformModule,
    sdaiCloudModule,
    sdaiCloudUiKitModule,
)
