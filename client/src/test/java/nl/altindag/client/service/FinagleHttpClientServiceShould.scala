/*
 * Copyright 2018 Thunderberry.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.altindag.client.service

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import nl.altindag.client.TestConstants
import nl.altindag.client.TestConstants.HTTP_URL
import nl.altindag.client.util.SSLFactoryTestHelper
import org.assertj.core.api.Assertions.assertThat
import org.mockito.scalatest.MockitoSugar
import org.scalatest.funspec.AnyFunSpec

class FinagleHttpClientServiceShould extends AnyFunSpec with MockitoSugar {

  describe("execute request") {
    val finagleService = mock[Service[Request, Response]]
    val response = mock[Response]

    when(finagleService.apply(any[Request])).thenReturn(Future.value(response))
    when(response.statusCode).thenReturn(200)
    when(response.contentString).thenReturn("Hello")

    val victim = new FinagleHttpClientService(finagleService)
    val clientResponse = victim.executeRequest(HTTP_URL)

    assertThat(clientResponse.getStatusCode).isEqualTo(200)
    assertThat(clientResponse.getResponseBody).isEqualTo("Hello")
  }

  describe("create finagle without ssl material when url is http and sslFactory is present") {
    System.setProperty("url", TestConstants.HTTP_URL)

    val sslFactory = SSLFactoryTestHelper.createSSLFactory(true, true)
    val client = new FinagleHttpClientConfiguration()
      .createFinagle(sslFactory)

    assertThat(client).isNotNull
    verify(sslFactory, times(0)).getKeyManagerFactory
    verify(sslFactory, times(0)).getTrustManagerFactory
    assertThat(client.isAvailable).isTrue
    assertThat(client.status).hasToString("Open")

    client.close();
    System.clearProperty("url");
  }

  describe("create finagle http client with ssl") {
    System.setProperty("url", TestConstants.HTTPS_URL)

    val sslFactory = SSLFactoryTestHelper.createSSLFactory(true, true)
    val client = new FinagleHttpClientConfiguration()
      .createFinagle(sslFactory)

    assertThat(client).isNotNull
    verify(sslFactory, times(1)).getKeyManagerFactory
    verify(sslFactory, times(1)).getTrustManagerFactory
    assertThat(client.isAvailable).isTrue
    assertThat(client.status).hasToString("Open")

    client.close()
    System.clearProperty("url")
  }

}
