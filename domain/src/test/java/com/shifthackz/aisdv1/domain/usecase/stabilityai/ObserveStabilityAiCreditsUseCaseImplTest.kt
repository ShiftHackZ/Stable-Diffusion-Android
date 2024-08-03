package com.shifthackz.aisdv1.domain.usecase.stabilityai

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.mocks.mockSettings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StabilityAiCreditsRepository
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test

class ObserveStabilityAiCreditsUseCaseImplTest {

    private val stubSettingsObserver = BehaviorSubject.create<Settings>()
    private val stubCreditsObserver = BehaviorSubject.create<Result<Float>>()

    private val stubStabilityAiCreditsRepository = mock<StabilityAiCreditsRepository>()

    private val stubPreferenceManager = mock<PreferenceManager>()

    private val useCase = ObserveStabilityAiCreditsUseCaseImpl(
        repository = stubStabilityAiCreditsRepository,
        preferenceManager = stubPreferenceManager,
    )

    @Before
    fun initialize() {
        whenever(stubPreferenceManager.observe())
            .thenReturn(stubSettingsObserver.toFlowable(BackpressureStrategy.LATEST))

        whenever(stubStabilityAiCreditsRepository.fetchAndObserve())
            .thenReturn(
                stubCreditsObserver
                    .toFlowable(BackpressureStrategy.LATEST)
                    .flatMap { result ->
                        result.fold(
                            onSuccess = { credits -> Flowable.just(credits) },
                            onFailure = { t -> Flowable.error(t) },
                        )
                    }
            )
    }

    @Test
    fun `given successfully got settings and credits, expected valid credits value`() {
        val stubObserver = useCase().test()

        stubCreditsObserver.onNext(Result.success(5598f))
        stubSettingsObserver.onNext(mockSettings)

        stubObserver
            .assertValueAt(0, 5598f)
            .assertNoErrors()
    }

    @Test
    fun `given successfully got settings and credits, then change settings, expected credits value not changed`() {
        val stubObserver = useCase().test()

        stubCreditsObserver.onNext(Result.success(5598f))
        stubSettingsObserver.onNext(mockSettings)

        stubObserver
            .assertValueAt(0, 5598f)
            .assertNoErrors()

        stubSettingsObserver.onNext(mockSettings.copy(formPromptTaggedInput = false))

        stubObserver
            .assertValueAt(1, 5598f)
            .assertNoErrors()
    }

    @Test
    fun `given successfully got settings and credits, then credits changed, expected credits value changed`() {
        val stubObserver = useCase().test()

        stubCreditsObserver.onNext(Result.success(5598f))
        stubSettingsObserver.onNext(mockSettings)

        stubObserver
            .assertValueAt(0, 5598f)
            .assertNoErrors()

        stubCreditsObserver.onNext(Result.success(2211f))

        stubObserver
            .assertValueAt(1, 2211f)
            .assertNoErrors()
    }

    @Test
    fun `given exception from credits repository, expected zero credits value`() {
        val stubObserver = useCase().test()
        val stubException = Throwable("Wrong server source selected.")

        stubCreditsObserver.onNext(Result.failure(stubException))
        stubSettingsObserver.onNext(mockSettings)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, 0f)
            .await()
            .assertComplete()
    }
}
