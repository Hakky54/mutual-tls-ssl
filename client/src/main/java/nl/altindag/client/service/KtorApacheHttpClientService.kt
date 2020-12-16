package nl.altindag.client.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.KTOR_APACHE_HTTP_CLIENT
import nl.altindag.ssl.SSLFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class KtorApacheHttpClientService(
        @Autowired(required = false)
        sslFactory: SSLFactory?
): KtorHttpClientService(
        HttpClient(Apache) {
            sslFactory?.let { factory ->
                engine {
                    sslContext = factory.sslContext
                }
            }
        }
) {

    override fun getClientType(): ClientType = KTOR_APACHE_HTTP_CLIENT

}
