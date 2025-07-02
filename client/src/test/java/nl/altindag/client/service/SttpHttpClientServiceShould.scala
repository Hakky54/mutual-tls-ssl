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

import java.net.URI
import nl.altindag.client.TestConstants.{HTTPS_URL, HTTP_URL}
import nl.altindag.client.model.ClientResponse
import nl.altindag.client.util.SSLFactoryTestHelper
import org.assertj.core.api.Assertions.{assertThat, assertThatThrownBy}
import org.mockito.ArgumentCaptor
import org.mockito.scalatest.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import sttp.client4.{GenericRequest, Response, SyncBackend, basicRequest}
import sttp.model.{Header, StatusCode, Uri}

class SttpHttpClientServiceShould extends AnyFunSpec with MockitoSugar {

  describe("execute request") {
    val mockedBackend = mock[SyncBackend]
    val mockedResponse = mock[Response[Either[String, String]]]
    val mockedBody = mock[Either[String, String]]

    when(mockedBackend.send(any[GenericRequest[Any, Any]])).thenReturn(mockedResponse)
    when(mockedResponse.code).thenReturn(StatusCode.Ok)
    when(mockedResponse.body).thenReturn(mockedBody)
    when(mockedBody.toOption).thenReturn(Option.apply("Hello"))

    val requestArgumentCaptor: ArgumentCaptor[GenericRequest[Any, Any]] = {
      ArgumentCaptor.forClass(classOf[GenericRequest[Any, Any]])
    }

    val victim = new SttpHttpClientService(mockedBackend)
    val clientResponse: ClientResponse = victim.executeRequest(HTTP_URL)

    assertThat(clientResponse.getStatusCode).isEqualTo(200)
    assertThat(clientResponse.getResponseBody).isEqualTo("Hello")

    verify(mockedBackend, times(1)).send(requestArgumentCaptor.capture())
    assertThat(requestArgumentCaptor.getValue.headers.toArray).contains(new Header("client-type", "sttp"))
  }

  describe("create Sttp backend client without ssl") {
    val victim: SyncBackend = new SttpHttpClientConfiguration().createSttpBackendClient(null)
    assertThat(victim).isNotNull
  }

  describe("create Sttp backend client with ssl") {
    val sslFactory = SSLFactoryTestHelper.createSSLFactory(true, true)

    val request: GenericRequest[Any, Any] =  basicRequest.get(uri = Uri(javaUri = URI.create(HTTPS_URL)))
    val victim: SyncBackend = new SttpHttpClientConfiguration().createSttpBackendClient(sslFactory)

    assertThat(victim).isNotNull
    assertThatThrownBy(() => victim.send(request))

    verify(sslFactory, times(1)).getHostnameVerifier
    verify(sslFactory, times(1)).getSslSocketFactory
  }

  describe("create Sttp backend client without ssl when sslFactory is absent") {
    val request: GenericRequest[Any, Any] =  basicRequest.get(uri = Uri(javaUri = URI.create(HTTPS_URL)))
    val victim: SyncBackend = new SttpHttpClientConfiguration().createSttpBackendClient(null)

    assertThat(victim).isNotNull
    assertThatThrownBy(() => victim.send(request))
  }

  describe("create Sttp backend client without ssl when url is http scheme") {
    val sslFactory = SSLFactoryTestHelper.createSSLFactory(true, true)

    val request: GenericRequest[Any, Any] =  basicRequest.get(uri = Uri(javaUri = URI.create(HTTP_URL)))
    val victim: SyncBackend = new SttpHttpClientConfiguration().createSttpBackendClient(sslFactory)

    assertThat(victim).isNotNull
    assertThatThrownBy(() => victim.send(request))

    verify(sslFactory, times(0)).getHostnameVerifier
    verify(sslFactory, times(0)).getSslSocketFactory
  }

}
