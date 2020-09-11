package nl.altindag.client.service

import java.net.URI

import nl.altindag.client.ClientType.FEATHERBED
import nl.altindag.client.TestConstants.HTTP_URL
import nl.altindag.client.util.{MockServerTestHelper, SSLFactoryTestHelper}
import org.assertj.core.api.Assertions.assertThat
import org.mockito.scalatest.MockitoSugar
import org.scalatest.funspec.AnyFunSpec

class FeatherbedRequestServiceShould extends AnyFunSpec with MockitoSugar {

  describe("execute request") {
    MockServerTestHelper.mockResponseForClient(FEATHERBED)

    val client = new featherbed.Client(URI.create("http://localhost:8080/").toURL)
    val victim = new FeatherbedRequestService(client)
    val clientResponse = victim.executeRequest(HTTP_URL)

    assertThat(clientResponse.getStatusCode).isEqualTo(200)
    assertThat(clientResponse.getResponseBody).isEqualTo("Hello")
  }

  describe("create custom instance of featherbed client without ssl") {
    val client = new FeatherbedClientConfig().createFeatherbedClient(null)

    assertThat(client).isNotNull
  }

  describe("create custom instance of featherbed client with ssl") {
    val sslFactory = SSLFactoryTestHelper.createSSLFactory(true, true)

    val client = new FeatherbedClientConfig().createFeatherbedClient(sslFactory)

    assertThat(client).isNotNull
    verify(sslFactory, times(1)).getSslContext
  }

}
