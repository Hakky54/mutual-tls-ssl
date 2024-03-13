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

import nl.altindag.client.ClientType.KTOR_JAVA_HTTP_CLIENT
import nl.altindag.client.TestConstants.HTTP_URL
import nl.altindag.client.TestConstants.HTTPS_URL
import nl.altindag.client.util.MockServerTestHelper
import nl.altindag.client.util.SSLFactoryTestHelper
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class KtorJavaHttpClientServiceShould {

    @Test
    fun executeRequest() {
        MockServerTestHelper.mockResponseForClient(KTOR_JAVA_HTTP_CLIENT)
        val client = KtorJavaHttpClientService(SSLFactoryTestHelper.createBasic())

        val clientResponse = client.executeRequest(HTTP_URL)

        assertThat(clientResponse.statusCode).isEqualTo(200)
        assertThat(clientResponse.responseBody).isEqualTo("Hello")
    }

    @Test
    fun createClientWithSslMaterial() {
        val sslFactory = SSLFactoryTestHelper.createSSLFactory(true, true)

        val client = KtorJavaHttpClientService(sslFactory)

        assertThatThrownBy { client.executeRequest(HTTPS_URL) }

        verify(sslFactory, times(1)).sslContext
        verify(sslFactory, times(1)).sslParameters
    }

}
