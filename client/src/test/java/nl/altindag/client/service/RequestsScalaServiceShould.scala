package nl.altindag.client.service

import javax.net.ssl.SSLContext
import nl.altindag.client.ClientType.REQUESTS_SCALA
import nl.altindag.client.TestConstants.HTTP_URL
import nl.altindag.client.model.ClientResponse
import nl.altindag.client.util.MockServerTestHelper
import nl.altindag.sslcontext.SSLFactory
import org.assertj.core.api.Assertions.assertThat
import org.mockito.scalatest.MockitoSugar
import org.scalatest.funspec.AnyFunSpec

class RequestsScalaServiceShould extends AnyFunSpec with MockitoSugar {

  describe("execute request") {
    MockServerTestHelper.mockResponseForClient(REQUESTS_SCALA)

    describe("execute request without ssl material") {

      val victim = new RequestsScalaService(null)
      val clientResponse: ClientResponse = victim.executeRequest(HTTP_URL)

      assertThat(clientResponse.getStatusCode).isEqualTo(200)
      assertThat(clientResponse.getResponseBody).isEqualTo("Hello")
    }

    describe("execute request with ssl material") {
      val sslFactory = mock[SSLFactory]
      val sslContext = mock[SSLContext]

      when(sslFactory.getSslContext).thenReturn(sslContext)

      val victim = new RequestsScalaService(sslFactory)
      val clientResponse: ClientResponse = victim.executeRequest(HTTP_URL)

      assertThat(clientResponse.getStatusCode).isEqualTo(200)
      assertThat(clientResponse.getResponseBody).isEqualTo("Hello")
      verify(sslFactory, times(1)).getSslContext()
    }
  }

}
