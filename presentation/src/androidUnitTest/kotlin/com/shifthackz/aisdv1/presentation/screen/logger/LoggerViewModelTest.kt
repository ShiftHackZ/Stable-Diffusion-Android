package com.shifthackz.aisdv1.presentation.screen.logger

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.presentation.navigation.router.LoggerRouter
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoggerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchersProvider = object : DispatchersProvider {
        override val io: CoroutineDispatcher = testDispatcher
        override val ui: CoroutineDispatcher = testDispatcher
        override val immediate: CoroutineDispatcher = testDispatcher
    }
    private val stubLogReader = mockk<LogReader>()
    private val stubPlatformActions = mockk<LoggerPlatformActions>(relaxed = true)
    private val stubRouter = mockk<LoggerRouter>(relaxed = true)

    @Test
    fun `initialized, read logs succeeds, expected loaded state with log text`() =
        runTest(testDispatcher) {
            coEvery {
                stubLogReader.read()
            } returns "Sample log line"

            val viewModel = createViewModel()
            advanceUntilIdle()

            Assert.assertEquals(
                LoggerState(
                    loading = false,
                    text = "Sample log line",
                ),
                viewModel.state.value,
            )
        }

    @Test
    fun `initialized, read logs fails, expected loaded state with empty text`() =
        runTest(testDispatcher) {
            coEvery {
                stubLogReader.read()
            } throws IllegalStateException("No logs")

            val viewModel = createViewModel()
            advanceUntilIdle()

            Assert.assertEquals(
                LoggerState(
                    loading = false,
                    text = "",
                ),
                viewModel.state.value,
            )
        }

    @Test
    fun `given received NavigateBack intent, expected router navigateBack called`() =
        runTest(testDispatcher) {
            coEvery {
                stubLogReader.read()
            } returns ""

            val viewModel = createViewModel()
            viewModel.processIntent(LoggerIntent.NavigateBack)

            verify {
                stubRouter.navigateBack()
            }
        }

    @Test
    fun `given received CopyLogs intent with loaded logs, expected platform copyLogs called`() =
        runTest(testDispatcher) {
            coEvery {
                stubLogReader.read()
            } returns "Sample log line"

            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.processIntent(LoggerIntent.CopyLogs)
            advanceUntilIdle()

            coVerify {
                stubPlatformActions.copyLogs("Sample log line")
            }
        }

    @Test
    fun `given received ShareLogs intent with loaded logs, expected platform shareLogs called`() =
        runTest(testDispatcher) {
            coEvery {
                stubLogReader.read()
            } returns "Sample log line"

            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.processIntent(LoggerIntent.ShareLogs)
            advanceUntilIdle()

            coVerify {
                stubPlatformActions.shareLogs("Sample log line")
            }
        }

    private fun TestScope.createViewModel() = LoggerViewModel(
        dispatchersProvider = dispatchersProvider,
        logReader = stubLogReader,
        platformActions = stubPlatformActions,
        router = stubRouter,
    )
}
