package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersToken
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.debug.DebugInsertBadBase64UseCase
import com.shifthackz.aisdv1.presentation.navigation.router.DebugMenuRouter
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DebugMenuViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchersProvider = object : DispatchersProvider {
        override val io: CoroutineDispatcher = testDispatcher
        override val ui: CoroutineDispatcher = testDispatcher
        override val immediate: CoroutineDispatcher = testDispatcher
    }
    private val stubDebugInsertBadBase64UseCase = mockk<DebugInsertBadBase64UseCase>()
    private val stubRouter = mockk<DebugMenuRouter>(relaxed = true)
    private val stubPreferenceManager = mockk<PreferenceManager>(relaxed = true)
    private val stubPlatformActions = mockk<DebugMenuPlatformActions>()

    @Before
    fun initialize() {
        every {
            stubPreferenceManager.observe()
        } returns flowOf(
            Settings(
                localDiffusionAllowCancel = true,
                localDiffusionSchedulerThread = SchedulersToken.IO_THREAD,
            ),
        )
        every {
            stubPlatformActions.showWorkManagerSection
        } returns true
        every {
            stubPlatformActions.showLocalDiffusionSection
        } returns true
    }

    @Test
    fun `initialized, expected settings and platform sections reflected in state`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            val expected = DebugMenuState(
                localDiffusionAllowCancel = true,
                localDiffusionSchedulerThread = SchedulersToken.IO_THREAD,
                showWorkManagerSection = true,
                showLocalDiffusionSection = true,
            )
            Assert.assertEquals(expected, viewModel.state.value)
        }

    @Test
    fun `given received NavigateBack intent, expected router navigateBack method called`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.processIntent(DebugMenuIntent.NavigateBack)

            verify {
                stubRouter.navigateBack()
            }
        }

    @Test
    fun `given received ViewLogs intent, expected router navigateToLogger method called`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.processIntent(DebugMenuIntent.ViewLogs)

            verify {
                stubRouter.navigateToLogger()
            }
        }

    @Test
    fun `given received InsertBadBase64 intent, expected debugInsertBadBase64UseCase method called`() =
        runTest(testDispatcher) {
            coEvery {
                stubDebugInsertBadBase64UseCase()
            } returns Unit

            val viewModel = createViewModel()
            viewModel.processIntent(DebugMenuIntent.InsertBadBase64)
            advanceUntilIdle()

            coVerify {
                stubDebugInsertBadBase64UseCase()
            }
        }

    @Test
    fun `given received ClearLogs intent, expected platform clearLogs method called and success effect emitted`() =
        runTest(testDispatcher) {
            coEvery {
                stubPlatformActions.clearLogs()
            } returns Result.success(Unit)

            val viewModel = createViewModel()
            viewModel.processIntent(DebugMenuIntent.ClearLogs)
            advanceUntilIdle()

            coVerify {
                stubPlatformActions.clearLogs()
            }
            Assert.assertEquals(
                DebugMenuEffect.Message(Localization.string("success", languageCode = "en")),
                viewModel.effect.firstOrNull(),
            )
        }

    @Test
    fun `given received AllowLocalDiffusionCancel intent, expected preference flag toggled`() =
        runTest(testDispatcher) {
            justRun {
                stubPreferenceManager.localOnnxAllowCancel = false
            }

            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.processIntent(DebugMenuIntent.AllowLocalDiffusionCancel)

            verify {
                stubPreferenceManager.localOnnxAllowCancel = false
            }
        }

    @Test
    fun `given received LocalDiffusionScheduler Request intent, expected scheduler modal shown`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(DebugMenuIntent.LocalDiffusionScheduler.Request)

            Assert.assertEquals(
                DebugMenuModal.LDScheduler(SchedulersToken.IO_THREAD),
                viewModel.state.value.screenModal,
            )
        }

    @Test
    fun `given received LocalDiffusionScheduler Confirm intent, expected preference scheduler updated and modal dismissed`() =
        runTest(testDispatcher) {
            justRun {
                stubPreferenceManager.localOnnxSchedulerThread = SchedulersToken.COMPUTATION
            }

            val viewModel = createViewModel()
            viewModel.processIntent(DebugMenuIntent.LocalDiffusionScheduler.Confirm(SchedulersToken.COMPUTATION))

            verify {
                stubPreferenceManager.localOnnxSchedulerThread = SchedulersToken.COMPUTATION
            }
            Assert.assertEquals(DebugMenuModal.None, viewModel.state.value.screenModal)
        }

    @Test
    fun `given local diffusion section hidden and scheduler requested, expected modal is not shown`() =
        runTest(testDispatcher) {
            every {
                stubPlatformActions.showLocalDiffusionSection
            } returns false

            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.processIntent(DebugMenuIntent.LocalDiffusionScheduler.Request)

            Assert.assertEquals(DebugMenuModal.None, viewModel.state.value.screenModal)
        }

    private fun TestScope.createViewModel() = DebugMenuViewModel(
        dispatchersProvider = dispatchersProvider,
        preferenceManager = stubPreferenceManager,
        debugInsertBadBase64UseCase = stubDebugInsertBadBase64UseCase,
        router = stubRouter,
        platformActions = stubPlatformActions,
    )
}
