package com.shifthackz.aisdv1.data.remote

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.data.mappers.mapRawToCheckpointDomain
import com.shifthackz.aisdv1.data.mocks.mockDownloadableModelsResponse
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.network.api.sdai.DownloadableModelsApi
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class DownloadableModelRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubApi = mock<DownloadableModelsApi>()
    private val stubFileProviderDescriptor = mock<FileProviderDescriptor>()

    private val remoteDataSource = DownloadableModelRemoteDataSource(
        api = stubApi,
        fileProviderDescriptor = stubFileProviderDescriptor,
    )

    @Test
    fun `given attempt to fetch models list, api returns data, expected valid domain models list`() {
        whenever(stubApi.fetchOnnxModels())
            .thenReturn(Single.just(mockDownloadableModelsResponse))

        whenever(stubApi.fetchMediaPipeModels())
            .thenReturn(Single.just(mockDownloadableModelsResponse))

        val expected = listOf(
            mockDownloadableModelsResponse.mapRawToCheckpointDomain(LocalAiModel.Type.ONNX),
            mockDownloadableModelsResponse.mapRawToCheckpointDomain(LocalAiModel.Type.MediaPipe),
        ).flatten()

        remoteDataSource
            .fetch()
            .test()
            .assertNoErrors()
            .assertValue(expected)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch models list, api returns empty data, expected empty domain models list`() {
        whenever(stubApi.fetchOnnxModels())
            .thenReturn(Single.just(emptyList()))

        whenever(stubApi.fetchMediaPipeModels())
            .thenReturn(Single.just(emptyList()))

        remoteDataSource
            .fetch()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch models list, api returns error, expected error value`() {
        whenever(stubApi.fetchOnnxModels())
            .thenReturn(Single.error(stubException))

        whenever(stubApi.fetchMediaPipeModels())
            .thenReturn(Single.error(stubException))

        remoteDataSource
            .fetch()
            .test()
            .assertError(stubException)
            .assertValueCount(0)
            .await()
            .assertNotComplete()
    }
}
