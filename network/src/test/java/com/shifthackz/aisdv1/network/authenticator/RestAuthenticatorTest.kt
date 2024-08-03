package com.shifthackz.aisdv1.network.authenticator

import com.shifthackz.aisdv1.network.qualifiers.CredentialsProvider
import com.shifthackz.aisdv1.network.qualifiers.NetworkHeaders
import io.mockk.every
import io.mockk.mockk
import okhttp3.Address
import okhttp3.Authenticator
import okhttp3.Dns
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.junit.Assert
import org.junit.Test
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.ProxySelector
import javax.net.SocketFactory

class RestAuthenticatorTest {

    private val stubResponse
        get() = Response.Builder()
            .request(
                Request.Builder()
                    .url("http://192.168.0.1:8080")
                    .build()
            )
            .protocol(Protocol.HTTP_1_0)
            .message("msg")
            .code(333)
            .build()

    private val stubRoute
        get() = Route(
            address = Address(
                uriHost = "192.168.0.1",
                uriPort = 8080,
                dns = Dns.SYSTEM,
                socketFactory = SocketFactory.getDefault(),
                sslSocketFactory = null,
                hostnameVerifier = null,
                certificatePinner = null,
                proxyAuthenticator = Authenticator.NONE,
                proxy = null,
                protocols = emptyList(),
                connectionSpecs = emptyList(),
                proxySelector = ProxySelector.getDefault(),
            ),
            proxy = Proxy.NO_PROXY,
            socketAddress = InetSocketAddress.createUnresolved("192.168.0.1", 8080)
        )

    private val stubCredentialsProvider = mockk<CredentialsProvider>()

    private val authenticator = RestAuthenticator(stubCredentialsProvider)

    @Test
    fun `given provider has no credentials, expected authenticator returned null response value`() {
        every {
            stubCredentialsProvider()
        } returns CredentialsProvider.Data.None

        val expected: Response? = null
        val actual = authenticator.authenticate(stubRoute, stubResponse)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given provider has HTTP credentials, expected authenticator returned response with AUTHORIZATION header`() {
        val stubLogin = "5598"
        val stubPassword = "is_my_favorite"

        every {
            stubCredentialsProvider()
        } returns CredentialsProvider.Data.HttpBasic(
            login = stubLogin,
            password = stubPassword,
        )

        val expected = "Basic NTU5ODppc19teV9mYXZvcml0ZQ=="
        val actual = authenticator.authenticate(stubRoute, stubResponse)
        Assert.assertEquals(true, actual != null)
        Assert.assertEquals(expected, actual?.headers?.get(NetworkHeaders.AUTHORIZATION))
    }
}
