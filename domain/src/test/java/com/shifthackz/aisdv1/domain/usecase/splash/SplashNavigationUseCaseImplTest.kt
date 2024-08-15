package com.shifthackz.aisdv1.domain.usecase.splash

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import org.junit.Test

class SplashNavigationUseCaseImplTest {

    private val stubPreferenceManager = mock<PreferenceManager>()

    private val useCase = SplashNavigationUseCaseImpl(stubPreferenceManager)

    @Test
    fun `given onBoardingComplete is false, expected LAUNCH_ONBOARDING`() {
        whenever(stubPreferenceManager.onBoardingComplete)
            .thenReturn(false)

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(SplashNavigationUseCase.Action.LAUNCH_ONBOARDING)
    }

    @Test
    fun `given forceSetupAfterUpdate is true, expected LAUNCH_SERVER_SETUP`() {
        whenever(stubPreferenceManager.onBoardingComplete)
            .thenReturn(true)

        whenever(stubPreferenceManager.forceSetupAfterUpdate)
            .thenReturn(true)

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(SplashNavigationUseCase.Action.LAUNCH_SERVER_SETUP)
    }

    @Test
    fun `given source is AUTOMATIC1111 and server url empty, expected LAUNCH_SERVER_SETUP`() {
        whenever(stubPreferenceManager.onBoardingComplete)
            .thenReturn(true)

        whenever(stubPreferenceManager.forceSetupAfterUpdate)
            .thenReturn(false)

        whenever(stubPreferenceManager.automatic1111ServerUrl)
            .thenReturn("")

        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.AUTOMATIC1111)

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(SplashNavigationUseCase.Action.LAUNCH_SERVER_SETUP)
    }

    @Test
    fun `given source is AUTOMATIC1111 and server url not empty, expected LAUNCH_HOME`() {
        whenever(stubPreferenceManager.onBoardingComplete)
            .thenReturn(true)

        whenever(stubPreferenceManager.forceSetupAfterUpdate)
            .thenReturn(false)

        whenever(stubPreferenceManager.automatic1111ServerUrl)
            .thenReturn("http://192.168.0.1:7860")

        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.AUTOMATIC1111)

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(SplashNavigationUseCase.Action.LAUNCH_HOME)
    }

    @Test
    fun `given source is LOCAL, and server url is empty, expected LAUNCH_HOME`() {
        whenever(stubPreferenceManager.onBoardingComplete)
            .thenReturn(true)

        whenever(stubPreferenceManager.forceSetupAfterUpdate)
            .thenReturn(false)

        whenever(stubPreferenceManager.automatic1111ServerUrl)
            .thenReturn("")

        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.LOCAL)

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(SplashNavigationUseCase.Action.LAUNCH_HOME)
    }

    @Test
    fun `given source is LOCAL, and server url is not empty, expected LAUNCH_HOME`() {
        whenever(stubPreferenceManager.onBoardingComplete)
            .thenReturn(true)

        whenever(stubPreferenceManager.forceSetupAfterUpdate)
            .thenReturn(false)

        whenever(stubPreferenceManager.automatic1111ServerUrl)
            .thenReturn("http://192.168.0.1:7860")

        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.LOCAL)

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(SplashNavigationUseCase.Action.LAUNCH_HOME)
    }
}
