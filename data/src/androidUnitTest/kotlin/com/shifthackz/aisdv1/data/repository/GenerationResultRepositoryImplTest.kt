package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResultPreviews
import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResults
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.AiGenerationResultPreview
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GenerationResultRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubMediaStoreGateway = mockk<MediaStoreGateway>()
    private val stubLocalDataSource = mockk<GenerationResultDataSource.Local>()

    private val repository = GenerationResultRepositoryImpl(
        preferenceManager = stubPreferenceManager,
        mediaStoreGateway = stubMediaStoreGateway,
        localDataSource = stubLocalDataSource,
    )

    @Before
    fun initialize() {
        every {
            stubPreferenceManager.saveToMediaStore
        } returns false
    }

    @Test
    fun `given attempt to get all, local returns data, expected valid domain model list value`() = runTest {
        coEvery {
            stubLocalDataSource.queryAll()
        } returns mockAiGenerationResults

        val actual = repository.getAll()

        Assert.assertEquals(mockAiGenerationResults, actual)
    }

    @Test
    fun `given attempt to get all, local returns empty data, expected empty domain model list value`() = runTest {
        coEvery {
            stubLocalDataSource.queryAll()
        } returns emptyList()

        val actual = repository.getAll()

        Assert.assertEquals(emptyList<AiGenerationResult>(), actual)
    }

    @Test
    fun `given attempt to get all, local throws exception, expected error value`() = runTest {
        coEvery {
            stubLocalDataSource.queryAll()
        } throws stubException

        val actual = runCatching { repository.getAll() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to get all ids, local returns ids, expected valid id list value`() = runTest {
        coEvery {
            stubLocalDataSource.queryIds()
        } returns listOf(5598L, 1504L)

        val actual = repository.getAllIds()

        Assert.assertEquals(listOf(5598L, 1504L), actual)
    }

    @Test
    fun `given attempt to get page, local returns data, expected valid domain model list value`() = runTest {
        coEvery {
            stubLocalDataSource.queryPage(any(), any())
        } returns mockAiGenerationResults

        val actual = repository.getPage(20, 0)

        Assert.assertEquals(mockAiGenerationResults, actual)
    }

    @Test
    fun `given attempt to get page, local returns empty data, expected empty domain model list value`() = runTest {
        coEvery {
            stubLocalDataSource.queryPage(any(), any())
        } returns emptyList()

        val actual = repository.getPage(20, 0)

        Assert.assertEquals(emptyList<AiGenerationResult>(), actual)
    }

    @Test
    fun `given attempt to get page, local throws exception, expected error value`() = runTest {
        coEvery {
            stubLocalDataSource.queryPage(any(), any())
        } throws stubException

        val actual = runCatching { repository.getPage(20, 0) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to get preview page, local returns data, expected valid preview list value`() = runTest {
        coEvery {
            stubLocalDataSource.queryPagePreview(any(), any())
        } returns mockAiGenerationResultPreviews

        val actual = repository.getPagePreview(30, 0)

        Assert.assertEquals(mockAiGenerationResultPreviews, actual)
    }

    @Test
    fun `given attempt to get preview page, local returns empty data, expected empty preview list value`() = runTest {
        coEvery {
            stubLocalDataSource.queryPagePreview(any(), any())
        } returns emptyList()

        val actual = repository.getPagePreview(30, 0)

        Assert.assertEquals(emptyList<AiGenerationResultPreview>(), actual)
    }

    @Test
    fun `given attempt to observe preview page, local emits data, expected valid preview list value`() = runTest {
        every {
            stubLocalDataSource.observePagePreview(any(), any())
        } returns flowOf(mockAiGenerationResultPreviews)

        val actual = repository.observePagePreview(30, 0).first()

        Assert.assertEquals(mockAiGenerationResultPreviews, actual)
    }

    @Test
    fun `given attempt to get by id, local returns data, expected valid domain model value`() = runTest {
        coEvery {
            stubLocalDataSource.queryById(any())
        } returns mockAiGenerationResult

        val actual = repository.getById(5598L)

        Assert.assertEquals(mockAiGenerationResult, actual)
    }

    @Test
    fun `given attempt to get by id, local throws exception, expected error value`() = runTest {
        coEvery {
            stubLocalDataSource.queryById(any())
        } throws stubException

        val actual = runCatching { repository.getById(5598L) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to get by id list, local returns data, expected valid domain model list value`() = runTest {
        coEvery {
            stubLocalDataSource.queryByIdList(any())
        } returns mockAiGenerationResults

        val actual = repository.getByIds(listOf(5598L, 151297L))

        Assert.assertEquals(mockAiGenerationResults, actual)
    }

    @Test
    fun `given attempt to delete by id list, local delete success, expected complete value`() = runTest {
        coEvery {
            stubLocalDataSource.deleteByIdList(any())
        } returns Unit

        val actual = runCatching { repository.deleteByIdList(listOf(5598L, 151297L)) }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to delete by id list, local delete fails, expected error value`() = runTest {
        coEvery {
            stubLocalDataSource.deleteByIdList(any())
        } throws stubException

        val actual = runCatching { repository.deleteByIdList(listOf(5598L, 151297L)) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to delete by id, local delete success, expected complete value`() = runTest {
        coEvery {
            stubLocalDataSource.deleteById(any())
        } returns Unit

        val actual = runCatching { repository.deleteById(5598L) }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to delete by id, local delete fails, expected error value`() = runTest {
        coEvery {
            stubLocalDataSource.deleteById(any())
        } throws stubException

        val actual = runCatching { repository.deleteById(5598L) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to delete all, local delete success, expected complete value`() = runTest {
        coEvery {
            stubLocalDataSource.deleteAll()
        } returns Unit

        val actual = runCatching { repository.deleteAll() }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to delete all, local delete fails, expected complete value`() = runTest {
        coEvery {
            stubLocalDataSource.deleteAll()
        } throws stubException

        val actual = runCatching { repository.deleteAll() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to set visibility by ids, local update succeeds, expected complete value`() = runTest {
        coEvery {
            stubLocalDataSource.updateHiddenByIdList(listOf(5598L), true)
        } returns Unit

        val actual = runCatching { repository.setVisibilityByIds(listOf(5598L), true) }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to set visibility by ids, local update fails, expected error value`() = runTest {
        coEvery {
            stubLocalDataSource.updateHiddenByIdList(any(), any())
        } throws stubException

        val actual = runCatching { repository.setVisibilityByIds(listOf(5598L), false) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to set liked by ids, local update succeeds, expected complete value`() = runTest {
        coEvery {
            stubLocalDataSource.updateLikedByIdList(listOf(5598L), true)
        } returns Unit

        val actual = runCatching { repository.setLikedByIds(listOf(5598L), true) }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to set liked by ids, local update fails, expected error value`() = runTest {
        coEvery {
            stubLocalDataSource.updateLikedByIdList(any(), any())
        } throws stubException

        val actual = runCatching { repository.setLikedByIds(listOf(5598L), false) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to insert data, local insert success, expected id of inserted model value`() = runTest {
        coEvery {
            stubLocalDataSource.insert(any())
        } returns mockAiGenerationResult.id

        val actual = repository.insert(mockAiGenerationResult)

        Assert.assertEquals(5598L, actual)
    }

    @Test
    fun `given attempt to insert data, local insert fails, expected error value`() = runTest {
        coEvery {
            stubLocalDataSource.insert(any())
        } throws stubException

        val actual = runCatching { repository.insert(mockAiGenerationResult) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to toggle image visibility, process succeeds, expected boolean value`() = runTest {
        coEvery {
            stubLocalDataSource.queryById(any())
        } returnsMany listOf(
            mockAiGenerationResult.copy(hidden = false),
            mockAiGenerationResult.copy(hidden = true),
        )

        coEvery {
            stubLocalDataSource.insert(any())
        } returns 5598L

        val actual = repository.toggleVisibility(5598L)

        Assert.assertTrue(actual)
    }

    @Test
    fun `given attempt to toggle image visibility, error occurs, expected boolean value`() = runTest {
        coEvery {
            stubLocalDataSource.queryById(any())
        } returns mockAiGenerationResult

        coEvery {
            stubLocalDataSource.insert(any())
        } throws stubException

        val actual = runCatching { repository.toggleVisibility(5598L) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to toggle image like, process succeeds, expected boolean value`() = runTest {
        coEvery {
            stubLocalDataSource.queryById(any())
        } returnsMany listOf(
            mockAiGenerationResult.copy(liked = false),
            mockAiGenerationResult.copy(liked = true),
        )

        coEvery {
            stubLocalDataSource.insert(any())
        } returns 5598L

        val actual = repository.toggleLike(5598L)

        Assert.assertTrue(actual)
    }

    @Test
    fun `given attempt to toggle image like, error occurs, expected boolean value`() = runTest {
        coEvery {
            stubLocalDataSource.queryById(any())
        } returns mockAiGenerationResult

        coEvery {
            stubLocalDataSource.insert(any())
        } throws stubException

        val actual = runCatching { repository.toggleLike(5598L) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }
}
