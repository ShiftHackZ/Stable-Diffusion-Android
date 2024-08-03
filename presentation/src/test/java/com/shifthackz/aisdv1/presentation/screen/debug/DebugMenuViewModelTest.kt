package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.domain.usecase.debug.DebugInsertBadBase64UseCase
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import org.junit.Test

class DebugMenuViewModelTest : CoreViewModelTest<DebugMenuViewModel>() {

    private val stubDebugInsertBadBase64UseCase = mockk<DebugInsertBadBase64UseCase>()
    private val stubMainRouter = mockk<MainRouter>()

    override fun initializeViewModel() = DebugMenuViewModel(
        debugInsertBadBase64UseCase = stubDebugInsertBadBase64UseCase,
        schedulersProvider = stubSchedulersProvider,
        mainRouter = stubMainRouter,
    )

    @Test
    fun `given received NavigateBack intent, expected router navigateBack() method called`() {
        every {
            stubMainRouter.navigateBack()
        } returns Unit

        viewModel.processIntent(DebugMenuIntent.NavigateBack)

        verify {
            stubMainRouter.navigateBack()
        }
    }

    @Test
    fun `given received InsertBadBase64 intent, expected debugInsertBadBase64UseCase() method called`() {
        every {
            stubDebugInsertBadBase64UseCase()
        } returns Completable.complete()

        viewModel.processIntent(DebugMenuIntent.InsertBadBase64)

        verify {
            stubDebugInsertBadBase64UseCase()
        }
    }
}
