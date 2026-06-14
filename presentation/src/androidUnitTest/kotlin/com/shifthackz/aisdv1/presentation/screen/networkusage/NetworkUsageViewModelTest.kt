package com.shifthackz.aisdv1.presentation.screen.networkusage

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.domain.entity.NetworkUsage
import com.shifthackz.aisdv1.domain.usecase.settings.ObserveNetworkUsageUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ResetNetworkUsageUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.NetworkUsageRouter
import com.shifthackz.aisdv1.presentation.screen.networkusage.model.NetworkUsageIntent
import com.shifthackz.aisdv1.presentation.model.UsageCategory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Verifies that network usage UI state is driven by observed Room-backed traffic counters.
 *
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NetworkUsageViewModelTest {

    private val networkUsage = MutableStateFlow(NetworkUsage())
    private val observeNetworkUsageUseCase = mockk<ObserveNetworkUsageUseCase>()
    private val resetNetworkUsageUseCase = mockk<ResetNetworkUsageUseCase>()
    private val router = TestNetworkUsageRouter()
    private lateinit var dispatchersProvider: DispatchersProvider
    private var viewModelScope: CoroutineScope? = null

    @Before
    fun initialize() {
        every { observeNetworkUsageUseCase() } returns networkUsage
        coEvery { resetNetworkUsageUseCase() } answers {
            networkUsage.value = NetworkUsage()
        }
    }

    @After
    fun tearDown() {
        viewModelScope?.cancel()
        viewModelScope = null
        router.backInvocations = 0
        networkUsage.value = NetworkUsage()
    }

    @Test
    fun `given network usage changes, expected usage items update`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        networkUsage.value = NetworkUsage(
            modelDownloadBytes = 1024L,
            configBytes = 512L,
            inferenceBytes = 2048L,
        )
        advanceUntilIdle()

        val actual = viewModel.state.value.usage
        assertEquals(false, actual.loading)
        assertEquals(3584L, actual.totalBytes)
        assertEquals(
            2048L,
            actual.items.first { it.category == UsageCategory.TRAFFIC_INFERENCE }.bytes,
        )
    }

    @Test
    fun `given selected network category becomes empty, expected total selected`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        networkUsage.value = NetworkUsage(configBytes = 512L)
        advanceUntilIdle()
        viewModel.processIntent(NetworkUsageIntent.SelectCategory(UsageCategory.TRAFFIC_CONFIGS))

        assertEquals(UsageCategory.TRAFFIC_CONFIGS, viewModel.state.value.usage.selectedCategory)

        networkUsage.value = NetworkUsage()
        advanceUntilIdle()

        assertEquals(null, viewModel.state.value.usage.selectedCategory)
        assertEquals(0L, viewModel.state.value.usage.totalBytes)
    }

    @Test
    fun `given reset requested, expected shimmer loading and zero usage`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        networkUsage.value = NetworkUsage(
            modelDownloadBytes = 2048L,
            configBytes = 512L,
            inferenceBytes = 1024L,
        )
        advanceUntilIdle()

        viewModel.processIntent(NetworkUsageIntent.ResetStatistics)

        assertEquals(true, viewModel.state.value.usage.loading)
        advanceUntilIdle()

        assertEquals(false, viewModel.state.value.usage.loading)
        assertEquals(0L, viewModel.state.value.usage.totalBytes)
    }

    @Test
    fun `given back intent, expected router navigates back`() = runTest {
        val viewModel = createViewModel()

        viewModel.processIntent(NetworkUsageIntent.NavigateBack)

        assertEquals(1, router.backInvocations)
    }

    private fun TestScope.createViewModel(): NetworkUsageViewModel {
        val dispatcher = StandardTestDispatcher(testScheduler)
        dispatchersProvider = object : DispatchersProvider {
            override val io: CoroutineDispatcher = dispatcher
            override val ui: CoroutineDispatcher = dispatcher
            override val immediate: CoroutineDispatcher = dispatcher
        }
        viewModelScope = CoroutineScope(SupervisorJob() + dispatcher)
        return NetworkUsageViewModel(
            dispatchersProvider = dispatchersProvider,
            observeNetworkUsageUseCase = observeNetworkUsageUseCase,
            resetNetworkUsageUseCase = resetNetworkUsageUseCase,
            router = router,
        )
    }
}

/**
 * Router spy used to assert standalone screen back navigation.
 *
 * @author Dmitriy Moroz
 */
private class TestNetworkUsageRouter : NetworkUsageRouter {
    var backInvocations = 0

    override fun navigateBack() {
        backInvocations++
    }
}
