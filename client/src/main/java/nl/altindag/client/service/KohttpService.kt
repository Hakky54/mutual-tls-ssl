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

import io.github.rybalkinsd.kohttp.client.client
import io.github.rybalkinsd.kohttp.configuration.SslConfig
import io.github.rybalkinsd.kohttp.dsl.httpGet
import io.github.rybalkinsd.kohttp.ext.asString
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.KOHTTP
import nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.model.ClientResponse
import nl.altindag.ssl.SSLFactory
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.net.URI

@Service
class KohttpService(
    @Qualifier("kohttp")
    private val client: OkHttpClient
) : RequestService {

    override fun executeRequest(url: String): ClientResponse {
        val uri = URI.create(url)

        return httpGet(client) {
            host = uri.host
            port = uri.port
            path = uri.path
            scheme = uri.scheme

            header {
                HEADER_KEY_CLIENT_TYPE to clientType.value
            }
        }.use { response ->
            ClientResponse(response.asString(), response.code)
        }
    }

    override fun getClientType(): ClientType = KOHTTP

}

@Component
class KohttpClientConfig {

    @Bean("kohttp")
    fun createKohttpClient(sslFactory: SSLFactory): OkHttpClient {
        return client {
            sslConfig = SslConfig().apply {
                sslSocketFactory = sslFactory.sslSocketFactory
                trustManager = sslFactory.trustManager.orElseThrow()
                hostnameVerifier = sslFactory.hostnameVerifier
            }
        }
    }

}
