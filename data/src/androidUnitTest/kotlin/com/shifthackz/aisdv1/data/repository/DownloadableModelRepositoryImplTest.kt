package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.data.mocks.mockLocalAiModel
import com.shifthackz.aisdv1.data.mocks.mockLocalAiModels
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.entity.DownloadState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DownloadableModelRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubFilePath = "/storage/emulated/0/file.dat"
    private val stubRemoteDataSource = mockk<DownloadableModelDataSource.Remote>()
    private val stubLocalDataSource = mockk<DownloadableModelDataSource.Local>()
    private val stubBuildInfoProvider = mockk<BuildInfoProvider>()

    private val repository = DownloadableModelRepositoryImpl(
        remoteDataSource = stubRemoteDataSource,
        localDataSource = stubLocalDataSource,
        buildInfoProvider = stubBuildInfoProvider,
    )

    @Before
    fun initialize() {
        every {
            stubBuildInfoProvider.type
        } returns BuildType.FULL

        every {
            stubLocalDataSource.observeAllOnnx()
        } returns flowOf(mockLocalAiModels)

        every {
            stubRemoteDataSource.download(any(), any())
        } returns flowOf(DownloadState.Unknown)
    }

    @Test
    fun `given attempt to delete model, local data source completes, expected complete value`() = runTest {
        coEvery {
            stubLocalDataSource.delete(any())
        } returns Unit

        val actual = runCatching { repository.delete("5598") }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to delete model, local data source throws exception, expected error value`() = runTest {
        coEvery {
            stubLocalDataSource.delete(any())
        } throws stubException

        val actual = runCatching { repository.delete("5598") }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to get all, remote returns list, save success, local query success, expected valid domain model list value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetch()
        } returns mockLocalAiModels

        coEvery {
            stubLocalDataSource.save(any())
        } returns Unit

        coEvery {
            stubLocalDataSource.getAllOnnx()
        } returns mockLocalAiModels

        val actual = repository.getAllOnnx()

        Assert.assertEquals(mockLocalAiModels, actual)
    }

    @Test
    fun `given attempt to get all, remote returns list, save fails, local query success, expected valid domain model list value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetch()
        } returns mockLocalAiModels

        coEvery {
            stubLocalDataSource.save(any())
        } throws stubException

        coEvery {
            stubLocalDataSource.getAllOnnx()
        } returns mockLocalAiModels

        val actual = repository.getAllOnnx()

        Assert.assertEquals(mockLocalAiModels, actual)
    }

    @Test
    fun `given attempt to get all, remote fails, local query success, expected valid domain model list value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetch()
        } throws stubException

        coEvery {
            stubLocalDataSource.save(any())
        } returns Unit

        coEvery {
            stubLocalDataSource.getAllOnnx()
        } returns mockLocalAiModels

        val actual = repository.getAllOnnx()

        Assert.assertEquals(mockLocalAiModels, actual)
    }

    @Test
    fun `given attempt to get all, remote returns list, save success, local query fails, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetch()
        } returns mockLocalAiModels

        coEvery {
            stubLocalDataSource.save(any())
        } returns Unit

        coEvery {
            stubLocalDataSource.getAllOnnx()
        } throws stubException

        val actual = runCatching { repository.getAllOnnx() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given observe all models, local data source emits empty list, then another list, expected empty value, then valid domain models list value`() = runTest {
        every {
            stubLocalDataSource.observeAllOnnx()
        } returns flowOf(emptyList(), mockLocalAiModels)

        val actual = repository.observeAllOnnx().toList()

        Assert.assertEquals(listOf(emptyList(), mockLocalAiModels), actual)
    }

    @Test
    fun `given observe all models, local data source emits list, then changed list, expected valid domain models list value, then changed value`() = runTest {
        every {
            stubLocalDataSource.observeAllOnnx()
        } returns flowOf(mockLocalAiModels, mockLocalAiModels.map { it.copy(id = "1") })

        val actual = repository.observeAllOnnx().toList()

        Assert.assertEquals(listOf(mockLocalAiModels, listOf(mockLocalAiModel.copy(id = "1"))), actual)
    }

    @Test
    fun `given observe all models, local data source throws exception, expected error value`() = runTest {
        every {
            stubLocalDataSource.observeAllOnnx()
        } returns flow { throw stubException }

        val actual = runCatching { repository.observeAllOnnx().toList() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to download model, download succeeds, expected unknown, downloading, complete values`() = runTest {
        every {
            stubRemoteDataSource.download(any(), any())
        } returns flowOf(
            DownloadState.Unknown,
            DownloadState.Downloading(44),
            DownloadState.Downloading(100),
            DownloadState.Complete(stubFilePath),
        )

        val actual = repository.download("5598", "https://moroz.cc/stub.zip").toList()

        Assert.assertEquals(
            listOf(
                DownloadState.Unknown,
                DownloadState.Downloading(44),
                DownloadState.Downloading(100),
                DownloadState.Complete(stubFilePath),
            ),
            actual,
        )
    }

    @Test
    fun `given attempt to download model, download fails, expected unknown, downloading, error values`() = runTest {
        every {
            stubRemoteDataSource.download(any(), any())
        } returns flowOf(
            DownloadState.Unknown,
            DownloadState.Downloading(44),
            DownloadState.Error(stubException),
        )

        val actual = repository.download("5598", "https://moroz.cc/stub.zip").toList()

        Assert.assertEquals(
            listOf(
                DownloadState.Unknown,
                DownloadState.Downloading(44),
                DownloadState.Error(stubException),
            ),
            actual,
        )
    }

    @Test
    fun `given attempt to download model, remote data source throws exception, expected error value`() = runTest {
        every {
            stubRemoteDataSource.download(any(), any())
        } returns flow { throw stubException }

        val actual = runCatching {
            repository.download("5598", "https://moroz.cc/stub.zip").toList()
        }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }
}
