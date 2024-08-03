package com.shifthackz.aisdv1.demo

import com.shifthackz.aisdv1.core.common.time.TimeProvider
import com.shifthackz.aisdv1.demo.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.demo.serialize.DemoDataSerializer
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.util.Date

class TextToImageDemoImplTest {

    private val stubSerializer = DemoDataSerializer()
    private val stubTimeProvider = mockk<TimeProvider>()

    private val demo = TextToImageDemoImpl(
        demoDataSerializer = stubSerializer,
        timeProvider = stubTimeProvider,
    )

    @Before
    fun initialize() {
        every {
            stubTimeProvider.currentTimeMillis()
        } returns 5598L

        every {
            stubTimeProvider.currentDate()
        } returns Date(5598L)
    }

    @Test
    fun `given done demo generation, expected generation result base64 is from demo serializer`() {
        demo.getDemoBase64(mockTextToImagePayload)
            .test()
            .await()
            .assertNoErrors()
            .assertValue { actual ->
                stubSerializer
                    .readDemoAssets()
                    .contains(actual.image)
            }
            .assertComplete()
    }
}
