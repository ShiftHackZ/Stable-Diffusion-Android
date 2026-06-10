package com.shifthackz.aisdv1.presentation.screen.splash

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.SplashRouter
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SplashViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchersProvider = object : DispatchersProvider {
        override val io: CoroutineDispatcher = testDispatcher
        override val ui: CoroutineDispatcher = testDispatcher
        override val immediate: CoroutineDispatcher = testDispatcher
    }
    private val stubSplashRouter = mockk<SplashRouter>(relaxed = true)
    private val stubSplashNavigationUseCase = mockk<SplashNavigationUseCase>()

    @Test
    fun `initialized, use case emits LAUNCH_ONBOARDING action, expected router navigateToOnBoardingFromSplash called`() =
        runTest(testDispatcher) {
            coEvery {
                stubSplashNavigationUseCase()
            } returns SplashNavigationUseCase.Action.LAUNCH_ONBOARDING

            createViewModel()
            advanceUntilIdle()

            verify {
                stubSplashRouter.navigateToOnBoardingFromSplash()
            }
        }

    @Test
    fun `initialized, use case emits LAUNCH_SERVER_SETUP action, expected router navigateToServerSetupFromSplash called`() =
        runTest(testDispatcher) {
            coEvery {
                stubSplashNavigationUseCase()
            } returns SplashNavigationUseCase.Action.LAUNCH_SERVER_SETUP

            createViewModel()
            advanceUntilIdle()

            verify {
                stubSplashRouter.navigateToServerSetupFromSplash()
            }
        }

    @Test
    fun `initialized, use case emits LAUNCH_HOME action, expected router navigateToPostSplashConfigLoader called`() =
        runTest(testDispatcher) {
            coEvery {
                stubSplashNavigationUseCase()
            } returns SplashNavigationUseCase.Action.LAUNCH_HOME

            createViewModel()
            advanceUntilIdle()

            verify {
                stubSplashRouter.navigateToPostSplashConfigLoader()
            }
        }

    @Test
    fun `initialized, use case failed, expected router navigateToServerSetupFromSplash called`() =
        runTest(testDispatcher) {
            coEvery {
                stubSplashNavigationUseCase()
            } throws Throwable("Unable to resolve initial route.")

            createViewModel()
            advanceUntilIdle()

            verify {
                stubSplashRouter.navigateToServerSetupFromSplash()
            }
        }

    private fun TestScope.createViewModel() = SplashViewModel(
        dispatchersProvider = dispatchersProvider,
        splashNavigationUseCase = stubSplashNavigationUseCase,
        splashRouter = stubSplashRouter,
    )
}
