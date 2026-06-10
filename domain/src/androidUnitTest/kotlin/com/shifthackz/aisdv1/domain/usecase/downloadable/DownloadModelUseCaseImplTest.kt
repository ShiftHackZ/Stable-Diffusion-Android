package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadModelUseCaseImplTest {

    private val stubFilePath = "/storage/emulated/0/file.dat"
    private val stubException = Throwable("Error downloading file.")
    private val stubTerminateException = Throwable("Unexpected Flow termination.")
    private val stubDownloadStatus = MutableSharedFlow<DownloadState>()
    private val stubRepository = mockk<DownloadableModelRepository>()

    private val useCase = DownloadModelUseCaseImpl(stubRepository)

    @Before
    fun initialize() {
        every {
            stubRepository.download(any(), any())
        } returns stubDownloadStatus
    }

    @Test
    fun `given download running, then finishes successfully, expected final state is Complete`() = runTest {
        val values = mutableListOf<DownloadState>()
        val job = launch {
            useCase("5598", "https://moroz.cc/stub.zip")
                .take(4)
                .toList(values)
        }
        runCurrent()

        stubDownloadStatus.emit(DownloadState.Unknown)
        stubDownloadStatus.emit(DownloadState.Downloading(33))
        stubDownloadStatus.emit(DownloadState.Downloading(100))
        stubDownloadStatus.emit(DownloadState.Complete(stubFilePath))
        job.join()

        assertEquals(
            listOf(
                DownloadState.Unknown,
                DownloadState.Downloading(33),
                DownloadState.Downloading(100),
                DownloadState.Complete(stubFilePath),
            ),
            values,
        )
    }

    @Test
    fun `given download running, then fails, expected final state is Error`() = runTest {
        val values = mutableListOf<DownloadState>()
        val job = launch {
            useCase("5598", "https://moroz.cc/stub.zip")
                .take(4)
                .toList(values)
        }
        runCurrent()

        stubDownloadStatus.emit(DownloadState.Unknown)
        stubDownloadStatus.emit(DownloadState.Downloading(33))
        stubDownloadStatus.emit(DownloadState.Downloading(100))
        stubDownloadStatus.emit(DownloadState.Error(stubException))
        job.join()

        assertEquals(
            listOf(
                DownloadState.Unknown,
                DownloadState.Downloading(33),
                DownloadState.Downloading(100),
                DownloadState.Error(stubException),
            ),
            values,
        )
    }

    @Test
    fun `given download running, then fails, then user restarts download, then completes, expected state Error on 1st try, final state is Complete`() = runTest {
        val values = mutableListOf<DownloadState>()
        val job = launch {
            useCase("5598", "https://moroz.cc/stub.zip")
                .take(8)
                .toList(values)
        }
        runCurrent()

        stubDownloadStatus.emit(DownloadState.Unknown)
        stubDownloadStatus.emit(DownloadState.Downloading(33))
        stubDownloadStatus.emit(DownloadState.Downloading(100))
        stubDownloadStatus.emit(DownloadState.Error(stubException))
        stubDownloadStatus.emit(DownloadState.Unknown)
        stubDownloadStatus.emit(DownloadState.Downloading(33))
        stubDownloadStatus.emit(DownloadState.Downloading(100))
        stubDownloadStatus.emit(DownloadState.Complete(stubFilePath))
        job.join()

        assertEquals(
            listOf(
                DownloadState.Unknown,
                DownloadState.Downloading(33),
                DownloadState.Downloading(100),
                DownloadState.Error(stubException),
                DownloadState.Unknown,
                DownloadState.Downloading(33),
                DownloadState.Downloading(100),
                DownloadState.Complete(stubFilePath),
            ),
            values,
        )
    }

    @Test
    fun `given flow terminated with unexpected error, expected error value`() = runTest {
        every {
            stubRepository.download(any(), any())
        } returns flow { throw stubTerminateException }

        val actual = runCatching {
            useCase("5598", "https://moroz.cc/stub.zip").collect()
        }.exceptionOrNull()

        assertSame(stubTerminateException, actual)
    }
}
