package com.shifthackz.aisdv1.presentation.screen.splash

import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase
import com.shifthackz.aisdv1.presentation.core.CoreViewModelInitializeStrategy
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class SplashViewModelTest : CoreViewModelTest<SplashViewModel>() {

    private val stubMainRouter = mockk<MainRouter>()
    private val stubSplashNavigationUseCase = mockk<SplashNavigationUseCase>()

    override val testViewModelStrategy = CoreViewModelInitializeStrategy.InitializeEveryTime

    override fun initializeViewModel() = SplashViewModel(
        mainRouter = stubMainRouter,
        splashNavigationUseCase = stubSplashNavigationUseCase,
        schedulersProvider = stubSchedulersProvider,
    )

    @Test
    fun `given initialized, use case emits LAUNCH_ONBOARDING action, expected nothing happens`() {
        every {
            stubSplashNavigationUseCase()
        } returns Single.just(SplashNavigationUseCase.Action.LAUNCH_ONBOARDING)

        viewModel.hashCode()

        verify(inverse = true) {
            stubMainRouter.navigateToServerSetup(ServerSetupLaunchSource.SPLASH)
        }
        verify(inverse = true) {
            stubMainRouter.navigateToPostSplashConfigLoader()
        }
    }

    @Test
    fun `given initialized, use case emits LAUNCH_SERVER_SETUP action, expected router navigateToServerSetup() method called`() {
        every {
            stubMainRouter.navigateToServerSetup(any())
        } returns Unit

        every {
            stubSplashNavigationUseCase()
        } returns Single.just(SplashNavigationUseCase.Action.LAUNCH_SERVER_SETUP)

        viewModel.hashCode()

        verify {
            stubMainRouter.navigateToServerSetup(
                ServerSetupLaunchSource.SPLASH
            )
        }
    }

    @Test
    fun `given initialized, use case emits LAUNCH_HOME action, expected router navigateToServerSetup() method called`() {
        every {
            stubMainRouter.navigateToPostSplashConfigLoader()
        } returns Unit

        every {
            stubSplashNavigationUseCase()
        } returns Single.just(SplashNavigationUseCase.Action.LAUNCH_HOME)

        viewModel.hashCode()

        verify {
            stubMainRouter.navigateToPostSplashConfigLoader()
        }
    }
}
