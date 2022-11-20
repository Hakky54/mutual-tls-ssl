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

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import nl.altindag.client.ClientType.HTTP4K_APACHE4_ASYNC_HTTP_CLIENT
import nl.altindag.client.TestConstants
import nl.altindag.client.util.MockServerTestHelper
import nl.altindag.client.util.SSLFactoryTestHelper
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.awaitility.core.ConditionTimeoutException
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class Http4kApache4AsyncHttpClientServiceShould {

    @Test
    fun executeRequest() {
        MockServerTestHelper.mockResponseForClient(HTTP4K_APACHE4_ASYNC_HTTP_CLIENT)
        val sslFactory = SSLFactoryTestHelper.createSSLFactory(false, true)

        val client = Http4kApache4AsyncHttpClientService(sslFactory)
        val response = client.executeRequest(TestConstants.HTTP_URL)

        assertThat(response.responseBody).isEqualTo("Hello")
        assertThat(response.statusCode).isEqualTo(200)

        verify(sslFactory, times(1)).sslContext
        verify(sslFactory, times(1)).hostnameVerifier
        
        MockServerTestHelper.reset();
    }

    @Test
    fun throwExceptionWhenClientNeedsToWaitMoreThanOneSecondForServerResponse() {
        MockServerTestHelper.mockResponseForClient(HTTP4K_APACHE4_ASYNC_HTTP_CLIENT, TimeUnit.SECONDS, 2)

        val client = Http4kApache4AsyncHttpClientService(SSLFactoryTestHelper.createBasic())

        assertThatThrownBy { client.executeRequest(TestConstants.HTTP_URL) }
            .isInstanceOf(ConditionTimeoutException::class.java)
            
        MockServerTestHelper.reset();
    }

}
