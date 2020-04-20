package nl.altindag.client.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.KTOR_APACHE_HTTP_CLIENT
import nl.altindag.sslcontext.SSLFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class KtorApacheHttpClient(@Autowired(required = false) sslFactory: SSLFactory?) : KtorHttpClientService(HttpClient(Apache) {
    if (sslFactory != null) {
        engine {
            sslContext = sslFactory.sslContext
        }
    }
}) {

    override fun getClientType(): ClientType = KTOR_APACHE_HTTP_CLIENT

}