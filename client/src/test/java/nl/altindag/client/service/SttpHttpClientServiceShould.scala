package nl.altindag.client.service

import java.net.URI

import javax.net.ssl.SSLContext
import nl.altindag.client.TestConstants
import nl.altindag.client.model.ClientResponse
import nl.altindag.sslcontext.SSLFactory
import org.assertj.core.api.Assertions.{assertThat, assertThatThrownBy}
import org.mockito.scalatest.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import sttp.client.{Identity, NothingT, Request, RequestT, Response, SttpBackend, basicRequest}
import sttp.model.{StatusCode, Uri}

class SttpHttpClientServiceShould extends AnyFunSpec with MockitoSugar {

  describe("execute request") {
    val mockedBackend = mock[SttpBackend[Identity, Nothing, NothingT]]
    val mockedResponse = mock[Identity[Response[Either[String, String]]]]
    val mockedBody = mock[Either[String, String]]

    when(mockedBackend.send(any[RequestT[Identity, Either[String, String], Nothing]])).thenReturn(mockedResponse)
    when(mockedResponse.code).thenReturn(StatusCode.Ok)
    when(mockedResponse.body).thenReturn(mockedBody)
    when(mockedBody.toOption).thenReturn(Option.apply("Hello"))

    val victim = new SttpHttpClientService(mockedBackend)
    val clientResponse: ClientResponse = victim.executeRequest(TestConstants.HTTP_URL)

    assertThat(clientResponse.getStatusCode).isEqualTo(200)
    assertThat(clientResponse.getResponseBody).isEqualTo("Hello")
  }

  describe("create Sttp backend client without ssl") {
    val victim: SttpBackend[Identity, Nothing, NothingT] = new SttpHttpClientConfiguration().createSttpBackendClient(null)
    assertThat(victim).isNotNull
  }

  describe("create Sttp backend client with ssl") {
    val sslFactory = mock[SSLFactory]
    val sslContext = mock[SSLContext]

    when(sslFactory.getSslContext).thenReturn(sslContext)

    val request: Request[Either[String, String], Nothing] =  basicRequest.get(uri = Uri(javaUri = URI.create(TestConstants.HTTPS_URL)))
    val victim: SttpBackend[Identity, Nothing, NothingT] = new SttpHttpClientConfiguration().createSttpBackendClient(sslFactory)

    when(sslFactory.getSslContext).thenReturn(sslContext)

    assertThat(victim).isNotNull
    assertThatThrownBy(() => victim.send(request))

    verify(sslFactory, times(1)).getHostnameVerifier
    verify(sslFactory, times(1)).getSslContext
    verify(sslContext, times(1)).getSocketFactory
  }

  describe("create Sttp backend client without ssl when sslFactory is absent") {
    val request: Request[Either[String, String], Nothing] =  basicRequest.get(uri = Uri(javaUri = URI.create(TestConstants.HTTPS_URL)))
    val victim: SttpBackend[Identity, Nothing, NothingT] = new SttpHttpClientConfiguration().createSttpBackendClient(null)

    assertThat(victim).isNotNull
    assertThatThrownBy(() => victim.send(request))
  }

  describe("create Sttp backend client without ssl when url is http scheme") {
    val sslFactory = mock[SSLFactory]
    val sslContext = mock[SSLContext]

    val request: Request[Either[String, String], Nothing] =  basicRequest.get(uri = Uri(javaUri = URI.create(TestConstants.HTTP_URL)))
    val victim: SttpBackend[Identity, Nothing, NothingT] = new SttpHttpClientConfiguration().createSttpBackendClient(sslFactory)

    assertThat(victim).isNotNull
    assertThatThrownBy(() => victim.send(request))

    verify(sslFactory, times(0)).getHostnameVerifier
    verify(sslFactory, times(0)).getSslContext
    verify(sslContext, times(0)).getSocketFactory
  }

}
