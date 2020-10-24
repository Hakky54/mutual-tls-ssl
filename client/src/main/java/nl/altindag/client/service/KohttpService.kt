package nl.altindag.client.service

import io.github.rybalkinsd.kohttp.client.client
import io.github.rybalkinsd.kohttp.client.defaultHttpClient
import io.github.rybalkinsd.kohttp.configuration.SslConfig
import io.github.rybalkinsd.kohttp.dsl.httpGet
import io.github.rybalkinsd.kohttp.ext.asString
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.KOHTTP
import nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.model.ClientResponse
import nl.altindag.sslcontext.SSLFactory
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.net.URI

@Service
class KohttpService (
        @Qualifier("kohttp")
        private val client: OkHttpClient
): RequestService {

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
    fun createKohttpClient(@Autowired(required = false) sslFactory: SSLFactory?) : OkHttpClient {
        return sslFactory?.let { factory ->
            client {
                connectionSpecs = listOf(
                        ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).apply {
                            cipherSuites(*factory.sslParameters.cipherSuites)
                            tlsVersions(*factory.sslParameters.protocols)
                        }.build()
                )

                sslConfig = SslConfig().apply {
                    sslSocketFactory = factory.sslContext.socketFactory
                    trustManager = factory.trustManager.orElseThrow()
                    hostnameVerifier = factory.hostnameVerifier
                }
            }
        } ?: defaultHttpClient
    }

}
