package com.shifthackz.aisdv1.data.remote


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.domain.datasource.HordeGenerationDataSource
import com.shifthackz.aisdv1.network.api.horde.HordeRestApi
import com.shifthackz.aisdv1.network.response.HordeGenerationAsyncResponse
import com.shifthackz.aisdv1.network.response.HordeGenerationCheckFullResponse
import com.shifthackz.aisdv1.network.response.HordeGenerationCheckResponse
import com.shifthackz.aisdv1.network.response.HordeUserResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Test
import java.net.URL
import java.util.concurrent.TimeUnit

class HordeGenerationRemoteDataSourceTest {

    private val stubBytes = ByteArray(1024)
    private val stubBitmap = mockk<Bitmap>()
    private val stubException = Throwable("Internal server error.")
    private val stubApi = mockk<HordeRestApi>()
    private val stubBmpToBase64Converter = mockk<BitmapToBase64Converter>()
    private val stubHordeStatusSource = mockk<HordeGenerationDataSource.StatusSource>()

    private val remoteDataSource = HordeGenerationRemoteDataSource(
        hordeApi = stubApi,
        converter = stubBmpToBase64Converter,
        statusSource = stubHordeStatusSource,
    )

    @Test
    fun `given attempt to validate api key, api returns user with valid id, expected true value`() {
        every {
            stubApi.checkHordeApiKey()
        } returns Single.just(HordeUserResponse(5598))

        remoteDataSource
            .validateApiKey()
            .test()
            .assertNoErrors()
            .assertValue(true)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to validate api key, api returns null, expected false value`() {
        every {
            stubApi.checkHordeApiKey()
        } returns Single.just(HordeUserResponse(null))

        remoteDataSource
            .validateApiKey()
            .test()
            .assertNoErrors()
            .assertValue(false)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to validate api key, api throws exception, expected false value`() {
        every {
            stubApi.checkHordeApiKey()
        } returns Single.error(stubException)

        remoteDataSource
            .validateApiKey()
            .test()
            .assertNoErrors()
            .assertValue(false)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to interrupt generation, id present in cache, api returns success, expected complete value`() {
        every {
            stubHordeStatusSource.id
        } returns "5598"

        every {
            stubApi.cancelRequest(any())
        } returns Completable.complete()

        remoteDataSource
            .interruptGeneration()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to interrupt generation, id present in cache, api throws exception, expected error value`() {
        every {
            stubHordeStatusSource.id
        } returns "5598"

        every {
            stubApi.cancelRequest(any())
        } returns Completable.error(stubException)

        remoteDataSource
            .interruptGeneration()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to interrupt generation, no id present in cache, expected error value`() {
        every {
            stubHordeStatusSource.id
        } returns null

        remoteDataSource
            .interruptGeneration()
            .test()
            .assertError { t ->
                t is IllegalStateException && t.message == "No cached request id"
            }
            .await()
            .assertNotComplete()
    }
}
