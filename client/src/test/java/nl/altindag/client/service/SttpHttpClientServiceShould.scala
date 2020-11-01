package nl.altindag.client.service

import java.net.URI

import nl.altindag.client.TestConstants.{HTTPS_URL, HTTP_URL}
import nl.altindag.client.model.ClientResponse
import nl.altindag.client.util.SSLFactoryTestHelper
import org.assertj.core.api.Assertions.{assertThat, assertThatThrownBy}
import org.mockito.ArgumentCaptor
import org.mockito.scalatest.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import sttp.client.{Identity, Request, RequestT, Response, SttpBackend, basicRequest}
import sttp.model.{Header, StatusCode, Uri}

class SttpHttpClientServiceShould extends AnyFunSpec with MockitoSugar {

  describe("execute request") {
    val mockedBackend = mock[SttpBackend[Identity, Any]]
    val mockedResponse = mock[Identity[Response[Either[String, String]]]]
    val mockedBody = mock[Either[String, String]]

    when(mockedBackend.send(any[RequestT[Identity, Either[String, String], Any]])).thenReturn(mockedResponse)
    when(mockedResponse.code).thenReturn(StatusCode.Ok)
    when(mockedResponse.body).thenReturn(mockedBody)
    when(mockedBody.toOption).thenReturn(Option.apply("Hello"))

    val requestArgumentCaptor: ArgumentCaptor[RequestT[Identity, Either[String, String], Any]] = {
      ArgumentCaptor.forClass(classOf[RequestT[Identity, Either[String, String], Any]])
    }

    val victim = new SttpHttpClientService(mockedBackend)
    val clientResponse: ClientResponse = victim.executeRequest(HTTP_URL)

    assertThat(clientResponse.getStatusCode).isEqualTo(200)
    assertThat(clientResponse.getResponseBody).isEqualTo("Hello")

    verify(mockedBackend, times(1)).send(requestArgumentCaptor.capture())
    assertThat(requestArgumentCaptor.getValue.headers.toArray).contains(new Header("client-type", "sttp"))
  }

  describe("create Sttp backend client without ssl") {
    val victim: SttpBackend[Identity, Any] = new SttpHttpClientConfiguration().createSttpBackendClient(null)
    assertThat(victim).isNotNull
  }

  describe("create Sttp backend client with ssl") {
    val sslFactory = SSLFactoryTestHelper.createSSLFactory(true, true)

    val request: Request[Either[String, String], Any] =  basicRequest.get(uri = Uri(javaUri = URI.create(HTTPS_URL)))
    val victim: SttpBackend[Identity, Any] = new SttpHttpClientConfiguration().createSttpBackendClient(sslFactory)

    assertThat(victim).isNotNull
    assertThatThrownBy(() => victim.send(request))

    verify(sslFactory, times(1)).getHostnameVerifier
    verify(sslFactory, times(1)).getSslSocketFactory
  }

  describe("create Sttp backend client without ssl when sslFactory is absent") {
    val request: Request[Either[String, String], Any] =  basicRequest.get(uri = Uri(javaUri = URI.create(HTTPS_URL)))
    val victim: SttpBackend[Identity, Any] = new SttpHttpClientConfiguration().createSttpBackendClient(null)

    assertThat(victim).isNotNull
    assertThatThrownBy(() => victim.send(request))
  }

  describe("create Sttp backend client without ssl when url is http scheme") {
    val sslFactory = SSLFactoryTestHelper.createSSLFactory(true, true)

    val request: Request[Either[String, String], Any] =  basicRequest.get(uri = Uri(javaUri = URI.create(HTTP_URL)))
    val victim: SttpBackend[Identity, Any] = new SttpHttpClientConfiguration().createSttpBackendClient(sslFactory)

    assertThat(victim).isNotNull
    assertThatThrownBy(() => victim.send(request))

    verify(sslFactory, times(0)).getHostnameVerifier
    verify(sslFactory, times(0)).getSslSocketFactory
  }

}
