package com.shifthackz.aisdv1.presentation.di

import com.shifthackz.aisdv1.core.common.links.DefaultLinksProvider
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.createPlatformBuildInfoProvider
import com.shifthackz.aisdv1.core.common.schedulers.DefaultDispatchersProvider
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.common.time.DefaultTimeProvider
import com.shifthackz.aisdv1.core.common.time.TimeProvider
import com.shifthackz.aisdv1.presentation.core.GenerationPlatformServices
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.core.NoOpGenerationPlatformServices
import com.shifthackz.aisdv1.presentation.modal.download.DownloadDialogViewModel
import com.shifthackz.aisdv1.presentation.modal.embedding.EmbeddingViewModel
import com.shifthackz.aisdv1.presentation.modal.extras.ExtrasViewModel
import com.shifthackz.aisdv1.presentation.modal.history.InputHistoryViewModel
import com.shifthackz.aisdv1.presentation.modal.tag.EditTagViewModel
import com.shifthackz.aisdv1.presentation.navigation.router.DonateRouter
import com.shifthackz.aisdv1.presentation.navigation.router.GalleryDetailRouter
import com.shifthackz.aisdv1.presentation.navigation.router.GalleryRouter
import com.shifthackz.aisdv1.presentation.navigation.router.HistoryRouter
import com.shifthackz.aisdv1.presentation.navigation.router.HomeRouter
import com.shifthackz.aisdv1.presentation.navigation.router.ImageToImageRouter
import com.shifthackz.aisdv1.presentation.navigation.router.ConfigurationLoaderRouter
import com.shifthackz.aisdv1.presentation.navigation.router.DebugMenuRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpConfigurationLoaderRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpDebugMenuRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpDonateRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpGalleryDetailRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpGalleryRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpImageToImageRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpLoggerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpOnBoardingRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpReportRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpServerSetupRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpSettingsRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpSplashRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpTextToImageRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpWebUiRouter
import com.shifthackz.aisdv1.presentation.navigation.router.LoggerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.OnBoardingRouter
import com.shifthackz.aisdv1.presentation.navigation.router.ReportRouter
import com.shifthackz.aisdv1.presentation.navigation.router.ServerSetupRouter
import com.shifthackz.aisdv1.presentation.navigation.router.SettingsRouter
import com.shifthackz.aisdv1.presentation.navigation.router.SplashRouter
import com.shifthackz.aisdv1.presentation.navigation.router.TextToImageRouter
import com.shifthackz.aisdv1.presentation.navigation.router.WebUiRouter
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuAccessor
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuPlatformActions
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuViewModel
import com.shifthackz.aisdv1.presentation.screen.debug.NoOpDebugMenuPlatformActions
import com.shifthackz.aisdv1.presentation.screen.donate.DonateViewModel
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailPlatformActions
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailViewModel
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.createDefaultGalleryDetailPlatformActions
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryExportService
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryPlatformActions
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryViewModel
import com.shifthackz.aisdv1.presentation.screen.gallery.list.NoOpGalleryExportService
import com.shifthackz.aisdv1.presentation.screen.gallery.list.NoOpGalleryPlatformActions
import com.shifthackz.aisdv1.presentation.screen.history.HistoryViewModel
import com.shifthackz.aisdv1.presentation.screen.home.HomeViewModel
import com.shifthackz.aisdv1.presentation.screen.img2img.ImageToImagePlatformActions
import com.shifthackz.aisdv1.presentation.screen.img2img.ImageToImageViewModel
import com.shifthackz.aisdv1.presentation.screen.img2img.NoOpImageToImagePlatformActions
import com.shifthackz.aisdv1.presentation.screen.loader.ConfigurationLoaderViewModel
import com.shifthackz.aisdv1.presentation.screen.logger.LogReader
import com.shifthackz.aisdv1.presentation.screen.logger.LoggerViewModel
import com.shifthackz.aisdv1.presentation.screen.logger.NoOpLogReader
import com.shifthackz.aisdv1.presentation.screen.onboarding.OnBoardingViewModel
import com.shifthackz.aisdv1.presentation.screen.report.ReportViewModel
import com.shifthackz.aisdv1.presentation.screen.settings.NoOpSettingsPlatformActions
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsPlatformActions
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsViewModel
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupViewModel
import com.shifthackz.aisdv1.presentation.screen.setup.NoOpServerSetupDownloadGuard
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupDownloadGuard
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageSharer
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageSaver
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageViewModel
import com.shifthackz.aisdv1.presentation.screen.txt2img.createPlatformImageSharer
import com.shifthackz.aisdv1.presentation.screen.txt2img.createPlatformImageSaver
import com.shifthackz.aisdv1.presentation.screen.web.webui.WebUiViewModel
import com.shifthackz.aisdv1.presentation.screen.splash.SplashViewModel
import com.shifthackz.aisdv1.presentation.theme.global.AiSdAppThemeViewModel
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.widget.connectivity.ConnectivityViewModel
import com.shifthackz.aisdv1.presentation.widget.engine.EngineSelectionViewModel
import com.shifthackz.aisdv1.presentation.widget.work.BackgroundWorkViewModel
import com.shifthackz.aisdv1.presentation.widget.work.BackgroundWorkImageLoader
import com.shifthackz.aisdv1.presentation.widget.work.NoOpBackgroundWorkImageLoader
import com.shifthackz.aisdv1.presentation.model.LaunchSource
import org.koin.dsl.module

import org.koin.core.module.Module

/**
 * Executes the `registerPresentationCoreBindings` step in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal fun Module.registerPresentationCoreBindings() {
    single<LinksProvider> { DefaultLinksProvider }
    single<BuildInfoProvider> { createPlatformBuildInfoProvider() }
    single<DispatchersProvider> { DefaultDispatchersProvider }
    single<TimeProvider> { DefaultTimeProvider }
    single<GenerationPlatformServices> { NoOpGenerationPlatformServices }
    single<DebugMenuRouter> { NoOpDebugMenuRouter }
    single<DonateRouter> { NoOpDonateRouter }
    single<LoggerRouter> { NoOpLoggerRouter }
    single<OnBoardingRouter> { NoOpOnBoardingRouter }
    single<ReportRouter> { NoOpReportRouter }
    single<GalleryRouter> { NoOpGalleryRouter }
    single<GalleryDetailRouter> { NoOpGalleryDetailRouter }
    single<TextToImageRouter> { NoOpTextToImageRouter }
    single<ImageToImageRouter> { NoOpImageToImageRouter }
    single<SplashRouter> { NoOpSplashRouter }
    single<ConfigurationLoaderRouter> { NoOpConfigurationLoaderRouter }
    single<ServerSetupRouter> { NoOpServerSetupRouter }
    single<SettingsRouter> { NoOpSettingsRouter }
    single<WebUiRouter> { NoOpWebUiRouter }
    single<LogReader> { NoOpLogReader }
    single<DebugMenuPlatformActions> { NoOpDebugMenuPlatformActions }
    single<SettingsPlatformActions> { NoOpSettingsPlatformActions }
    single<BackgroundWorkImageLoader> { NoOpBackgroundWorkImageLoader }
    single<GalleryExportService> { NoOpGalleryExportService }
    single<GalleryPlatformActions> { NoOpGalleryPlatformActions }
    single<GalleryDetailPlatformActions> { createDefaultGalleryDetailPlatformActions() }
    single<ImageToImagePlatformActions> { NoOpImageToImagePlatformActions }
    single<ServerSetupDownloadGuard> { NoOpServerSetupDownloadGuard }
    single<ImageSaver> { createPlatformImageSaver() }
    single<ImageSharer> { createPlatformImageSharer() }
    single { GenerationFormUpdateEvent() }
    single { DebugMenuAccessor(get()) }
}
