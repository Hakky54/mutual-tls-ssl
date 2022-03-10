package nl.altindag.client.service

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import nl.altindag.client.ClientType.KTOR_ANDROID_HTTP_CLIENT
import nl.altindag.client.TestConstants.HTTPS_URL
import nl.altindag.client.TestConstants.HTTP_URL
import nl.altindag.client.util.MockServerTestHelper
import nl.altindag.client.util.SSLFactoryTestHelper
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class KtorAndroidHttpClientServiceShould {

    @Test
    fun executeRequest() {
        MockServerTestHelper.mockResponseForClient(KTOR_ANDROID_HTTP_CLIENT)
        val client = KtorAndroidHttpClientService(SSLFactoryTestHelper.createBasic())

        val clientResponse = client.executeRequest(HTTP_URL)

        assertThat(clientResponse.statusCode).isEqualTo(200)
        assertThat(clientResponse.responseBody).isEqualTo("Hello")
    }

    @Test
    fun createClientWithSslMaterial() {
        val sslFactory = SSLFactoryTestHelper.createSSLFactory(true, true)
        val client = KtorAndroidHttpClientService(sslFactory)

        assertThatThrownBy { client.executeRequest(HTTPS_URL) }

        verify(sslFactory, times(1)).sslSocketFactory
        verify(sslFactory, times(1)).hostnameVerifier
    }

}
