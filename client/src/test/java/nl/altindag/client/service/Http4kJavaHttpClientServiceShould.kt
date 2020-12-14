package nl.altindag.client.service

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import nl.altindag.client.ClientType.HTTP4K_JAVA_HTTP_CLIENT
import nl.altindag.client.TestConstants
import nl.altindag.client.util.MockServerTestHelper
import nl.altindag.client.util.SSLFactoryTestHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Http4kJavaHttpClientServiceShould {

    @Test
    fun executeRequest() {
        MockServerTestHelper.mockResponseForClient(HTTP4K_JAVA_HTTP_CLIENT)

        val client = Http4kJavaHttpClientService(null)
        val response = client.executeRequest(TestConstants.HTTP_URL)

        assertThat(response.responseBody).isEqualTo("Hello")
        assertThat(response.statusCode).isEqualTo(200)
    }

    @Test
    fun createClientWithSslMaterial() {
        val sslFactory = SSLFactoryTestHelper.createSSLFactory(false, true)

        Http4kJavaHttpClientService(sslFactory)

        verify(sslFactory, times(1)).sslContext
        verify(sslFactory, times(1)).sslParameters
    }

}