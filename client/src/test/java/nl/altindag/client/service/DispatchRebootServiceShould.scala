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

import dispatch.{Http, Req, Res}
import nl.altindag.client.TestConstants.HTTP_URL
import nl.altindag.client.util.SSLFactoryTestHelper
import org.assertj.core.api.Assertions.assertThat
import org.mockito.scalatest.MockitoSugar
import org.scalatest.funspec.AnyFunSpec

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class DispatchRebootServiceShould extends AnyFunSpec with MockitoSugar {

  describe("execute request") {
    val client = mock[Http]
    val response = mock[Res]
    implicit val executionContext: ExecutionContext = any[ExecutionContext]

    when(client(any[Req])).thenReturn(Future.fromTry(Try(response)))
    when(response.getResponseBody).thenReturn("Hello")
    when(response.getStatusCode).thenReturn(200)

    val victim = new DispatchRebootService(client)

    val clientResponse = victim.executeRequest(HTTP_URL)

    assertThat(clientResponse.getStatusCode).isEqualTo(200)
    assertThat(clientResponse.getResponseBody).isEqualTo("Hello")
  }

  describe("create dispatch reboot http client with ssl") {
    val sslFactory = SSLFactoryTestHelper.createSSLFactory(true, true)

    val client = new DispatchRebootHttpClientConfig()
      .createDispatchRebootHttpClient(sslFactory)

    assertThat(client).isNotNull
    verify(sslFactory, times(1)).getKeyManager
    verify(sslFactory, times(1)).getTrustManager
    verify(sslFactory, times(1)).getProtocols
    verify(sslFactory, times(1)).getCiphers
  }

}
