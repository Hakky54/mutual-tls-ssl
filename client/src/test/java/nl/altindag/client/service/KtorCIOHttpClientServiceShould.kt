package nl.altindag.client.service

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import nl.altindag.client.ClientType.KTOR_CIO_HTTP_CLIENT
import nl.altindag.client.TestConstants.HTTP_URL
import nl.altindag.client.util.MockServerTestHelper
import nl.altindag.client.util.SSLFactoryTestHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class KtorCIOHttpClientServiceShould {

    @Test
    fun executeRequest() {
        MockServerTestHelper.mockResponseForClient(KTOR_CIO_HTTP_CLIENT)
        val client = KtorCIOHttpClientService(null)

        val clientResponse = client.executeRequest(HTTP_URL)

        assertThat(clientResponse.statusCode).isEqualTo(200)
        assertThat(clientResponse.responseBody).isEqualTo("Hello")
    }

    @Test
    fun createClientWithSslMaterial() {
        val sslFactory = SSLFactoryTestHelper.createSSLFactory(false, true)

        KtorCIOHttpClientService(sslFactory)

        verify(sslFactory, times(1)).trustManager
        verify(sslFactory, times(1)).identities
    }

}
