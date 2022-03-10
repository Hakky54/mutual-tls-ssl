package nl.altindag.client.service

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import nl.altindag.client.ClientType.HTTP4K_JETTY_HTTP_CLIENT
import nl.altindag.client.TestConstants
import nl.altindag.client.util.MockServerTestHelper
import nl.altindag.client.util.SSLFactoryTestHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Http4kJettyHttpClientServiceShould {

    @Test
    fun executeRequest() {
        MockServerTestHelper.mockResponseForClient(HTTP4K_JETTY_HTTP_CLIENT)
        val sslFactory = SSLFactoryTestHelper.createSSLFactory(false, true)

        val client = Http4kJettyHttpClientService(sslFactory)
        val response = client.executeRequest(TestConstants.HTTP_URL)

        assertThat(response.responseBody).isEqualTo("Hello")
        assertThat(response.statusCode).isEqualTo(200)

        verify(sslFactory, times(1)).sslContext
        verify(sslFactory, times(1)).sslParameters
        verify(sslFactory, times(1)).hostnameVerifier
    }

}