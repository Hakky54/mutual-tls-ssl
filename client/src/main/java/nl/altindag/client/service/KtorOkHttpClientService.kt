package nl.altindag.client.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.KTOR_OK_HTTP
import nl.altindag.ssl.SSLFactory
import org.springframework.stereotype.Service

@Service
class KtorOkHttpClientService(
    sslFactory: SSLFactory
) : KtorHttpClientService(
    HttpClient(OkHttp) {
        engine {
            config {
                sslSocketFactory(sslFactory.sslSocketFactory, sslFactory.trustManager.orElseThrow())
                hostnameVerifier(sslFactory.hostnameVerifier)
            }
        }
    }
) {

    override fun getClientType(): ClientType = KTOR_OK_HTTP

}
