package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4K_APACHE5_ASYNC_HTTP_CLIENT
import nl.altindag.ssl.SSLFactory
import nl.altindag.ssl.util.Apache5SslUtils
import org.apache.hc.client5.http.impl.async.HttpAsyncClients
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder
import org.http4k.client.ApacheAsyncClient
import org.springframework.stereotype.Service

@Service
class Http4kApache5AsyncHttpClientService(
    sslFactory: SSLFactory
) : Http4kAsyncClientService(ApacheAsyncClient(
    client = sslFactory.let { factory ->
        val connectionManager = PoolingAsyncClientConnectionManagerBuilder.create()
            .setTlsStrategy(Apache5SslUtils.toTlsStrategy(factory))
            .build()

        HttpAsyncClients.custom()
            .setConnectionManager(connectionManager)
            .build()
    }
)) {

    override fun getClientType(): ClientType = HTTP4K_APACHE5_ASYNC_HTTP_CLIENT

}