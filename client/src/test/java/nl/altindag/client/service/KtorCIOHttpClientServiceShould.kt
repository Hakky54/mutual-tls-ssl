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

import nl.altindag.client.ClientType.KTOR_CIO_HTTP_CLIENT
import nl.altindag.client.TestConstants.HTTP_URL
import nl.altindag.client.util.MockServerTestHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KtorCIOHttpClientServiceShould {

    val keyStorePath = "keystores-for-unit-tests/identity.jks"
    val keyStorePassword = "secret".toCharArray()
    val trustStorePath = "keystores-for-unit-tests/truststore.jks"
    val trustStorePassword = "secret".toCharArray()

    @Test
    fun executeRequest() {
        MockServerTestHelper.mockResponseForClient(KTOR_CIO_HTTP_CLIENT)
        val client = KtorCIOHttpClientService(
                oneWayAuthenticationEnabled = false,
                twoWayAuthenticationEnabled = false,
                keyStorePath = null,
                keyStorePassword = null,
                trustStorePath = null,
                trustStorePassword = null)

        val clientResponse = client.executeRequest(HTTP_URL)

        assertThat(clientResponse.statusCode).isEqualTo(200)
        assertThat(clientResponse.responseBody).isEqualTo("Hello")
    }

    @Test
    fun createClientWithSslMaterialForOneWayAuthentication() {
        val client = KtorCIOHttpClientService(
                oneWayAuthenticationEnabled = true,
                twoWayAuthenticationEnabled = false,
                keyStorePath = keyStorePath,
                keyStorePassword = keyStorePassword,
                trustStorePath = trustStorePath,
                trustStorePassword = trustStorePassword
        )

        assertThat(client).isNotNull
    }

    @Test
    fun createClientWithSslMaterialForTwoWayAuthentication() {
        val client = KtorCIOHttpClientService(
                oneWayAuthenticationEnabled = false,
                twoWayAuthenticationEnabled = true,
                keyStorePath = keyStorePath,
                keyStorePassword = keyStorePassword,
                trustStorePath = trustStorePath,
                trustStorePassword = trustStorePassword
        )

        assertThat(client).isNotNull
    }

}
