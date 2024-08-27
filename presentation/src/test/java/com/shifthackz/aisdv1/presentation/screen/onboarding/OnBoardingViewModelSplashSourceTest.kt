package com.shifthackz.aisdv1.presentation.screen.onboarding

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase
import com.shifthackz.aisdv1.presentation.core.CoreViewModelInitializeStrategy
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.stub.stubDispatchersProvider
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class OnBoardingViewModelSplashSourceTest : CoreViewModelTest<OnBoardingViewModel>() {

    private var source = LaunchSource.SPLASH

    private val stubMainRouter = mockk<MainRouter>()
    private val stubSplashNavigationUseCase = mockk<SplashNavigationUseCase>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubBuildInfoProvider = mockk<BuildInfoProvider>()

    override val testViewModelStrategy = CoreViewModelInitializeStrategy.InitializeEveryTime

    override fun initializeViewModel() = OnBoardingViewModel(
        launchSource = source,
        dispatchersProvider = stubDispatchersProvider,
        mainRouter = stubMainRouter,
        splashNavigationUseCase = stubSplashNavigationUseCase,
        preferenceManager = stubPreferenceManager,
        schedulersProvider = stubSchedulersProvider,
        buildInfoProvider = stubBuildInfoProvider,
    )

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubPreferenceManager::designDarkThemeToken.get()
        } returns DarkThemeToken.FRAPPE.toString()

        every {
            stubPreferenceManager::onBoardingComplete.set(any())
        } returns Unit

        every {
            stubBuildInfoProvider.toString()
        } returns ""

        every {
            stubPreferenceManager::onBoardingComplete.get()
        } returns true
    }

    @Test
    fun `given received Navigate intent, expected onBoardingComplete updated in preference, navigation processed`() {
        source = LaunchSource.SPLASH

        every {
            stubSplashNavigationUseCase()
        } returns Single.just(SplashNavigationUseCase.Action.LAUNCH_HOME)

        viewModel.processIntent(OnBoardingIntent.Navigate)

        verify {
            stubMainRouter.navigateToPostSplashConfigLoader()
        }
    }

    @Test
    fun `given received Navigate intent, expected onBoardingComplete updated in preference, navigateBack processed`() {
        source = LaunchSource.SETTINGS

        every {
            stubMainRouter.navigateBack()
        } returns Unit

        viewModel.processIntent(OnBoardingIntent.Navigate)

        verify {
            stubMainRouter.navigateBack()
        }
    }
}
