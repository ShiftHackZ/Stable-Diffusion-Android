package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import java.io.File

class DownloadModelUseCaseImplTest {

    private val stubFile = File("/storage/emulated/0/file.dat")
    private val stubException = Throwable("Error downloading file.")
    private val stubTerminateException = Throwable("Unexpected Observable termination.")
    private val stubDownloadStatus = PublishSubject.create<DownloadState>()
    private val stubRepository = mock<DownloadableModelRepository>()

    private val useCase = DownloadModelUseCaseImpl(stubRepository)

    @Before
    fun initialize() {
        whenever(stubRepository.download(any()))
            .thenReturn(stubDownloadStatus)
    }

    @Test
    fun `given download running, then finishes successfully, expected final state is Complete`() {
        val stubObserver = useCase("5598").test()

        stubDownloadStatus.onNext(DownloadState.Unknown)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, DownloadState.Unknown)

        stubDownloadStatus.onNext(DownloadState.Downloading(33))

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, DownloadState.Downloading(33))

        stubDownloadStatus.onNext(DownloadState.Downloading(100))

        stubObserver
            .assertNoErrors()
            .assertValueAt(2, DownloadState.Downloading(100))

        stubDownloadStatus.onNext(DownloadState.Complete(stubFile))

        stubObserver
            .assertNoErrors()
            .assertValueAt(3, DownloadState.Complete(stubFile))
    }

    @Test
    fun `given download running, then fails, expected final state is Error`() {
        val stubObserver = useCase("5598").test()

        stubDownloadStatus.onNext(DownloadState.Unknown)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, DownloadState.Unknown)

        stubDownloadStatus.onNext(DownloadState.Downloading(33))

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, DownloadState.Downloading(33))

        stubDownloadStatus.onNext(DownloadState.Downloading(100))

        stubObserver
            .assertNoErrors()
            .assertValueAt(2, DownloadState.Downloading(100))

        stubDownloadStatus.onNext(DownloadState.Error(stubException))

        stubObserver
            .assertNoErrors()
            .assertValueAt(3, DownloadState.Error(stubException))
    }

    @Test
    fun `given download running, then fails, then user restarts download, then completes, expected state Error on 1st try, final state is Complete`() {
        val stubObserver = useCase("5598").test()

        stubDownloadStatus.onNext(DownloadState.Unknown)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, DownloadState.Unknown)

        stubDownloadStatus.onNext(DownloadState.Downloading(33))

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, DownloadState.Downloading(33))

        stubDownloadStatus.onNext(DownloadState.Downloading(100))

        stubObserver
            .assertNoErrors()
            .assertValueAt(2, DownloadState.Downloading(100))

        stubDownloadStatus.onNext(DownloadState.Error(stubException))

        stubObserver
            .assertNoErrors()
            .assertValueAt(3, DownloadState.Error(stubException))

        stubDownloadStatus.onNext(DownloadState.Unknown)

        stubObserver
            .assertNoErrors()
            .assertValueAt(4, DownloadState.Unknown)

        stubDownloadStatus.onNext(DownloadState.Downloading(33))

        stubObserver
            .assertNoErrors()
            .assertValueAt(5, DownloadState.Downloading(33))

        stubDownloadStatus.onNext(DownloadState.Downloading(100))

        stubObserver
            .assertNoErrors()
            .assertValueAt(6, DownloadState.Downloading(100))

        stubDownloadStatus.onNext(DownloadState.Complete(stubFile))

        stubObserver
            .assertNoErrors()
            .assertValueAt(7, DownloadState.Complete(stubFile))
    }

    @Test
    fun `given observable terminated with unexpected error, expected error value`() {
        whenever(stubRepository.download(any()))
            .thenReturn(Observable.error(stubTerminateException))

        useCase("5598")
            .test()
            .assertError(stubTerminateException)
            .await()
            .assertNotComplete()
    }
}
