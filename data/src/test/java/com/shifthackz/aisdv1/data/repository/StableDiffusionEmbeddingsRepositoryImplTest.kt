package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionEmbeddings
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionEmbeddingsDataSource
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class StableDiffusionEmbeddingsRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRemoteDataSource = mockk<StableDiffusionEmbeddingsDataSource.Remote>()
    private val stubLocalDataSource = mockk<StableDiffusionEmbeddingsDataSource.Local>()

    private val repository = StableDiffusionEmbeddingsRepositoryImpl(
        remoteDataSource = stubRemoteDataSource,
        localDataSource = stubLocalDataSource,
    )

    @Test
    fun `given attempt to fetch embeddings, remote returns data, local insert success, expected complete value`() {
        every {
            stubRemoteDataSource.fetchEmbeddings()
        } returns Single.just(mockStableDiffusionEmbeddings)

        every {
            stubLocalDataSource.insertEmbeddings(any())
        } returns Completable.complete()

        repository
            .fetchEmbeddings()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch embeddings, remote throws exception, local insert success, expected error value`() {
        every {
            stubRemoteDataSource.fetchEmbeddings()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.insertEmbeddings(any())
        } returns Completable.complete()

        repository
            .fetchEmbeddings()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch embeddings, remote returns data, local insert fails, expected error value`() {
        every {
            stubRemoteDataSource.fetchEmbeddings()
        } returns Single.just(mockStableDiffusionEmbeddings)

        every {
            stubLocalDataSource.insertEmbeddings(any())
        } returns Completable.error(stubException)

        repository
            .fetchEmbeddings()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get embeddings, local data source returns list, expected valid domain models list value`() {
        every {
            stubLocalDataSource.getEmbeddings()
        } returns Single.just(mockStableDiffusionEmbeddings)

        repository
            .getEmbeddings()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionEmbeddings)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get embeddings, local data source returns empty list, expected empty domain models list value`() {
        every {
            stubLocalDataSource.getEmbeddings()
        } returns Single.just(emptyList())

        repository
            .getEmbeddings()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get embeddings, local data source throws exception, expected error value`() {
        every {
            stubLocalDataSource.getEmbeddings()
        } returns Single.error(stubException)

        repository
            .getEmbeddings()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch and get embeddings, remote returns data, local returns data, expected valid domain models list value`() {
        every {
            stubRemoteDataSource.fetchEmbeddings()
        } returns Single.just(mockStableDiffusionEmbeddings)

        every {
            stubLocalDataSource.insertEmbeddings(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.getEmbeddings()
        } returns Single.just(mockStableDiffusionEmbeddings)

        repository
            .fetchAndGetEmbeddings()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionEmbeddings)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get embeddings, remote fails, local returns data, expected valid domain models list value`() {
        every {
            stubRemoteDataSource.fetchEmbeddings()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.insertEmbeddings(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.getEmbeddings()
        } returns Single.just(mockStableDiffusionEmbeddings)

        repository
            .fetchAndGetEmbeddings()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionEmbeddings)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get embeddings, remote fails, local fails, expected valid error value`() {
        every {
            stubRemoteDataSource.fetchEmbeddings()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.getEmbeddings()
        } returns Single.error(stubException)

        repository
            .fetchAndGetEmbeddings()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
