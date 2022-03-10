package nl.altindag.client.service

import com.nhaarman.mockitokotlin2.*
import nl.altindag.client.TestConstants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.TestConstants.HTTP_URL
import nl.altindag.client.util.SSLFactoryTestHelper
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KohttpServiceShould {

    @Test
    fun executeRequest() {
        val responseBody = mock<ResponseBody> {
            onGeneric { string() } doReturn "Hello"
        }

        val response = mock<Response> {
            onGeneric { code } doReturn 200
            onGeneric { body } doReturn responseBody
        }

        val call = mock<okhttp3.Call> {
            onGeneric { execute() } doReturn response
        }

        val client = mock<OkHttpClient> {
            onGeneric { newCall(any()) } doReturn call
        }

        val requestCaptor = argumentCaptor<Request>()

        val victim = KohttpService(client)
        val clientResponse = victim.executeRequest(HTTP_URL)

        assertThat(clientResponse.statusCode).isEqualTo(200)
        assertThat(clientResponse.responseBody).isEqualTo("Hello")

        verify(client, times(1)).newCall(requestCaptor.capture())
        assertThat(requestCaptor.firstValue.header(HEADER_KEY_CLIENT_TYPE)).isEqualTo("kohttp")
        assertThat(requestCaptor.firstValue.url.toUri().toString()).isEqualTo(HTTP_URL)
        assertThat(requestCaptor.firstValue.method).isEqualTo("GET")
    }

    @Test
    fun createClientWithSslMaterial() {
        val sslFactory = SSLFactoryTestHelper.createSSLFactory(true, true)

        val victim = KohttpClientConfig()
        val client = victim.createKohttpClient(sslFactory)

        assertThat(client).isNotNull
        verify(sslFactory, times(1)).sslSocketFactory
        verify(sslFactory, times(1)).trustManager
        verify(sslFactory, times(1)).hostnameVerifier
    }

}
