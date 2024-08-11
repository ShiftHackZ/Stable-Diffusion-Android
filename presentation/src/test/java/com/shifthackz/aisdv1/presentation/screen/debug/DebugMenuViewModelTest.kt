package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.debug.DebugInsertBadBase64UseCase
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import org.junit.Before
import org.junit.Test

class DebugMenuViewModelTest : CoreViewModelTest<DebugMenuViewModel>() {

    private val stubFileProviderDescriptor = mockk<FileProviderDescriptor>()
    private val stubDebugInsertBadBase64UseCase = mockk<DebugInsertBadBase64UseCase>()
    private val stubMainRouter = mockk<MainRouter>()
    private val stubPreferenceManager = mockk<PreferenceManager>()

    override fun initializeViewModel() = DebugMenuViewModel(
        preferenceManager = stubPreferenceManager,
        fileProviderDescriptor = stubFileProviderDescriptor,
        debugInsertBadBase64UseCase = stubDebugInsertBadBase64UseCase,
        schedulersProvider = stubSchedulersProvider,
        mainRouter = stubMainRouter,
    )

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubPreferenceManager.observe()
        } returns Flowable.just(Settings())
    }

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
