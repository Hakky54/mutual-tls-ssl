package nl.altindag.client.service

import nl.altindag.client.ClientType.HTTP4S_JAVA_NET_CLIENT
import nl.altindag.client.TestConstants
import nl.altindag.client.util.{MockServerTestHelper, SSLFactoryTestHelper}
import org.assertj.core.api.Assertions.assertThat
import org.mockito.scalatest.MockitoSugar
import org.scalatest.funspec.AnyFunSpec

class Http4sJavaNetClientServiceShould extends AnyFunSpec with MockitoSugar {

  describe("execute request") {
    MockServerTestHelper.mockResponseForClient(HTTP4S_JAVA_NET_CLIENT)
    val sslFactory = SSLFactoryTestHelper.createSSLFactory(true, true)

    val client = new JavaNetClientConfiguration().createJavaNetClient(sslFactory)
    val victim = new Http4sJavaNetClientService(client)

    val clientResponse = victim.executeRequest(TestConstants.HTTP_URL)

    assertThat(clientResponse.getStatusCode).isEqualTo(200)
    assertThat(clientResponse.getResponseBody).isEqualTo("Hello")

    verify(sslFactory, times(1)).getSslSocketFactory
    verify(sslFactory, times(1)).getHostnameVerifier
  }

  describe("java net io app run function should do nothing") {
    val any = new JavaNetClientConfiguration().run(null)
    assertThat(any).isNull()
  }

}
