package com.shifthackz.aisdv1.data.repository

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.data.mocks.mockLocalAiModel
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocalDiffusionGenerationRepositoryImplTest {

    private val stubBitmap = mockk<Bitmap>()
    private val stubException = Throwable("Something went wrong.")
    private val stubStatus = MutableSharedFlow<LocalDiffusionStatus>()
    private val stubMediaStoreGateway = mockk<MediaStoreGateway>()
    private val stubBitmapToBase64Converter = mockk<BitmapToBase64Converter>()
    private val stubLocalDataSource = mockk<GenerationResultDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubLocalDiffusion = mockk<LocalDiffusion>()
    private val stubDownloadableLocalDataSource = mockk<DownloadableModelDataSource.Local>()
    private val stubBackgroundWorkObserver = mockk<BackgroundWorkObserver>()

    private val repository = LocalDiffusionGenerationRepositoryImpl(
        mediaStoreGateway = stubMediaStoreGateway,
        localDataSource = stubLocalDataSource,
        preferenceManager = stubPreferenceManager,
        localDiffusion = stubLocalDiffusion,
        downloadableLocalDataSource = stubDownloadableLocalDataSource,
        bitmapToBase64Converter = stubBitmapToBase64Converter,
        backgroundWorkObserver = stubBackgroundWorkObserver,
    )

    @Before
    fun initialize() {
        every {
            stubBackgroundWorkObserver.hasActiveTasks()
        } returns false

        every {
            stubLocalDiffusion.observeStatus()
        } returns stubStatus

        every {
            stubPreferenceManager.autoSaveAiResults
        } returns false
    }

    @Test
    fun `given attempt to observe status, local emits two values, expected same values with same order`() = runTest {
        val values = mutableListOf<LocalDiffusionStatus>()
        val job = launch {
            repository
                .observeStatus()
                .take(2)
                .toList(values)
        }
        runCurrent()

        stubStatus.emit(LocalDiffusionStatus(1, 2))
        stubStatus.emit(LocalDiffusionStatus(2, 2))
        job.join()

        Assert.assertEquals(
            listOf(LocalDiffusionStatus(1, 2), LocalDiffusionStatus(2, 2)),
            values,
        )
    }

    @Test
    fun `given attempt to observe status, local throws exception, expected error value`() = runTest {
        every {
            stubLocalDiffusion.observeStatus()
        } returns flow { throw stubException }

        val actual = runCatching { repository.observeStatus().toList() }

        assertStubException(actual)
    }

    @Test
    fun `given attempt to interrupt generation, remote completes, expected complete value`() = runTest {
        coEvery {
            stubLocalDiffusion.interrupt()
        } returns Unit

        val actual = runCatching { repository.interruptGeneration() }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to interrupt generation, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubLocalDiffusion.interrupt()
        } throws stubException

        val actual = runCatching { repository.interruptGeneration() }

        assertStubException(actual)
    }

    @Test
    fun `given attempt to generate from text, no selected model, expected error value`() = runTest {
        coEvery {
            stubDownloadableLocalDataSource.getSelectedOnnx()
        } throws stubException

        val actual = runCatching { repository.generateFromText(mockTextToImagePayload) }

        assertStubException(actual)
    }

    @Test
    fun `given attempt to generate from text, has selected not downloaded model, expected IllegalStateException error value`() = runTest {
        coEvery {
            stubDownloadableLocalDataSource.getSelectedOnnx()
        } returns mockLocalAiModel.copy(downloaded = false)

        coEvery {
            stubLocalDiffusion.process(any())
        } returns stubBitmap

        every {
            stubBitmapToBase64Converter(any())
        } returns BitmapToBase64Converter.Output("base64")

        val actual = runCatching { repository.generateFromText(mockTextToImagePayload) }
        val error = actual.exceptionOrNull()

        Assert.assertTrue(error is IllegalStateException)
        Assert.assertEquals("Model not downloaded.", error?.message)
    }

    @Test
    fun `given attempt to generate from text, has selected downloaded model, local process success, expected valid domain model value`() = runTest {
        coEvery {
            stubDownloadableLocalDataSource.getSelectedOnnx()
        } returns mockLocalAiModel.copy(downloaded = true)

        coEvery {
            stubLocalDiffusion.process(any())
        } returns stubBitmap

        every {
            stubBitmapToBase64Converter(any())
        } returns BitmapToBase64Converter.Output("base64")

        val actual = runCatching { repository.generateFromText(mockTextToImagePayload) }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to generate from text, has selected downloaded model, local process fails, expected error value`() = runTest {
        coEvery {
            stubDownloadableLocalDataSource.getSelectedOnnx()
        } returns mockLocalAiModel.copy(downloaded = true)

        coEvery {
            stubLocalDiffusion.process(any())
        } throws stubException

        every {
            stubBitmapToBase64Converter(any())
        } returns BitmapToBase64Converter.Output("base64")

        val actual = runCatching { repository.generateFromText(mockTextToImagePayload) }

        assertStubException(actual)
    }

    private fun <T> assertStubException(actual: Result<T>) {
        val error = actual.exceptionOrNull()
        Assert.assertNotNull(error)
        Assert.assertEquals(stubException.message, error?.message)
    }
}
