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

import nl.altindag.client.ClientType.HTTP4K_JETTY_HTTP_CLIENT
import nl.altindag.client.TestConstants
import nl.altindag.client.util.MockServerTestHelper
import nl.altindag.client.util.SSLFactoryTestHelper
import nl.altindag.ssl.jetty.util.JettySslUtils
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jetty.client.HttpClient
import org.junit.jupiter.api.Test
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class Http4kJettyHttpClientServiceShould {

    @Test
    fun executeRequest() {
        MockServerTestHelper.mockResponseForClient(HTTP4K_JETTY_HTTP_CLIENT)
        val sslFactory = SSLFactoryTestHelper.createSSLFactory(false, true)

        val sslContextFactory = JettySslUtils.forClient(sslFactory)
        val httpClient = HttpClient()
        httpClient.sslContextFactory = sslContextFactory

        val client = Http4kJettyHttpClientService(httpClient)
        val response = client.executeRequest(TestConstants.HTTP_URL)

        assertThat(response.responseBody).isEqualTo("Hello")
        assertThat(response.statusCode).isEqualTo(200)

        verify(sslFactory, times(1)).sslContext
        verify(sslFactory, times(1)).sslParameters
        verify(sslFactory, times(1)).hostnameVerifier
    }

}