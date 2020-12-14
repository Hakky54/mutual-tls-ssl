package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4K_JAVA_HTTP_CLIENT
import nl.altindag.sslcontext.SSLFactory
import org.http4k.client.JavaHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.net.http.HttpClient

@Service
class Http4kJavaHttpClientService(
        @Autowired(required = false)
        sslFactory: SSLFactory?
) : Http4kClientService(
        JavaHttpClient(
                httpClient = sslFactory?.let { factory ->
                    HttpClient.newBuilder()
                            .sslParameters(factory.sslParameters)
                            .sslContext(factory.sslContext)
                            .build()
                } ?: HttpClient.newHttpClient()
        )
) {

    override fun getClientType(): ClientType = HTTP4K_JAVA_HTTP_CLIENT

}