package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4K_APACHE5_ASYNC_HTTP_CLIENT
import nl.altindag.ssl.SSLFactory
import nl.altindag.ssl.util.Apache5SslUtils
import org.apache.hc.client5.http.impl.async.HttpAsyncClients
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder
import org.http4k.client.ApacheAsyncClient
import org.http4k.client.AsyncHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class Http4kApache5AsyncHttpClientService(apacheAsyncHttpClient: AsyncHttpClient) : Http4kAsyncClientService(apacheAsyncHttpClient) {

    override fun getClientType(): ClientType = HTTP4K_APACHE5_ASYNC_HTTP_CLIENT

}

@Component
class Http4kApache5AsyncHttpClientConfiguration {

    @Bean
    fun createClient(@Autowired(required = false) sslFactory: SSLFactory?) : AsyncHttpClient {
        val client = sslFactory?.let { factory ->
            val connectionManager = PoolingAsyncClientConnectionManagerBuilder.create()
                    .setTlsStrategy(Apache5SslUtils.toTlsStrategy(factory))
                    .build()

            HttpAsyncClients.custom()
                    .setConnectionManager(connectionManager)
                    .build()
        } ?: HttpAsyncClients.createDefault()

        client.start()
        return ApacheAsyncClient(client)
    }

}