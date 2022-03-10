package nl.altindag.client.service

import nl.altindag.client.ClientType.REQUESTS_SCALA
import nl.altindag.client.TestConstants.HTTP_URL
import nl.altindag.client.model.ClientResponse
import nl.altindag.client.util.{MockServerTestHelper, SSLFactoryTestHelper}
import org.assertj.core.api.Assertions.assertThat
import org.mockito.scalatest.MockitoSugar
import org.scalatest.funspec.AnyFunSpec

class RequestsScalaServiceShould extends AnyFunSpec with MockitoSugar {

  describe("execute request") {
    MockServerTestHelper.mockResponseForClient(REQUESTS_SCALA)

    val sslFactory = SSLFactoryTestHelper.createBasic()
    val victim = new RequestsScalaService(sslFactory)
    val clientResponse: ClientResponse = victim.executeRequest(HTTP_URL)

    assertThat(clientResponse.getStatusCode).isEqualTo(200)
    assertThat(clientResponse.getResponseBody).isEqualTo("Hello")
    verify(sslFactory, times(1)).getSslContext()
  }

}
