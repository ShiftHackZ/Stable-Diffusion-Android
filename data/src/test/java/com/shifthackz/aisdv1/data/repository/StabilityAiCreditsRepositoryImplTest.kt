package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsDataSource
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.observers.TestObserver
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Test

class StabilityAiCreditsRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubCredits = BehaviorSubject.create<Float>()
    private val stubRemoteDataSource = mockk<StabilityAiCreditsDataSource.Remote>()
    private val stubLocalDataSource = mockk<StabilityAiCreditsDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()

    private val repository = StabilityAiCreditsRepositoryImpl(
        remoteDataSource = stubRemoteDataSource,
        localDataSource = stubLocalDataSource,
        preferenceManager = stubPreferenceManager,
    )

    @Before
    fun initialize() {
        every {
            stubLocalDataSource.observe()
        } returns stubCredits.toFlowable(BackpressureStrategy.LATEST)
    }

    @Test
    fun `given server source is not STABILITY_AI, attempt to fetch, expected IllegalStateException error value`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.LOCAL

        every {
            stubRemoteDataSource.fetch()
        } returns Single.just(5598f)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        repository
            .fetch()
            .test()
            .assertWrongServerSourceSelected()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given server source is not STABILITY_AI, attempt to fetch and get, expected IllegalStateException error value`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.LOCAL

        every {
            stubRemoteDataSource.fetch()
        } returns Single.just(5598f)

        every {
            stubLocalDataSource.get()
        } returns Single.just(5598f)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        repository
            .fetchAndGet()
            .test()
            .assertWrongServerSourceSelected()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given server source is not STABILITY_AI, attempt to fetch and observe, expected IllegalStateException error value`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.LOCAL

        every {
            stubRemoteDataSource.fetch()
        } returns Single.just(5598f)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        repository
            .fetchAndObserve()
            .test()
            .assertWrongServerSourceSelected()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given server source is not STABILITY_AI, attempt to get, expected IllegalStateException error value`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.LOCAL

        every {
            stubLocalDataSource.get()
        } returns Single.just(5598f)

        repository
            .get()
            .test()
            .assertWrongServerSourceSelected()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given server source is not STABILITY_AI, attempt to observe, expected IllegalStateException error value`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.LOCAL

        repository
            .observe()
            .test()
            .assertWrongServerSourceSelected()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to fetch, remote returns data, local save success, expected complete value`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.STABILITY_AI

        every {
            stubRemoteDataSource.fetch()
        } returns Single.just(5598f)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        repository
            .fetch()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to fetch, remote returns error, local save success, expected error value`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.STABILITY_AI

        every {
            stubRemoteDataSource.fetch()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        repository
            .fetch()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to fetch, remote returns data, local save fails, expected error value`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.STABILITY_AI

        every {
            stubRemoteDataSource.fetch()
        } returns Single.just(5598f)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.error(stubException)

        repository
            .fetch()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to fetch and get, fetch success, get success, expected valid credits value`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.STABILITY_AI

        every {
            stubRemoteDataSource.fetch()
        } returns Single.just(5598f)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.get()
        } returns Single.just(5598f)

        repository
            .fetchAndGet()
            .test()
            .assertNoErrors()
            .assertValue(5598f)
            .await()
            .assertComplete()
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to fetch and get, fetch fails, get success, expected valid credits value`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.STABILITY_AI

        every {
            stubRemoteDataSource.fetch()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.get()
        } returns Single.just(5598f)

        repository
            .fetchAndGet()
            .test()
            .assertNoErrors()
            .assertValue(5598f)
            .await()
            .assertComplete()
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to fetch and get, fetch fails, get fails, expected error value`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.STABILITY_AI

        every {
            stubRemoteDataSource.fetch()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.get()
        } returns Single.error(stubException)

        repository
            .fetchAndGet()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to fetch and observe, fetch success, expected valid credits value`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.STABILITY_AI

        every {
            stubRemoteDataSource.fetch()
        } returns Single.just(5598f)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        val stubObserver = repository
            .fetchAndObserve()
            .test()

        stubCredits.onNext(5598f)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, 5598f)
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to fetch and observe, fetch fails, expected valid credits value`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.STABILITY_AI

        every {
            stubRemoteDataSource.fetch()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        val stubObserver = repository
            .fetchAndObserve()
            .test()

        stubCredits.onNext(0f)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, 0f)
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to get, local returns data, expected valid credits value`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.STABILITY_AI

        every {
            stubLocalDataSource.get()
        } returns Single.just(5598f)

        repository
            .get()
            .test()
            .assertNoErrors()
            .assertValue(5598f)
            .await()
            .assertComplete()
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to get, local throws exception, expected error value`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.STABILITY_AI

        every {
            stubLocalDataSource.get()
        } returns Single.error(stubException)

        repository
            .get()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to observe, local emits two values, expected valid credits values in same order`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.STABILITY_AI

        val stubObserver = repository
            .observe()
            .test()

        stubCredits.onNext(0f)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, 0f)

        stubCredits.onNext(5598f)

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, 5598f)
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to observe, local throws exception, expected error value`() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.STABILITY_AI

        every {
            stubLocalDataSource.observe()
        } returns Flowable.error(stubException)

        repository
            .observe()
            .test()
            .assertNoValues()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    private fun <T : Any> TestObserver<T>.assertWrongServerSourceSelected() = this
        .assertError { t -> t.wrongSourceErrorPredicate() }

    private fun <T : Any> TestSubscriber<T>.assertWrongServerSourceSelected() = this
        .assertError { t -> t.wrongSourceErrorPredicate() }

    private fun Throwable.wrongSourceErrorPredicate(): Boolean {
        return this is IllegalStateException && message == "Wrong server source selected."
    }
}
