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

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.FUEL
import nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.model.ClientResponse
import nl.altindag.ssl.SSLFactory
import org.springframework.stereotype.Service

@Service
@Suppress("UNUSED_VARIABLE")
class FuelService(sslFactory: SSLFactory): RequestService {

    init {
        FuelManager.instance.hostnameVerifier = sslFactory.hostnameVerifier
        FuelManager.instance.socketFactory = sslFactory.sslSocketFactory
    }

    override fun executeRequest(url: String): ClientResponse {
        val (request, response, result) = url.httpGet()
                .header(HEADER_KEY_CLIENT_TYPE, clientType.value)
                .responseString()

        return ClientResponse(result.get(), response.statusCode)
    }

    override fun getClientType(): ClientType = FUEL

}
