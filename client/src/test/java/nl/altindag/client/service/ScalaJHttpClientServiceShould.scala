package nl.altindag.client.service

import java.net.HttpURLConnection

import javax.net.ssl.{HostnameVerifier, HttpsURLConnection, SSLContext, SSLSocketFactory}
import nl.altindag.client.ClientType.SCALAJ_HTTP_CLIENT
import nl.altindag.client.TestConstants.HTTP_URL
import nl.altindag.client.model.ClientResponse
import nl.altindag.client.util.MockServerTestHelper
import nl.altindag.sslcontext.SSLFactory
import org.assertj.core.api.Assertions.assertThat
import org.mockito.scalatest.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import scalaj.http.HttpOptions

class ScalaJHttpClientServiceShould extends AnyFunSpec with MockitoSugar {

  describe("execute request") {
    val mockServerTestHelper = new MockServerTestHelper(SCALAJ_HTTP_CLIENT)

    val dummyHttpOption = HttpOptions.connTimeout(1000)
    val victim = new ScalaJHttpClientService(dummyHttpOption)
    val clientResponse: ClientResponse = victim.executeRequest(HTTP_URL)

    assertThat(clientResponse.getStatusCode).isEqualTo(200)
    assertThat(clientResponse.getResponseBody).isEqualTo("Hello")

    mockServerTestHelper.stop();
  }

  describe("create http option with ssl material when url is https and sslFactory is present") {
    val httpsURLConnection = mock[HttpsURLConnection]
    val sslFactory = mock[SSLFactory]
    val sslContext = mock[SSLContext]
    val socketFactory = mock[SSLSocketFactory]
    val hostnameVerifier = mock[HostnameVerifier]

    when(sslFactory.getSslContext).thenReturn(sslContext)
    when(sslContext.getSocketFactory).thenReturn(socketFactory)
    when(sslFactory.getHostnameVerifier).thenReturn(hostnameVerifier)

    val victim = new ScalaJHttpClientConfiguration()
    val httpOption = victim.createHttpOption(sslFactory)

    httpOption.apply(httpsURLConnection)

    verify(httpsURLConnection, times(1)).setHostnameVerifier(sslFactory.getHostnameVerifier)
    verify(httpsURLConnection, times(1)).setSSLSocketFactory(sslFactory.getSslContext.getSocketFactory)
  }

  describe("create http option without ssl material when url is https and sslFactory is absent") {
    val httpsURLConnection = mock[HttpsURLConnection]

    val victim = new ScalaJHttpClientConfiguration()
    val httpOption = victim.createHttpOption(null)

    httpOption.apply(httpsURLConnection)

    verify(httpsURLConnection, times(0)).setHostnameVerifier(any[HostnameVerifier])
    verify(httpsURLConnection, times(0)).setSSLSocketFactory(any[SSLSocketFactory])
  }

  describe("create http option without ssl material when url is http and sslFactory is present") {
    val httpURLConnection = mock[HttpURLConnection]
    val sslFactory = mock[SSLFactory]
    val sslContext = mock[SSLContext]
    val socketFactory = mock[SSLSocketFactory]
    val hostnameVerifier = mock[HostnameVerifier]

    when(sslFactory.getSslContext).thenReturn(sslContext)
    when(sslContext.getSocketFactory).thenReturn(socketFactory)
    when(sslFactory.getHostnameVerifier).thenReturn(hostnameVerifier)

    val victim = new ScalaJHttpClientConfiguration()
    val httpOption = victim.createHttpOption(sslFactory)

    httpOption.apply(httpURLConnection)

    verify(sslFactory, times(0)).getHostnameVerifier
    verify(sslFactory, times(0)).getSslContext
  }

}
