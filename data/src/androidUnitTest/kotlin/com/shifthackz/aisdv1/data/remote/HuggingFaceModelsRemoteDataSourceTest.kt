package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockHuggingFaceModelsRaw
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.network.api.huggingface.HuggingFaceModelsApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class HuggingFaceModelsRemoteDataSourceTest {

    private val stubException = Throwable("Error to communicate with api.")
    private val stubApi = mockk<HuggingFaceModelsApi>()

    private val remoteDataSource = KtorHuggingFaceModelsRemoteDataSource(stubApi)

    @Test
    fun `given attempt to fetch hugging face models, api returns two models, expected list value with two domain models`() = runTest {
        coEvery {
            stubApi.fetchTextToImageModels()
        } returns mockHuggingFaceModelsRaw

        val models = remoteDataSource.fetchHuggingFaceModels()

        Assert.assertTrue(
            models.size == mockHuggingFaceModelsRaw.size
                    && models.any { it.id == "black-forest-labs/FLUX.1-schnell" }
                    && models.any { it.id == "stabilityai/stable-diffusion-3-medium-diffusers" }
        )
    }

    @Test
    fun `given attempt to fetch hugging face models, api throws exception, expected error value`() = runTest {
        coEvery {
            stubApi.fetchTextToImageModels()
        } throws stubException

        val actual = runCatching { remoteDataSource.fetchHuggingFaceModels() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }
}
