package com.shifthackz.aisdv1.presentation.modal.extras

import com.shifthackz.aisdv1.core.common.time.TimeProvider
import com.shifthackz.aisdv1.domain.usecase.sdhypernet.FetchAndGetHyperNetworksUseCase
import com.shifthackz.aisdv1.domain.usecase.sdlora.FetchAndGetLorasUseCase
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.mocks.mockStableDiffusionLoras
import com.shifthackz.aisdv1.presentation.model.ErrorState
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ExtrasViewModelTest : CoreViewModelTest<ExtrasViewModel>() {

    private val stubException = Throwable("Something went wrong.")
    private val stubFetchAndGetLorasUseCase = mockk<FetchAndGetLorasUseCase>()
    private val stubFetchAndGetHyperNetworksUseCase = mockk<FetchAndGetHyperNetworksUseCase>()
    private val stubTimeProvider = mockk<TimeProvider>()

    override fun initializeViewModel() = ExtrasViewModel(
        stubFetchAndGetLorasUseCase,
        stubFetchAndGetHyperNetworksUseCase,
        stubSchedulersProvider,
        stubTimeProvider,
    )

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubTimeProvider.nanoTime()
        } returns MOCK_SYS_TIME
    }

    @Test
    fun `given update data, fetch loras successful, expected UI state with loras list`() {
        every {
            stubFetchAndGetLorasUseCase()
        } returns Single.just(mockStableDiffusionLoras)

        viewModel.updateData(
            prompt = "prompt <lora:alias_5598:1>",
            negativePrompt = "negative",
            type = ExtraType.Lora,
        )

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
//            assertEquals(expected.type, actual.type)
//            assertEquals(expected.error, actual.error)
//            assertEquals(expected.prompt, actual.prompt)
//            assertEquals(expected.negativePrompt, actual.negativePrompt)
//            assertEquals(expected.type, actual.type)
            assert(actual.loras.any { it.name == "name_5598" && it.isApplied })
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

    companion object {
        private const val MOCK_SYS_TIME = 5598L
    }
}
