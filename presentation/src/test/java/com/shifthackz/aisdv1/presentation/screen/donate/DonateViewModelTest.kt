package com.shifthackz.aisdv1.presentation.screen.donate

import com.shifthackz.aisdv1.domain.usecase.donate.FetchAndGetSupportersUseCase
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.mocks.mockSupporters
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class DonateViewModelTest : CoreViewModelTest<DonateViewModel>() {

    private val stubException = Throwable("Something went wrong.")
    private val stubFetchAndGetSupportersUseCase = mockk<FetchAndGetSupportersUseCase>()
    private val stubMainRouter = mockk<MainRouter>()

    override fun initializeViewModel() = DonateViewModel(
        fetchAndGetSupportersUseCase = stubFetchAndGetSupportersUseCase,
        schedulersProvider = stubSchedulersProvider,
        mainRouter = stubMainRouter,
    )

    @Test
    fun `initialized, fetch successful, expected UI state with loading false and non empty supporters`() {
        every {
            stubFetchAndGetSupportersUseCase()
        } returns Single.just(mockSupporters)

        runTest {
            val expected = DonateState(
                loading = false,
                supporters = mockSupporters,
            )
            val actual = viewModel.state.value
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `initialized, fetch failed, expected UI state with loading false and empty supporters`() {
        every {
            stubFetchAndGetSupportersUseCase()
        } returns Single.error(stubException)

        runTest {
            val expected = DonateState(
                loading = false,
                supporters = emptyList(),
            )
            val actual = viewModel.state.value
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received NavigateBack intent, expected router navigateBack() called`() {
        every {
            stubFetchAndGetSupportersUseCase()
        } returns Single.just(mockSupporters)

        every {
            stubMainRouter.navigateBack()
        } returns Unit

        viewModel.processIntent(DonateIntent.NavigateBack)

        verify {
            stubMainRouter.navigateBack()
        }
    }

    @Test
    fun `given received LaunchUrl intent, expected OpenUrl effect delivered to effect collector`() {
        every {
            stubFetchAndGetSupportersUseCase()
        } returns Single.just(mockSupporters)

        val intent = mockk<DonateIntent.LaunchUrl.DonateBmc>()
        every {
            intent::url.get()
        } returns "https://5598.is.my.favourite.com"

        viewModel.processIntent(intent)

        runTest {
            val expected = DonateEffect.OpenUrl("https://5598.is.my.favourite.com")
            val actual = viewModel.effect.firstOrNull()
            Assert.assertEquals(expected, actual)
        }
    }
}
