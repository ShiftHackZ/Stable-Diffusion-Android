package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockLocalAiModel
import com.shifthackz.aisdv1.data.mocks.mockLocalAiModels
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test
import java.io.File

class DownloadableModelRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubFile = mockk<File>()
    private val stubLocalModels = BehaviorSubject.create<List<LocalAiModel>>()
    private val stubDownloadState = BehaviorSubject.create<DownloadState>()
    private val stubRemoteDataSource = mockk<DownloadableModelDataSource.Remote>()
    private val stubLocalDataSource = mockk<DownloadableModelDataSource.Local>()

    private val repository = DownloadableModelRepositoryImpl(
        remoteDataSource = stubRemoteDataSource,
        localDataSource = stubLocalDataSource,
    )

    @Before
    fun initialize() {
        every {
            stubLocalDataSource.observeAll()
        } returns stubLocalModels.toFlowable(BackpressureStrategy.LATEST)

        every {
            stubRemoteDataSource.download(any(), any())
        } returns stubDownloadState
    }

    @Test
    fun `given attempt to check if model downloaded, local data source returns true, expected true value`() {
        every {
            stubLocalDataSource.isDownloaded(any())
        } returns Single.just(true)

        repository
            .isModelDownloaded("5598")
            .test()
            .assertNoErrors()
            .assertValue(true)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to check if model downloaded, local data source returns false, expected false value`() {
        every {
            stubLocalDataSource.isDownloaded(any())
        } returns Single.just(false)

        repository
            .isModelDownloaded("5598")
            .test()
            .assertNoErrors()
            .assertValue(false)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to check if model downloaded, local data source throws exception, expected error value`() {
        every {
            stubLocalDataSource.isDownloaded(any())
        } returns Single.error(stubException)

        repository
            .isModelDownloaded("5598")
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to delete model, local data source completes, expected complete value`() {
        every {
            stubLocalDataSource.delete(any())
        } returns Completable.complete()

        repository
            .delete("5598")
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to delete model, local data source throws exception, expected error value`() {
        every {
            stubLocalDataSource.delete(any())
        } returns Completable.error(stubException)

        repository
            .delete("5598")
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to select model, local data source completes, expected complete value`() {
        every {
            stubLocalDataSource.select(any())
        } returns Completable.complete()

        repository
            .select("5598")
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to select model, local data source throws exception, expected error value`() {
        every {
            stubLocalDataSource.select(any())
        } returns Completable.error(stubException)

        repository
            .select("5598")
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get all, remote returns list, save success, local query success, expected valid domain model list value`() {
        every {
            stubRemoteDataSource.fetch()
        } returns Single.just(mockLocalAiModels)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.getAll()
        } returns Single.just(mockLocalAiModels)

        repository
            .getAll()
            .test()
            .assertNoErrors()
            .assertValue(mockLocalAiModels)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get all, remote returns list, save fails, local query success, expected valid domain model list value`() {
        every {
            stubRemoteDataSource.fetch()
        } returns Single.just(mockLocalAiModels)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.error(stubException)

        every {
            stubLocalDataSource.getAll()
        } returns Single.just(mockLocalAiModels)

        repository
            .getAll()
            .test()
            .assertNoErrors()
            .assertValue(mockLocalAiModels)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get all, remote fails, local query success, expected valid domain model list value`() {
        every {
            stubRemoteDataSource.fetch()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.getAll()
        } returns Single.just(mockLocalAiModels)

        repository
            .getAll()
            .test()
            .assertNoErrors()
            .assertValue(mockLocalAiModels)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get all, remote returns list, save success, local query fails, expected error value`() {
        every {
            stubRemoteDataSource.fetch()
        } returns Single.just(mockLocalAiModels)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.getAll()
        } returns Single.error(stubException)

        repository
            .getAll()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get by id, local data source returns data, expected valid domain model value`() {
        every {
            stubLocalDataSource.getById(any())
        } returns Single.just(mockLocalAiModel)

        repository
            .getById("5598")
            .test()
            .assertNoErrors()
            .assertValue(mockLocalAiModel)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get by id, local data source fails, expected error value`() {
        every {
            stubLocalDataSource.getById(any())
        } returns Single.error(stubException)

        repository
            .getById("5598")
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given observe all models, local data source emits empty list, then another list, expected empty value, then valid domain models list value`() {
        val stubObserver = repository.observeAll().test()

        stubLocalModels.onNext(emptyList())

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, emptyList())

        stubLocalModels.onNext(mockLocalAiModels)

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, mockLocalAiModels)
    }

    @Test
    fun `given observe all models, local data source emits list, then changed list, expected valid domain models list value, then changed value`() {
        val stubObserver = repository.observeAll().test()

        stubLocalModels.onNext(mockLocalAiModels)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, mockLocalAiModels)

        stubLocalModels.onNext(mockLocalAiModels.map { it.copy(id = "1") })

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, listOf(mockLocalAiModel.copy(id = "1")))
    }

    @Test
    fun `given observe all models, local data source throws exception, expected error value`() {
        every {
            stubLocalDataSource.observeAll()
        } returns Flowable.error(stubException)

        repository
            .observeAll()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to download model, local data source has no such model, expected error value`() {
        every {
            stubLocalDataSource.getById(any())
        } returns Single.error(stubException)

        repository
            .download("5598")
            .test()
            .assertNoValues()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to download model, local data source has such model, download succeeds, expected unknown, downloading, complete values`() {
        every {
            stubLocalDataSource.getById(any())
        } returns Single.just(mockLocalAiModel)

        val stubObserver = repository
            .download("5598")
            .test()

        stubDownloadState.onNext(DownloadState.Unknown)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, DownloadState.Unknown)

        stubDownloadState.onNext(DownloadState.Downloading(44))

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, DownloadState.Downloading(44))

        stubDownloadState.onNext(DownloadState.Downloading(100))

        stubObserver
            .assertNoErrors()
            .assertValueAt(2, DownloadState.Downloading(100))

        stubDownloadState.onNext(DownloadState.Complete(stubFile))

        stubObserver
            .assertNoErrors()
            .assertValueAt(3, DownloadState.Complete(stubFile))
    }

    @Test
    fun `given attempt to download model, local data source has such model, download fails, expected unknown, downloading, error values`() {
        every {
            stubLocalDataSource.getById(any())
        } returns Single.just(mockLocalAiModel)

        val stubObserver = repository
            .download("5598")
            .test()

        stubDownloadState.onNext(DownloadState.Unknown)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, DownloadState.Unknown)

        stubDownloadState.onNext(DownloadState.Downloading(44))

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, DownloadState.Downloading(44))

        stubDownloadState.onNext(DownloadState.Error(stubException))

        stubObserver
            .assertNoErrors()
            .assertValueAt(2, DownloadState.Error(stubException))
    }

    @Test
    fun `given attempt to download model, local data source has such model, remote data source throws exception, expected error value`() {
        every {
            stubLocalDataSource.getById(any())
        } returns Single.just(mockLocalAiModel)

        every {
            stubRemoteDataSource.download(any(), any())
        } returns Observable.error(stubException)

        repository
            .download("5598")
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
