package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockHuggingFaceModels
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class HuggingFaceModelsRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRemoteDataSource = mockk<HuggingFaceModelsRemoteDataSource>()
    private val stubLocalDataSource = mockk<HuggingFaceModelsDataSource.Local>()

    private val repository = HuggingFaceModelsRepositoryImpl(
        remoteDataSource = stubRemoteDataSource,
        localDataSource = stubLocalDataSource,
    )

    @Test
    fun `given attempt to fetch models, remote returns data, local insert success, expected complete value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchHuggingFaceModels()
        } returns mockHuggingFaceModels

        coEvery {
            stubLocalDataSource.save(any())
        } returns Unit

        repository.fetchHuggingFaceModels()

        coVerify {
            stubLocalDataSource.save(mockHuggingFaceModels)
        }
    }

    @Test
    fun `given attempt to fetch models, remote throws exception, local insert success, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchHuggingFaceModels()
        } throws stubException

        val actual = runCatching { repository.fetchHuggingFaceModels() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to fetch models, remote returns data, local insert fails, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchHuggingFaceModels()
        } returns mockHuggingFaceModels

        coEvery {
            stubLocalDataSource.save(any())
        } throws stubException

        val actual = runCatching { repository.fetchHuggingFaceModels() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to get models, local data source returns list, expected valid domain models list value`() = runTest {
        coEvery {
            stubLocalDataSource.getAll()
        } returns mockHuggingFaceModels

        val actual = repository.getHuggingFaceModels()

        Assert.assertEquals(mockHuggingFaceModels, actual)
    }

    @Test
    fun `given attempt to get models, local data source returns empty list, expected supported fallback models list value`() = runTest {
        coEvery {
            stubLocalDataSource.getAll()
        } returns emptyList()

        val actual = repository.getHuggingFaceModels()

        Assert.assertEquals(HuggingFaceModel.supportedHfInferenceTextToImageModels, actual)
    }

    @Test
    fun `given attempt to get models, local data source throws exception, expected error value`() = runTest {
        coEvery {
            stubLocalDataSource.getAll()
        } throws stubException

        val actual = runCatching { repository.getHuggingFaceModels() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to fetch and get models, remote returns data, local returns data, expected valid domain models list value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchHuggingFaceModels()
        } returns mockHuggingFaceModels

        coEvery {
            stubLocalDataSource.save(any())
        } returns Unit

        coEvery {
            stubLocalDataSource.getAll()
        } returns mockHuggingFaceModels

        val actual = repository.fetchAndGetHuggingFaceModels()

        Assert.assertEquals(mockHuggingFaceModels, actual)
    }

    @Test
    fun `given attempt to fetch and get models, remote fails, local returns data, expected valid domain models list value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchHuggingFaceModels()
        } throws stubException

        coEvery {
            stubLocalDataSource.getAll()
        } returns mockHuggingFaceModels

        val actual = repository.fetchAndGetHuggingFaceModels()

        Assert.assertEquals(mockHuggingFaceModels, actual)
    }

    @Test
    fun `given attempt to fetch and get models, remote fails, local fails, expected valid error value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchHuggingFaceModels()
        } throws stubException

        coEvery {
            stubLocalDataSource.getAll()
        } throws stubException

        val actual = runCatching { repository.fetchAndGetHuggingFaceModels() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }
}
