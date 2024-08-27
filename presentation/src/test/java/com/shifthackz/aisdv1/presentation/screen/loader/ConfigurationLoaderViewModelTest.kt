package com.shifthackz.aisdv1.presentation.screen.loader

import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.stub.stubDispatchersProvider
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class ConfigurationLoaderViewModelTest : CoreViewModelTest<ConfigurationLoaderViewModel>() {

    private val stubException = Throwable("Something went wrong.")
    private val stubDataPreLoaderUseCase = mockk<DataPreLoaderUseCase>()
    private val stubMainRouter = mockk<MainRouter>()

    override fun initializeViewModel() = ConfigurationLoaderViewModel(
        dispatchersProvider = stubDispatchersProvider,
        dataPreLoaderUseCase = stubDataPreLoaderUseCase,
        schedulersProvider = stubSchedulersProvider,
        mainRouter = stubMainRouter,
    )

    @Test
    fun `given initialized, data loaded successfully, expected UI state is StatusNotification, router navigateToHomeScreen() method called`() {
        every {
            stubMainRouter.navigateToHomeScreen()
        } returns Unit

        every {
            stubDataPreLoaderUseCase()
        } returns Completable.complete()

        runTest {
            val expected = true
            val actual = viewModel.state.value is ConfigurationLoaderState.StatusNotification
            Assert.assertEquals(expected, actual)
        }

        verify {
            stubMainRouter.navigateToHomeScreen()
        }
    }

    @Test
    fun `given initialized, data not loaded, expected UI state is StatusNotification, router navigateToHomeScreen() method called`() {
        every {
            stubMainRouter.navigateToHomeScreen()
        } returns Unit

        every {
            stubDataPreLoaderUseCase()
        } returns Completable.error(stubException)

        runTest {
            val expected = true
            val actual = viewModel.state.value is ConfigurationLoaderState.StatusNotification
            Assert.assertEquals(expected, actual)
        }

        verify {
            stubMainRouter.navigateToHomeScreen()
        }
    }
}
