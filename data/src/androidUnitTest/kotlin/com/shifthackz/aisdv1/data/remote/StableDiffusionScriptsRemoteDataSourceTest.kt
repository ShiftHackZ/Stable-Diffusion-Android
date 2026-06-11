package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi
import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.model.StableDiffusionExtensionRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionScriptInfoRaw
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class StableDiffusionScriptsRemoteDataSourceTest {

    private val stubBaseUrl = "http://192.168.0.1:7860"
    private val stubCredentials = AuthorizationCredentials.HttpBasic("user", "password")
    private val stubAuthorization = BasicHttpAuthorization("user", "password")
    private val stubApi = mockk<Automatic1111MetadataApi>()

    private val remoteDataSource = KtorStableDiffusionScriptsRemoteDataSource(
        api = stubApi,
    )

    @Test
    fun `given adetailer only exists in extensions, expected scripts contain ADetailer`() = runTest {
        coEvery {
            stubApi.fetchScriptInfo(stubBaseUrl, stubAuthorization)
        } returns listOf(
            StableDiffusionScriptInfoRaw(
                name = "seed",
                isAlwaysOn = true,
                isImg2Img = false,
            ),
        )
        coEvery {
            stubApi.fetchExtensions(stubBaseUrl, stubAuthorization)
        } returns listOf(
            StableDiffusionExtensionRaw(
                name = "adetailer",
                enabled = true,
            ),
        )

        val actual = remoteDataSource.fetchScripts(stubBaseUrl, stubCredentials)

        assertTrue(actual.contains("ADetailer"))
    }
}
