package nl.altindag.client.service

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import nl.altindag.client.ClientType.KTOR_APACHE_HTTP_CLIENT
import nl.altindag.client.TestConstants.HTTP_URL
import nl.altindag.client.util.MockServerTestHelper
import nl.altindag.client.util.SSLFactoryTestHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class KtorApacheHttpClientServiceShould {

    @Test
    fun executeRequest() {
        val mockServerTestHelper = MockServerTestHelper(KTOR_APACHE_HTTP_CLIENT)
        val client = KtorApacheHttpClientService(null)

        val clientResponse = client.executeRequest(HTTP_URL)

        assertThat(clientResponse.statusCode).isEqualTo(200)
        assertThat(clientResponse.responseBody).isEqualTo("Hello")

        mockServerTestHelper.stop()
    }

    @Test
    fun createClientWithSslMaterial() {
        val sslFactory = SSLFactoryTestHelper.createSSLFactory(true, true)

        KtorApacheHttpClientService(sslFactory)

        verify(sslFactory, times(1)).sslContext
    }

}
