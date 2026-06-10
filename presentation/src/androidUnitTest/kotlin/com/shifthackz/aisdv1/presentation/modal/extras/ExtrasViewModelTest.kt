package com.shifthackz.aisdv1.presentation.modal.extras

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.sdhypernet.FetchAndGetHyperNetworksUseCase
import com.shifthackz.aisdv1.domain.usecase.sdlora.FetchAndGetLorasUseCase
import com.shifthackz.aisdv1.presentation.mocks.mockStableDiffusionHyperNetworks
import com.shifthackz.aisdv1.presentation.mocks.mockStableDiffusionLoras
import com.shifthackz.aisdv1.presentation.model.ErrorState
import com.shifthackz.aisdv1.presentation.model.ExtraType
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore("ToDo: Investigate why sometimes tests fail on remote worker due to race-conditions.")
class ExtrasViewModelTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubFetchAndGetLorasUseCase = mockk<FetchAndGetLorasUseCase>()
    private val stubFetchAndGetHyperNetworksUseCase = mockk<FetchAndGetHyperNetworksUseCase>()
    private val stubPreferenceManager = mockk<PreferenceManager>()

    @Before
    fun initialize() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111
    }

    @Test
    fun `given update data, fetch loras successful, expected UI state with loras list`() = runTest {
        mockInitialData()
        val viewModel = createViewModel()
        advanceUntilIdle()

        val expected = ExtrasState(
            loading = false,
            error = ErrorState.None,
            prompt = "prompt <lora:alias_5598:1>",
            negativePrompt = "negative",
            type = ExtraType.Lora,
            loras = listOf(
                ExtraItemUi(
                    type = ExtraType.Lora,
                    key = "name_5598_Lora_0",
                    name = "name_5598",
                    alias = "alias_5598",
                    isApplied = true,
                    value = "1",
                ),
                ExtraItemUi(
                    type = ExtraType.Lora,
                    key = "name_151297_Lora_1",
                    name = "name_151297",
                    alias = "alias_151297",
                    isApplied = false,
                    value = null,
                ),
            ),
        )
        val actual = viewModel.state.value
        Assert.assertEquals(expected.type, actual.type)
        Assert.assertEquals(expected.error, actual.error)
        Assert.assertEquals(expected.prompt, actual.prompt)
        Assert.assertEquals(expected.negativePrompt, actual.negativePrompt)
        Assert.assertEquals(expected.type, actual.type)
        Assert.assertEquals(expected.loras, actual.loras)
    }

    @Test
    fun `given update data, fetch loras failed, expected UI state with Generic error`() = runTest {
        coEvery {
            stubFetchAndGetLorasUseCase()
        } throws stubException

        val viewModel = createViewModel()
        advanceUntilIdle()

        Assert.assertEquals(false, viewModel.state.value.loading)
        Assert.assertEquals(ErrorState.Generic, viewModel.state.value.error)
    }

    @Test
    fun `given received ApplyPrompts intent, expected ApplyPrompts effect delivered to effect collector`() = runTest {
        mockInitialData()
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processIntent(ExtrasIntent.ApplyPrompts)
        advanceUntilIdle()

        val expected = ExtrasEffect.ApplyPrompts(
            prompt = "prompt <lora:alias_5598:1>",
            negativePrompt = "negative",
        )
        val actual = viewModel.effect.firstOrNull()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received Close intent, expected Close effect delivered to effect collector`() = runTest {
        mockInitialData()
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processIntent(ExtrasIntent.Close)
        advanceUntilIdle()

        val expected = ExtrasEffect.Close
        val actual = viewModel.effect.firstOrNull()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received ToggleItem intent, expected prompt updated in UI state`() = runTest {
        mockInitialData()
        val viewModel = createViewModel()
        advanceUntilIdle()

        val item = viewModel.state.value.loras.first()
        viewModel.processIntent(ExtrasIntent.ToggleItem(item))
        advanceUntilIdle()

        val state = viewModel.state.value
        Assert.assertEquals("prompt", state.prompt)
    }

    private fun mockInitialData() {
        coEvery {
            stubFetchAndGetLorasUseCase()
        } returns mockStableDiffusionLoras

        coEvery {
            stubFetchAndGetHyperNetworksUseCase()
        } returns mockStableDiffusionHyperNetworks
    }

    private fun TestScope.createViewModel(): ExtrasViewModel {
        val dispatcher = StandardTestDispatcher(testScheduler)
        return ExtrasViewModel(
            dispatchersProvider = testDispatchersProvider(dispatcher),
            fetchAndGetLorasUseCase = stubFetchAndGetLorasUseCase,
            fetchAndGetHyperNetworksUseCase = stubFetchAndGetHyperNetworksUseCase,
            preferenceManager = stubPreferenceManager,
            prompt = "prompt <lora:alias_5598:1>",
            negativePrompt = "negative",
            type = ExtraType.Lora,
        )
    }

    private fun testDispatchersProvider(dispatcher: CoroutineDispatcher) = object : DispatchersProvider {
        override val io: CoroutineDispatcher = dispatcher
        override val ui: CoroutineDispatcher = dispatcher
        override val immediate: CoroutineDispatcher = dispatcher
    }
}
