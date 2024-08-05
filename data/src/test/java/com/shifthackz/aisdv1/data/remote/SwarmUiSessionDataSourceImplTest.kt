package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.preference.SessionPreference
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi
import com.shifthackz.aisdv1.network.response.SwarmUiSessionResponse
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class SwarmUiSessionDataSourceImplTest {

    private val stubApi = mockk<SwarmUiApi>()
    private val stubSessionPreference = mockk<SessionPreference>()
    private val stubServerUrlProvider = mockk<ServerUrlProvider>()

    private val remoteDataSource = SwarmUiSessionDataSourceImpl(
        api = stubApi,
        sessionPreference = stubSessionPreference,
        serverUrlProvider = stubServerUrlProvider,
    )

    @Before
    fun initialize() {
        every {
            stubServerUrlProvider(any())
        } returns Single.just("http://192.168.0.1:7801")
    }

    @Test
    fun `given session present in preference, expected sessionId value from preference`() {
        every {
            stubSessionPreference::swarmUiSessionId.get()
        } returns "5598"

        remoteDataSource
            .getSessionId()
            .test()
            .assertNoErrors()
            .await()
            .assertValue("5598")
            .assertComplete()
    }

    @Test
    fun `given session NOT present in preference, API returns session, expected sessionId value from API`() {
        every {
            stubSessionPreference::swarmUiSessionId.get()
        } returns ""

        every {
            stubSessionPreference::swarmUiSessionId.set(any())
        } returns Unit

        every {
            stubApi.getNewSession(any())
        } returns Single.just(SwarmUiSessionResponse("5598"))

        remoteDataSource
            .getSessionId()
            .test()
            .assertNoErrors()
            .await()
            .assertValue("5598")
            .assertComplete()
    }

    @Test
    fun `given session NOT present in preference, API returns null, expected error value`() {
        every {
            stubSessionPreference::swarmUiSessionId.get()
        } returns ""

        every {
            stubSessionPreference::swarmUiSessionId.set(any())
        } returns Unit

        every {
            stubApi.getNewSession(any())
        } returns Single.just(SwarmUiSessionResponse(null))

        remoteDataSource
            .getSessionId()
            .test()
            .assertError { t -> t is IllegalStateException && t.message == "Bad session ID." }
            .await()
            .assertNoValues()
            .assertNotComplete()
    }
}
