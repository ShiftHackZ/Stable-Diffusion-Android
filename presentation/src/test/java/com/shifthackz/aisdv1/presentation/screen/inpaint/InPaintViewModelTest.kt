package com.shifthackz.aisdv1.presentation.screen.inpaint

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Path
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.model.InPaintModel
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.stub.stubDispatchersProvider
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class InPaintViewModelTest : CoreViewModelTest<InPaintViewModel>() {

    private val stubBitmap = mockk<Bitmap>()
    private val stubPath = mockk<Path>()
    private val stubInPainSubject = BehaviorSubject.create<InPaintModel>()
    private val stubBitmapSubject = BehaviorSubject.create<Bitmap>()
    private val stubInPaintStateProducer = mockk<InPaintStateProducer>()
    private val stubMainRouter = mockk<MainRouter>()

    override fun initializeViewModel() = InPaintViewModel(
        dispatchersProvider = stubDispatchersProvider,
        schedulersProvider = stubSchedulersProvider,
        stateProducer = stubInPaintStateProducer,
        mainRouter = stubMainRouter,
    )

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubInPaintStateProducer.observeInPaint()
        } returns stubInPainSubject.toFlowable(BackpressureStrategy.LATEST)

        every {
            stubInPaintStateProducer.observeBitmap()
        } returns stubBitmapSubject.toFlowable(BackpressureStrategy.LATEST)
    }

    @Test
    fun `initialized, expected UI state updated with correct stub values`() {
        stubBitmapSubject.onNext(stubBitmap)
        stubInPainSubject.onNext(InPaintModel())
        runTest {
            val state = viewModel.state.value
            Assert.assertEquals(InPaintModel(), state.model)
            Assert.assertEquals(stubBitmap, state.bitmap)
        }
    }

    @Test
    fun `given received DrawPath intent, expected last path in UI state added from intent`() {
        viewModel.processIntent(InPaintIntent.DrawPath(stubPath))
        runTest {
            val expected = stubPath to 16
            val actual = viewModel.state.value.model.paths.last()
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received DrawPathBmp intent, expected model bitmap updated in UI state from intent`() {
        viewModel.processIntent(InPaintIntent.DrawPathBmp(stubBitmap))
        runTest {
            val expected = stubBitmap
            val actual = viewModel.state.value.model.bitmap
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received NavigateBack intent, expected state producer updateInPaint(), router navigateBack() methods called`() {
        every {
            stubInPaintStateProducer.updateInPaint(any())
        } returns Unit

        every {
            stubMainRouter.navigateBack()
        } returns Unit

        viewModel.processIntent(InPaintIntent.NavigateBack)

        verify {
            stubInPaintStateProducer.updateInPaint(viewModel.state.value.model)
        }
        verify {
            stubMainRouter.navigateBack()
        }
    }

    @Test
    fun `given received SelectTab intent, expected selectedTab field updated in UI state`() {
        viewModel.processIntent(InPaintIntent.SelectTab(InPaintState.Tab.FORM))
        runTest {
            val expected = InPaintState.Tab.FORM
            val actual = viewModel.state.value.selectedTab
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received ChangeCapSize intent, expected size field updated in UI state`() {
        viewModel.processIntent(InPaintIntent.ChangeCapSize(5598))
        runTest {
            val expected = 5598
            val actual = viewModel.state.value.size
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Action Undo intent, expected last path in UI state removed`() {
        viewModel.processIntent(InPaintIntent.Action.Undo)
        runTest {
            val expected = emptyList<Pair<Path, Int>>()
            val actual = viewModel.state.value.model.paths
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received ScreenModal Dismiss intent, expected screenModal in UI state is None`() {
        viewModel.processIntent(InPaintIntent.ScreenModal.Dismiss)
        runTest {
            val expected = Modal.None
            val actual = viewModel.state.value.screenModal
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received ScreenModal Show intent, expected screenModal in UI state is updated`() {
        viewModel.processIntent(InPaintIntent.ScreenModal.Show(Modal.Language))
        runTest {
            val expected = Modal.Language
            val actual = viewModel.state.value.screenModal
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Action Clear intent, expected screenModal is None, paths is empty in UI state`() {
        viewModel.processIntent(InPaintIntent.Action.Clear)
        runTest {
            val state = viewModel.state.value
            Assert.assertEquals(Modal.None, state.screenModal)
            Assert.assertEquals(emptyList<Pair<Path, Int>>(), state.model.paths)
        }
    }

    @Test
    fun `given received Update MaskBlur intent, expected maskBlur is updated in UI state`() {
        viewModel.processIntent(InPaintIntent.Update.MaskBlur(5598))
        runTest {
            val expected = 5598
            val actual = viewModel.state.value.model.maskBlur
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update OnlyMaskedPadding intent, expected onlyMaskedPaddingPx is updated in UI state`() {
        viewModel.processIntent(InPaintIntent.Update.OnlyMaskedPadding(5598))
        runTest {
            val expected = 5598
            val actual = viewModel.state.value.model.onlyMaskedPaddingPx
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update Area intent, expected inPaintArea is updated in UI state`() {
        viewModel.processIntent(InPaintIntent.Update.Area(InPaintModel.Area.WholePicture))
        runTest {
            val expected = InPaintModel.Area.WholePicture
            val actual = viewModel.state.value.model.inPaintArea
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update MaskContent intent, expected maskContent is updated in UI state`() {
        viewModel.processIntent(InPaintIntent.Update.MaskContent(InPaintModel.MaskContent.Fill))
        runTest {
            val expected = InPaintModel.MaskContent.Fill
            val actual = viewModel.state.value.model.maskContent
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update MaskMode intent, expected maskMode is updated in UI state`() {
        viewModel.processIntent(InPaintIntent.Update.MaskMode(InPaintModel.MaskMode.InPaintNotMasked))
        runTest {
            val expected = InPaintModel.MaskMode.InPaintNotMasked
            val actual = viewModel.state.value.model.maskMode
            Assert.assertEquals(expected, actual)
        }
    }
}
