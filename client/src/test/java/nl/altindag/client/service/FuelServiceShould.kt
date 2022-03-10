package nl.altindag.client.service

import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.core.requests.DefaultBody
import com.nhaarman.mockitokotlin2.*
import nl.altindag.client.TestConstants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.TestConstants.HTTP_URL
import nl.altindag.client.util.SSLFactoryTestHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import java.net.URL

class FuelServiceShould {

    @Test
    fun executeRequest() {
        val mockedBody = mock<DefaultBody> {
            onGeneric { toByteArray() } doReturn "Hello".toByteArray()
        }

        val mockedClient = mock<Client> {
            onGeneric { executeRequest(any()) } doReturn Response(
                    statusCode = 200,
                    responseMessage = "OK",
                    body = mockedBody,
                    url = URL(HTTP_URL)
            )
        }

        val sslFactory = SSLFactoryTestHelper.createSSLFactory(true, true)

        FuelManager.instance.client = mockedClient

        val requestCaptor = argumentCaptor<Request>()

        val fuelService = FuelService(sslFactory)
        val clientResponse = fuelService.executeRequest(HTTP_URL)

        assertThat(clientResponse.statusCode).isEqualTo(200)
        assertThat(clientResponse.responseBody).isEqualTo("Hello")

        verify(mockedClient, times(1)).executeRequest(requestCaptor.capture())
        assertThat(requestCaptor.firstValue.url.toString()).isEqualTo(HTTP_URL)
        assertThat(requestCaptor.firstValue.method).isEqualTo(Method.GET)
        assertThat(requestCaptor.firstValue.headers[HEADER_KEY_CLIENT_TYPE]).contains("fuel")

        verify(sslFactory, times(1)).sslSocketFactory
        verify(sslFactory, times(1)).hostnameVerifier
    }

}
