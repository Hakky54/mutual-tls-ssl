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

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.*
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.runBlocking
import nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.model.ClientResponse

abstract class KtorHttpClientService(var client: HttpClient): RequestService {

    override fun executeRequest(url: String): ClientResponse {
        return runBlocking {
            val httpResponse: HttpResponse = client.get(url) {
                header(HEADER_KEY_CLIENT_TYPE, clientType.value)
            }

            ClientResponse(httpResponse.bodyAsText(), httpResponse.status.value)
        }
    }

}
