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

import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4K_APACHE5_HTTP_CLIENT
import nl.altindag.ssl.SSLFactory
import nl.altindag.ssl.apache5.util.Apache5SslUtils
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.http4k.client.ApacheClient
import org.springframework.stereotype.Service

@Service
class Http4kApache5HttpClientService(
    sslFactory: SSLFactory
) : Http4kClientService(
    ApacheClient(
        client = sslFactory.let { factory ->
            val socketFactory = Apache5SslUtils.toSocketFactory(factory)
            val connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(socketFactory)
                .build()

            HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build()
        }
    )
) {

    override fun getClientType(): ClientType = HTTP4K_APACHE5_HTTP_CLIENT

}