package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.FileLoggingTree
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.reactivex.rxjava3.core.Completable
import org.junit.After
import org.junit.Before
import org.junit.Test

class ClearAppCacheUseCaseImplTest {

    private val stubException = Throwable("Fatal error.")
    private val stubFileProviderDescriptor = mockk<FileProviderDescriptor>()
    private val stubRepository = mockk<GenerationResultRepository>()

    private val useCase = ClearAppCacheUseCaseImpl(
        fileProviderDescriptor = stubFileProviderDescriptor,
        repository = stubRepository,
    )

    @Before
    fun initialize() {
        mockkObject(FileLoggingTree)
    }

    @After
    fun finalize() {
        unmockkObject(FileLoggingTree)
    }

    @Test
    fun `given repository and logs clear success, expected complete value`() {
        every {
            stubRepository.deleteAll()
        } returns Completable.complete()

        every {
            stubFileProviderDescriptor.logsCacheDirPath
        } returns "/tmp/cache"

        every {
            FileLoggingTree.clearLog(any())
        } returns Unit

        useCase()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository clear fails, logs clear success, expected error value`() {
        every {
            stubRepository.deleteAll()
        } returns Completable.error(stubException)

        every {
            stubFileProviderDescriptor.logsCacheDirPath
        } returns "/tmp/cache"

        every {
            FileLoggingTree.clearLog(any())
        } returns Unit

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given repository clear success, logs clear fails, expected error value`() {
        every {
            stubRepository.deleteAll()
        } returns Completable.complete()

        every {
            stubFileProviderDescriptor.logsCacheDirPath
        } returns "/tmp/cache"

        every {
            FileLoggingTree.clearLog(any())
        } throws stubException

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given repository clear success, file provider throws exception, expected error value`() {
        every {
            stubRepository.deleteAll()
        } returns Completable.complete()

        every {
            stubFileProviderDescriptor.logsCacheDirPath
        } throws stubException

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
