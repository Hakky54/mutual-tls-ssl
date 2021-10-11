package nl.altindag.client.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.KTOR_JAVA_HTTP_CLIENT
import nl.altindag.ssl.SSLFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class KtorJavaHttpClientService(
        @Autowired(required = false)
        sslFactory: SSLFactory?
): KtorHttpClientService(
        HttpClient(Java) {
            sslFactory?.let { factory ->
                engine {
                    config {
                        sslContext(factory.sslContext)
                        sslParameters(factory.sslParameters)
                    }
                }
            }
        }
) {

    override fun getClientType(): ClientType = KTOR_JAVA_HTTP_CLIENT

}
