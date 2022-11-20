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

import nl.altindag.client.ClientType.SCALAJ_HTTP_CLIENT
import nl.altindag.client.TestConstants.HTTP_URL
import nl.altindag.client.model.ClientResponse
import nl.altindag.client.util.{MockServerTestHelper, SSLFactoryTestHelper}
import org.assertj.core.api.Assertions.assertThat
import org.mockito.scalatest.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import scalaj.http.HttpOptions

import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection

class ScalaJHttpClientServiceShould extends AnyFunSpec with MockitoSugar {

  describe("execute request bla") {
    MockServerTestHelper.mockResponseForClient(SCALAJ_HTTP_CLIENT)

    val dummyHttpOption = HttpOptions.connTimeout(1000)
    val victim = new ScalaJHttpClientService(dummyHttpOption)
    val clientResponse: ClientResponse = victim.executeRequest(HTTP_URL)

    assertThat(clientResponse.getStatusCode).isEqualTo(200)
    assertThat(clientResponse.getResponseBody).isEqualTo("Hello")
  }

  describe("create http option with ssl material when url is https and sslFactory is present") {
    val httpsURLConnection = mock[HttpsURLConnection]
    val sslFactory = SSLFactoryTestHelper.createBasic()

    val victim = new ScalaJHttpClientConfiguration()
    val httpOption = victim.createHttpOption(sslFactory)

    httpOption.apply(httpsURLConnection)

    verify(httpsURLConnection, times(1)).setHostnameVerifier(any)
    verify(httpsURLConnection, times(1)).setSSLSocketFactory(any)
  }

  describe("create http option without ssl material when url is http and sslFactory is present") {
    val httpURLConnection = mock[HttpURLConnection]
    val sslFactory = SSLFactoryTestHelper.createBasic()

    val victim = new ScalaJHttpClientConfiguration()
    val httpOption = victim.createHttpOption(sslFactory)

    httpOption.apply(httpURLConnection)

    verify(sslFactory, times(0)).getHostnameVerifier
    verify(sslFactory, times(0)).getSslSocketFactory
  }

}
