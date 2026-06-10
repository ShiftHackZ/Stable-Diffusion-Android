package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.mocks.mockMediaStoreInfo
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class GetMediaStoreInfoUseCaseImplTest {

    private val stubMediaStoreGateway = mockk<MediaStoreGateway>()

    private val useCase = GetMediaStoreInfoUseCaseImpl(stubMediaStoreGateway)

    @Test
    fun `given gateway provided media store info, expected valid media store info`() = runTest {
        every {
            stubMediaStoreGateway.getInfo()
        } returns mockMediaStoreInfo

        val actual = useCase()

        assertEquals(mockMediaStoreInfo, actual)
    }

    @Test
    fun `given gateway provided empty media store info, expected default media store info`() = runTest {
        every {
            stubMediaStoreGateway.getInfo()
        } returns MediaStoreInfo()

        val actual = useCase()

        assertEquals(MediaStoreInfo(), actual)
    }

    @Test
    fun `given gateway thrown exception, expected error value`() = runTest {
        val stubException = Throwable("Error communicating with MediaStore.")

        every {
            stubMediaStoreGateway.getInfo()
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
