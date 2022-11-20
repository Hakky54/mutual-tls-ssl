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

import nl.altindag.client.ClientType.HTTP4S_BLAZE_CLIENT
import nl.altindag.client.TestConstants
import nl.altindag.client.util.{MockServerTestHelper, SSLFactoryTestHelper}
import org.assertj.core.api.Assertions.assertThat
import org.mockito.scalatest.MockitoSugar
import org.scalatest.funspec.AnyFunSpec

class Http4sBlazeClientServiceShould extends AnyFunSpec with MockitoSugar {

  describe("execute request") {
    MockServerTestHelper.mockResponseForClient(HTTP4S_BLAZE_CLIENT)
    val sslFactory = SSLFactoryTestHelper.createSSLFactory(true, true)

    val client = new BlazeClientConfiguration().createBlazeClient(sslFactory)
    val victim = new Http4sBlazeClientService(client)

    val clientResponse = victim.executeRequest(TestConstants.HTTP_URL)

    assertThat(clientResponse.getStatusCode).isEqualTo(200)
    assertThat(clientResponse.getResponseBody).isEqualTo("Hello")
    verify(sslFactory, times(1)).getSslContext
  }

  describe("blaze io app run function should do nothing") {
    val any = new BlazeClientConfiguration().run(null)
    assertThat(any).isNull()
  }

}
