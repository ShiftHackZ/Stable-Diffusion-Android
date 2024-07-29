@file:OptIn(ExperimentalCoroutinesApi::class)

package com.shifthackz.aisdv1.presentation.screen.home

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.core.CoreViewModelInitializeStrategy
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before

class HomeNavigationViewModelTest : CoreViewModelTest<HomeNavigationViewModel>() {

    private val stubRoute = BehaviorSubject.create<AiGenerationResult.Type>()
    private val stubGenerationFormUpdateEvent = mockk<GenerationFormUpdateEvent>()

    override val testDispatcher: CoroutineDispatcher
        get() = UnconfinedTestDispatcher()

    override val testViewModelStrategy = CoreViewModelInitializeStrategy.InitializeEveryTime

    override fun initializeViewModel() = HomeNavigationViewModel(
        generationFormUpdateEvent = stubGenerationFormUpdateEvent,
        schedulersProvider = stubSchedulersProvider,
    )

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubGenerationFormUpdateEvent.observeRoute()
        } returns stubRoute.toFlowable(BackpressureStrategy.LATEST)
    }

//    @Test
//    fun `given generation form event is IMAGE_TO_IMAGE, expected HomeNavigationEffect with route ROUTE_IMG_TO_IMG delivered to effect collector`() {
//        stubRoute.onNext(AiGenerationResult.Type.IMAGE_TO_IMAGE)
//        runTest {
//            val actual = HomeNavigationEffect(Constants.ROUTE_IMG_TO_IMG)
//            val expected = viewModel.effect.firstOrNull()
//            Assert.assertEquals(expected, actual)
//        }
//    }

//    @Test
//    fun `given generation form event is TEXT_TO_IMAGE, expected HomeNavigationEffect with route ROUTE_TXT_TO_IMG delivered to effect collector`() {
//        stubRoute.onNext(AiGenerationResult.Type.TEXT_TO_IMAGE)
//        runTest {
//            val actual = HomeNavigationEffect(Constants.ROUTE_TXT_TO_IMG)
//            val expected = viewModel.effect.firstOrNull()
//            Assert.assertEquals(expected, actual)
//        }
//    }
}