package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockHuggingFaceModelsRaw
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.network.api.sdai.HuggingFaceModelsApi
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class HuggingFaceModelsRemoteDataSourceTest {

    private val stubException = Throwable("Error to communicate with api.")
    private val stubApi = mockk<HuggingFaceModelsApi>()

    private val remoteDataSource = HuggingFaceModelsRemoteDataSource(stubApi)

    @Test
    fun `given attempt to fetch hugging face models, api returns two models, expected list value with two domain models`() {
        every {
            stubApi.fetchHuggingFaceModels()
        } returns Single.just(mockHuggingFaceModelsRaw)

        remoteDataSource
            .fetchHuggingFaceModels()
            .test()
            .assertNoErrors()
            .assertValue { models ->
                models is List<HuggingFaceModel>
                        && models.size == mockHuggingFaceModelsRaw.size
                        && models.any { it.id == "050598" }
                        && models.any { it.id == "151297" }
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch hugging face models, api throws exception, expected list value with one default domain model`() {
        every {
            stubApi.fetchHuggingFaceModels()
        } returns Single.error(stubException)

        remoteDataSource
            .fetchHuggingFaceModels()
            .test()
            .assertNoErrors()
            .assertValue { models ->
                models is List<HuggingFaceModel>
                        && models.size == 1
                        && models.first() == HuggingFaceModel.default
            }
            .await()
            .assertComplete()
    }
}
