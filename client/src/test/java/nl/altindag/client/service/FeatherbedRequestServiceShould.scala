package nl.altindag.client.service

import java.net.URI

import nl.altindag.client.TestConstants
import nl.altindag.sslcontext.SSLFactory
import org.assertj.core.api.Assertions.assertThat
import org.mockito.scalatest.MockitoSugar
import org.mockserver.client.MockServerClient
import org.scalatest.funspec.AnyFunSpec
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.matchers.Times.exactly
import org.mockserver.integration.ClientAndServer.startClientAndServer


class FeatherbedRequestServiceShould extends AnyFunSpec with MockitoSugar {

  describe("execute request") {
    // Using MockServer because mocking the specific client is not possible
    val mockServer = startClientAndServer(8080)
    new MockServerClient("127.0.0.1", 8080)
      .when(
        request()
          .withMethod("GET")
          .withPath("/api/hello")
          .withHeader("client-type", "featherbed"),
        exactly(1))
      .respond(
        response()
          .withBody("Hello")
          .withStatusCode(200)
      )

    val client = new featherbed.Client(URI.create("http://localhost:8080/").toURL)
    val victim = new FeatherbedRequestService(client)
    val clientResponse = victim.executeRequest(TestConstants.HTTP_URL)

    assertThat(clientResponse.getStatusCode).isEqualTo(200)
    assertThat(clientResponse.getResponseBody).isEqualTo("Hello")

    mockServer.stop()
  }

  describe("create custom instance of featherbed client without ssl") {
    val client = new FeatherbedClientConfig().createClient(null)

    assertThat(client).isNotNull
  }

  describe("create custom instance of featherbed client with ssl") {
    val sslFactory = mock[SSLFactory]

    val client = new FeatherbedClientConfig().createClient(sslFactory)

    assertThat(client).isNotNull
    verify(sslFactory, times(1)).getSslContext
  }

}
