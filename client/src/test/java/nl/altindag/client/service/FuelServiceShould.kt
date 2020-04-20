package nl.altindag.client.service

import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.requests.DefaultBody
import com.nhaarman.mockitokotlin2.*
import nl.altindag.client.TestConstants
import nl.altindag.sslcontext.SSLFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.net.URL
import javax.net.ssl.SSLContext

class FuelServiceShould {

    @Test
    fun executeRequest() {
        val mockedBody = mock<DefaultBody>() {
            onGeneric {
                toByteArray()
            } doReturn "Hello".toByteArray()
        }

        val mockedClient = mock<Client> {
            onGeneric { executeRequest(any()) } doReturn Response(
                    statusCode = 200,
                    responseMessage = "OK",
                    body = mockedBody,
                    url = URL(TestConstants.HTTP_URL)
            )
        }

        FuelManager.instance.client = mockedClient

        val fuelService = FuelService(null)
        val clientResponse = fuelService.executeRequest(TestConstants.HTTP_URL)

        assertThat(clientResponse.statusCode).isEqualTo(200)
        assertThat(clientResponse.responseBody).isEqualTo("Hello")
    }

    @Test
    fun executeRequestWithSslFactory() {
        val mockedBody = mock<DefaultBody>() {
            onGeneric {
                toByteArray()
            } doReturn "Hello".toByteArray()
        }

        val mockedClient = mock<Client> {
            onGeneric { executeRequest(any()) } doReturn Response(
                    statusCode = 200,
                    responseMessage = "OK",
                    body = mockedBody,
                    url = URL(TestConstants.HTTP_URL)
            )
        }

        val mockedSslContext = mock<SSLContext> {
            onGeneric {
                socketFactory
            } doReturn mock()
        }

        val sslFactory = mock<SSLFactory> {
            onGeneric {
                hostnameVerifier
            } doReturn mock()
            onGeneric {
                sslContext
            } doReturn mockedSslContext
        }

        FuelManager.instance.client = mockedClient

        val fuelService = FuelService(sslFactory)
        val clientResponse = fuelService.executeRequest(TestConstants.HTTP_URL)

        assertThat(clientResponse.statusCode).isEqualTo(200)
        assertThat(clientResponse.responseBody).isEqualTo("Hello")

        verify(sslFactory, times(1)).sslContext
        verify(sslFactory, times(1)).hostnameVerifier
    }

}