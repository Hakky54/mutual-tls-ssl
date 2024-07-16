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

import nl.altindag.client.Constants
import nl.altindag.client.model.ClientResponse
import org.awaitility.Awaitility.await
import org.http4k.client.AsyncHttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import java.util.concurrent.TimeUnit

abstract class Http4kAsyncClientService(val client: AsyncHttpHandler): RequestService {

    override fun executeRequest(url: String): ClientResponse {
        var response: ClientResponse? = null

        client(Request(Method.GET, url).header(Constants.HEADER_KEY_CLIENT_TYPE, clientType.value)) {
            response = ClientResponse(it.bodyString(), it.status.code)
        }

        // Waiting till the async call finishes
        return await()
            .atMost(500, TimeUnit.MILLISECONDS)
            .until { response != null }
            .let { response }!!
    }

}