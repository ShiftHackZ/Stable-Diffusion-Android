package com.shifthackz.aisdv1.presentation.widget.connectivity

import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.connectivity.ObserveSeverConnectivityUseCase
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ConnectivityViewModelTest : CoreViewModelTest<ConnectivityViewModel>() {

    private val stubSettings = BehaviorSubject.create<Settings>()
    private val stubConnected = BehaviorSubject.create<Boolean>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubObserveSeverConnectivityUseCase = mockk<ObserveSeverConnectivityUseCase>()

    override fun initializeViewModel() = ConnectivityViewModel(
        preferenceManager = stubPreferenceManager,
        observeServerConnectivityUseCase = stubObserveSeverConnectivityUseCase,
        schedulersProvider = stubSchedulersProvider,
    )

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubPreferenceManager.observe()
        } returns stubSettings.toFlowable(BackpressureStrategy.LATEST)

        every {
            stubPreferenceManager::monitorConnectivity.get()
        } returns true

        every {
            stubObserveSeverConnectivityUseCase()
        } returns stubConnected.toFlowable(BackpressureStrategy.LATEST)
    }

    @Test
    fun `initialized, monitorConnectivity true, expected UI state is Uninitialized, enabled is true`() {
        runTest {
            val expected = ConnectivityState.Uninitialized(true)
            val actual = viewModel.state.value
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `initialized, monitorConnectivity false, expected UI state is Uninitialized, enabled is false`() {
        every {
            stubPreferenceManager::monitorConnectivity.get()
        } returns false

        runTest {
            val expected = ConnectivityState.Uninitialized(false)
            val actual = viewModel.state.value
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given monitorConnectivity true, connected, expected UI state is Connected, enabled is true`() {
        stubSettings.onNext(Settings(monitorConnectivity = true))
        stubConnected.onNext(true)
        runTest {
            val expected = ConnectivityState.Connected(true)
            val actual = viewModel.state.value
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given monitorConnectivity false, connected, expected UI state is Connected, enabled is false`() {
        stubSettings.onNext(Settings(monitorConnectivity = false))
        stubConnected.onNext(true)
        runTest {
            val expected = ConnectivityState.Connected(false)
            val actual = viewModel.state.value
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given monitorConnectivity true, disconnected, expected UI state is Disconnected, enabled is true`() {
        stubSettings.onNext(Settings(monitorConnectivity = true))
        stubConnected.onNext(false)
        runTest {
            val expected = ConnectivityState.Disconnected(true)
            val actual = viewModel.state.value
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given monitorConnectivity false, disconnected, expected UI state is Disconnected, enabled is false`() {
        stubSettings.onNext(Settings(monitorConnectivity = false))
        stubConnected.onNext(false)
        runTest {
            val expected = ConnectivityState.Disconnected(false)
            val actual = viewModel.state.value
            Assert.assertEquals(expected, actual)
        }
    }
}
