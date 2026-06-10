package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockSupporters
import com.shifthackz.aisdv1.domain.datasource.SupportersDataSource
import com.shifthackz.aisdv1.domain.datasource.SupportersRemoteDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class SupportersRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRds = mockk<SupportersRemoteDataSource>()
    private val stubLds = mockk<SupportersDataSource.Local>()

    private val repository = SupportersRepositoryImpl(stubRds, stubLds)

    @Test
    fun `given attempt to fetch supporters, remote returns data, local insert success, expected complete value`() = runTest {
        coEvery {
            stubRds.fetch()
        } returns mockSupporters

        coEvery {
            stubLds.save(any())
        } returns Unit

        repository.fetchSupporters()

        coVerify {
            stubLds.save(mockSupporters)
        }
    }

    @Test
    fun `given attempt to fetch supporters, remote throws exception, local insert success, expected error value`() = runTest {
        coEvery {
            stubRds.fetch()
        } throws stubException

        val actual = runCatching { repository.fetchSupporters() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to fetch supporters, remote returns data, local insert fails, expected error value`() = runTest {
        coEvery {
            stubRds.fetch()
        } returns mockSupporters

        coEvery {
            stubLds.save(any())
        } throws stubException

        val actual = runCatching { repository.fetchSupporters() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to get supporters, local data source returns list, expected valid domain models list value`() = runTest {
        coEvery {
            stubLds.getAll()
        } returns mockSupporters

        val actual = repository.getSupporters()

        Assert.assertEquals(mockSupporters, actual)
    }

    @Test
    fun `given attempt to get supporters, local data source returns empty list, expected empty domain models list value`() = runTest {
        coEvery {
            stubLds.getAll()
        } returns emptyList()

        val actual = repository.getSupporters()

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given attempt to get supporters, local data source throws exception, expected error value`() = runTest {
        coEvery {
            stubLds.getAll()
        } throws stubException

        val actual = runCatching { repository.getSupporters() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to fetch and get supporters, remote returns data, local returns data, expected valid domain models list value`() = runTest {
        coEvery {
            stubRds.fetch()
        } returns mockSupporters

        coEvery {
            stubLds.save(any())
        } returns Unit

        coEvery {
            stubLds.getAll()
        } returns mockSupporters

        val actual = repository.fetchAndGetSupporters()

        Assert.assertEquals(mockSupporters, actual)
    }

    @Test
    fun `given attempt to fetch and get supporters, remote fails, local returns data, expected valid domain models list value`() = runTest {
        coEvery {
            stubRds.fetch()
        } throws stubException

        coEvery {
            stubLds.getAll()
        } returns mockSupporters

        val actual = repository.fetchAndGetSupporters()

        Assert.assertEquals(mockSupporters, actual)
    }

    @Test
    fun `given attempt to fetch and get supporters, remote fails, local fails, expected valid error value`() = runTest {
        coEvery {
            stubRds.fetch()
        } throws stubException

        coEvery {
            stubLds.getAll()
        } throws stubException

        val actual = runCatching { repository.fetchAndGetSupporters() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }
}
