package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4K_APACHE5_ASYNC_HTTP_CLIENT
import nl.altindag.sslcontext.SSLFactory
import org.apache.hc.client5.http.impl.async.HttpAsyncClients
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder
import org.apache.hc.core5.http.nio.ssl.BasicClientTlsStrategy
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
            HttpAsyncClients.custom()
                    .setConnectionManager(
                            PoolingAsyncClientConnectionManagerBuilder.create()
                                    .setTlsStrategy(BasicClientTlsStrategy(factory.sslContext))
                                    .build())
                    .build()
        } ?: HttpAsyncClients.createDefault()

        client.start()
        return ApacheAsyncClient(client)
    }

}