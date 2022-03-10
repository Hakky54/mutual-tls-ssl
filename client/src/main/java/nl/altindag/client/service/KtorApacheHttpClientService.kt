package nl.altindag.client.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.KTOR_APACHE_HTTP_CLIENT
import nl.altindag.ssl.SSLFactory
import org.springframework.stereotype.Service

@Service
class KtorApacheHttpClientService(
    sslFactory: SSLFactory
) : KtorHttpClientService(
    HttpClient(Apache) {
        engine {
            sslContext = sslFactory.sslContext
        }
    }
) {

    override fun getClientType(): ClientType = KTOR_APACHE_HTTP_CLIENT

}
