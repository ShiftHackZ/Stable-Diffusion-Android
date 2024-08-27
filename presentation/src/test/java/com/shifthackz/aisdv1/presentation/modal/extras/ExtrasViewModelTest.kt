package com.shifthackz.aisdv1.presentation.modal.extras

import com.shifthackz.aisdv1.core.common.time.TimeProvider
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.sdhypernet.FetchAndGetHyperNetworksUseCase
import com.shifthackz.aisdv1.domain.usecase.sdlora.FetchAndGetLorasUseCase
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.mocks.mockStableDiffusionLoras
import com.shifthackz.aisdv1.presentation.model.ErrorState
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.stub.stubDispatchersProvider
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ExtrasViewModelTest : CoreViewModelTest<ExtrasViewModel>() {

    private val stubException = Throwable("Something went wrong.")
    private val stubFetchAndGetLorasUseCase = mockk<FetchAndGetLorasUseCase>()
    private val stubFetchAndGetHyperNetworksUseCase = mockk<FetchAndGetHyperNetworksUseCase>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubTimeProvider = mockk<TimeProvider>()

    override fun initializeViewModel() = ExtrasViewModel(
        dispatchersProvider = stubDispatchersProvider,
        fetchAndGetLorasUseCase = stubFetchAndGetLorasUseCase,
        fetchAndGetHyperNetworksUseCase = stubFetchAndGetHyperNetworksUseCase,
        schedulersProvider = stubSchedulersProvider,
        preferenceManager = stubPreferenceManager,
        timeProvider = stubTimeProvider,
    )

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        every {
            stubTimeProvider.nanoTime()
        } returns MOCK_SYS_TIME
    }

    @Test
    fun `given update data, fetch loras successful, expected UI state with loras list`() {
        mockInitialData()
        runTest {
            val expected = ExtrasState(
                loading = false,
                error = ErrorState.None,
                prompt = "prompt <lora:alias_5598:1>",
                negativePrompt = "negative",
                type = ExtraType.Lora,
                loras = listOf(
                    ExtraItemUi(
                        type = ExtraType.Lora,
                        key = "name_5598_lora_$MOCK_SYS_TIME",
                        name = "name_5598",
                        alias = "alias_5598",
                        isApplied = true,
                        value = "1",
                    ),
                    ExtraItemUi(
                        type = ExtraType.Lora,
                        key = "name_151297_lora_$MOCK_SYS_TIME",
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
            Assert.assertEquals(true, actual.loras.any { it.name == "name_5598" && it.isApplied })
        }
    }

    @Test
    fun `given update data, fetch loras failed, expected UI state with Generic error`() {
        every {
            stubFetchAndGetLorasUseCase()
        } returns Single.error(stubException)

        viewModel.updateData(
            prompt = "prompt <lora:alias_5598:1>",
            negativePrompt = "negative",
            type = ExtraType.Lora,
        )

        runTest {
            val state = viewModel.state.value
            Assert.assertEquals(false, state.loading)
            Assert.assertEquals(ErrorState.Generic, state.error)
        }
    }

    @Test
    fun `given received ApplyPrompts intent, expected ApplyPrompts effect delivered to effect collector`() {
        mockInitialData()
        viewModel.processIntent(ExtrasIntent.ApplyPrompts)
        runTest {
            val expected = ExtrasEffect.ApplyPrompts(
                prompt = "prompt <lora:alias_5598:1>",
                negativePrompt = "negative",
            )
            val actual = viewModel.effect.firstOrNull()
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Close intent, expected Close effect delivered to effect collector`() {
        viewModel.processIntent(ExtrasIntent.Close)
        runTest {
            val expected = ExtrasEffect.Close
            val actual = viewModel.effect.firstOrNull()
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received ToggleItem intent, expected prompt updated in UI state`() {
        mockInitialData()
        Thread.sleep(1000L)
        val item = ExtraItemUi(
            type = ExtraType.Lora,
            key = "name_5598_lora_$MOCK_SYS_TIME",
            name = "name_5598",
            alias = "alias_5598",
            isApplied = true,
            value = "1",
        )
        viewModel.processIntent(ExtrasIntent.ToggleItem(item))
        runTest {
            val state = viewModel.state.value
            Assert.assertEquals("prompt", state.prompt)
        }
    }

    private fun mockInitialData() {
        every {
            stubFetchAndGetLorasUseCase()
        } returns Single.just(mockStableDiffusionLoras)

        viewModel.updateData(
            prompt = "prompt <lora:alias_5598:1>",
            negativePrompt = "negative",
            type = ExtraType.Lora,
        )
    }

    companion object {
        private const val MOCK_SYS_TIME = 5598L
    }
}
