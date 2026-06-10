package com.shifthackz.aisdv1.presentation.widget.connectivity

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.domain.usecase.connectivity.GetMonitorConnectivityUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.ObserveMonitorConnectivityUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.ObserveSeverConnectivityUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ConnectivityViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchersProvider = object : DispatchersProvider {
        override val io: CoroutineDispatcher = testDispatcher
        override val ui: CoroutineDispatcher = testDispatcher
        override val immediate: CoroutineDispatcher = testDispatcher
    }
    private val stubGetMonitorConnectivityUseCase = mockk<GetMonitorConnectivityUseCase>()
    private val stubObserveMonitorConnectivityUseCase = mockk<ObserveMonitorConnectivityUseCase>()
    private val stubObserveSeverConnectivityUseCase = mockk<ObserveSeverConnectivityUseCase>()

    @Before
    fun initialize() {
        every {
            stubGetMonitorConnectivityUseCase()
        } returns true

        every {
            stubObserveMonitorConnectivityUseCase()
        } returns emptyFlow()

        every {
            stubObserveSeverConnectivityUseCase()
        } returns emptyFlow()
    }

    @Test
    fun `initialized, monitorConnectivity true, expected UI state is Uninitialized, enabled is true`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            Assert.assertEquals(
                ConnectivityState.Uninitialized(true),
                viewModel.state.value,
            )
        }

    @Test
    fun `initialized, monitorConnectivity false, expected UI state is Uninitialized, enabled is false`() =
        runTest(testDispatcher) {
            every {
                stubGetMonitorConnectivityUseCase()
            } returns false

            val viewModel = createViewModel()

            Assert.assertEquals(
                ConnectivityState.Uninitialized(false),
                viewModel.state.value,
            )
        }

    @Test
    fun `given monitorConnectivity true, connected, expected UI state is Connected, enabled is true`() =
        runTest(testDispatcher) {
            every {
                stubObserveMonitorConnectivityUseCase()
            } returns flowOf(true)
            every {
                stubObserveSeverConnectivityUseCase()
            } returns flowOf(true)

            val viewModel = createViewModel()
            advanceUntilIdle()

            Assert.assertEquals(
                ConnectivityState.Connected(true),
                viewModel.state.value,
            )
        }

    @Test
    fun `given monitorConnectivity false, disconnected, expected UI state is Disconnected, enabled is false`() =
        runTest(testDispatcher) {
            every {
                stubObserveMonitorConnectivityUseCase()
            } returns flowOf(false)
            every {
                stubObserveSeverConnectivityUseCase()
            } returns flowOf(false)

            val viewModel = createViewModel()
            advanceUntilIdle()

            Assert.assertEquals(
                ConnectivityState.Disconnected(false),
                viewModel.state.value,
            )
        }

    private fun TestScope.createViewModel() = ConnectivityViewModel(
        observeServerConnectivityUseCase = stubObserveSeverConnectivityUseCase,
        getMonitorConnectivityUseCase = stubGetMonitorConnectivityUseCase,
        observeMonitorConnectivityUseCase = stubObserveMonitorConnectivityUseCase,
        dispatchersProvider = dispatchersProvider,
    )
}
