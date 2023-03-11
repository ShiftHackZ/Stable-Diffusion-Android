package com.shifthackz.aisdv1.presentation.di

import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.presentation.features.InAppGooglePlayReview
import com.shifthackz.aisdv1.presentation.features.InAppReviewFeature
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailBitmapExporter
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailSharing
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryExporter
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GallerySharing
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsStateProducer
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val presentationModule = (viewModelModule + module {
    factoryOf(::GalleryExporter)
    factoryOf(::GalleryDetailBitmapExporter)
    factoryOf(::GallerySharing)
    factoryOf(::GalleryDetailSharing)
    factoryOf(::SettingsStateProducer)

    factory {
        val buildInfoProvider = get<BuildInfoProvider>()
        when (buildInfoProvider.buildType) {
            BuildType.FOSS -> InAppReviewFeature {}
            BuildType.GOOGLE_PLAY -> {
                val reviewManager = androidContext().run {
                    if (buildInfoProvider.isDebug) FakeReviewManager(this)
                    else ReviewManagerFactory.create(this)
                }
                InAppGooglePlayReview(reviewManager)
            }
        }
    } bind InAppReviewFeature::class

}).toTypedArray()
