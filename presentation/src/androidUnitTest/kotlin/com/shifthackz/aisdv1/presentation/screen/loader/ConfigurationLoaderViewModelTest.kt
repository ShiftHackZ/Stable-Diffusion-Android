package com.shifthackz.aisdv1.presentation.screen.loader

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.ConfigurationLoaderRouter
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class ConfigurationLoaderViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchersProvider = object : DispatchersProvider {
        override val io: CoroutineDispatcher = testDispatcher
        override val ui: CoroutineDispatcher = testDispatcher
        override val immediate: CoroutineDispatcher = testDispatcher
    }
    private val stubException = Throwable("Something went wrong.")
    private val stubDataPreLoaderUseCase = mockk<DataPreLoaderUseCase>()
    private val stubGetConfigurationUseCase = mockk<GetConfigurationUseCase>()
    private val stubRouter = mockk<ConfigurationLoaderRouter>(relaxed = true)

    @Test
    fun `initialized, data loaded successfully, expected launching state and router navigateToHomeScreen called`() =
        runTest(testDispatcher) {
            val events = mutableListOf<String>()
            coEvery {
                stubGetConfigurationUseCase()
            } returns Configuration(
                serverUrl = "https://a1111.example.com",
                source = ServerSource.AUTOMATIC1111,
            )
            coEvery {
                stubDataPreLoaderUseCase()
            } coAnswers {
                events += "preload"
            }
            every {
                stubRouter.navigateToHomeScreen()
            } answers {
                events += "navigate"
            }

            val viewModel = createViewModel()
            advanceUntilIdle()

            Assert.assertEquals(
                ConfigurationLoaderState.Status.Launching,
                viewModel.state.value.status,
            )
            verify {
                stubRouter.navigateToHomeScreen()
            }
            Assert.assertEquals(listOf("preload", "navigate"), events)
        }

    @Test
    fun `initialized, data loading failed, expected failed state and router navigateToHomeScreen called`() =
        runTest(testDispatcher) {
            coEvery {
                stubGetConfigurationUseCase()
            } returns Configuration(
                serverUrl = "https://a1111.example.com",
                source = ServerSource.AUTOMATIC1111,
            )
            coEvery {
                stubDataPreLoaderUseCase()
            } throws stubException

            val viewModel = createViewModel()
            advanceUntilIdle()

            Assert.assertEquals(
                ConfigurationLoaderState.Status.Failed,
                viewModel.state.value.status,
            )
            verify {
                stubRouter.navigateToHomeScreen()
            }
        }

    @Test
    fun `initialized with cloud source, expected preload skipped and router navigateToHomeScreen called`() =
        runTest(testDispatcher) {
            coEvery {
                stubGetConfigurationUseCase()
            } returns Configuration(source = ServerSource.HUGGING_FACE)

            val viewModel = createViewModel()
            advanceUntilIdle()

            Assert.assertEquals(
                ConfigurationLoaderState.Status.Launching,
                viewModel.state.value.status,
            )
            coVerify(exactly = 0) {
                stubDataPreLoaderUseCase()
            }
            verify {
                stubRouter.navigateToHomeScreen()
            }
        }

    @Test
    fun `initialized with localhost a1111 source, expected preload skipped and router navigateToHomeScreen called`() =
        runTest(testDispatcher) {
            coEvery {
                stubGetConfigurationUseCase()
            } returns Configuration(
                serverUrl = "http://localhost",
                source = ServerSource.AUTOMATIC1111,
            )

            val viewModel = createViewModel()
            advanceUntilIdle()

            Assert.assertEquals(
                ConfigurationLoaderState.Status.Launching,
                viewModel.state.value.status,
            )
            coVerify(exactly = 0) {
                stubDataPreLoaderUseCase()
            }
            verify {
                stubRouter.navigateToHomeScreen()
            }
        }

    private fun TestScope.createViewModel() = ConfigurationLoaderViewModel(
        dispatchersProvider = dispatchersProvider,
        dataPreLoaderUseCase = stubDataPreLoaderUseCase,
        getConfigurationUseCase = stubGetConfigurationUseCase,
        router = stubRouter,
    )
}
