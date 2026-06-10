package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToCheckpointDomain
import com.shifthackz.aisdv1.data.mocks.mockDownloadableModelsResponse
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.network.api.sdai.SdaiAppApi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class DownloadableModelRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubApi = mockk<SdaiAppApi>()
    private val stubFileDownloader = mockk<DownloadableModelFileDownloader>()

    private val remoteDataSource = DownloadableModelRemoteDataSource(
        api = stubApi,
        fileDownloader = stubFileDownloader,
    )

    @Test
    fun `given attempt to fetch models list, api returns data, expected valid domain models list`() = runTest {
        coEvery {
            stubApi.fetchOnnxModels()
        } returns mockDownloadableModelsResponse

        coEvery {
            stubApi.fetchMediaPipeModels()
        } returns mockDownloadableModelsResponse

        val expected = listOf(
            mockDownloadableModelsResponse.mapRawToCheckpointDomain(LocalAiModel.Type.ONNX),
            mockDownloadableModelsResponse.mapRawToCheckpointDomain(LocalAiModel.Type.MediaPipe),
        ).flatten()

        val actual = remoteDataSource.fetch()

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given attempt to fetch models list, api returns empty data, expected empty domain models list`() = runTest {
        coEvery {
            stubApi.fetchOnnxModels()
        } returns emptyList()

        coEvery {
            stubApi.fetchMediaPipeModels()
        } returns emptyList()

        val actual = remoteDataSource.fetch()

        Assert.assertEquals(emptyList<LocalAiModel>(), actual)
    }

    @Test
    fun `given attempt to fetch models list, api returns error, expected error value`() = runTest {
        coEvery {
            stubApi.fetchOnnxModels()
        } throws stubException

        coEvery {
            stubApi.fetchMediaPipeModels()
        } throws stubException

        val actual = runCatching { remoteDataSource.fetch() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to download model, expected file downloader invoked with id and url`() = runTest {
        every {
            stubFileDownloader.download(MODEL_ID, MODEL_URL)
        } returns flowOf(
            DownloadState.Downloading(0),
            DownloadState.Complete(FILE_PATH),
        )

        val actual = remoteDataSource.download(MODEL_ID, MODEL_URL).toList()

        Assert.assertEquals(
            listOf(
                DownloadState.Downloading(0),
                DownloadState.Complete(FILE_PATH),
            ),
            actual,
        )
        verify {
            stubFileDownloader.download(MODEL_ID, MODEL_URL)
        }
    }

    private companion object {
        const val MODEL_ID = "model_id"
        const val MODEL_URL = "https://example.com/model.zip"
        const val FILE_PATH = "/storage/emulated/0/model.zip"
    }
}
