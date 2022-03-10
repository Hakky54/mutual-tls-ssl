package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4K_JAVA_HTTP_CLIENT
import nl.altindag.ssl.SSLFactory
import org.http4k.client.JavaHttpClient
import org.springframework.stereotype.Service
import java.net.http.HttpClient

@Service
class Http4kJavaHttpClientService(
    sslFactory: SSLFactory
) : Http4kClientService(
    JavaHttpClient(
        httpClient = HttpClient.newBuilder()
            .sslParameters(sslFactory.sslParameters)
            .sslContext(sslFactory.sslContext)
            .build()
    )
) {

    override fun getClientType(): ClientType = HTTP4K_JAVA_HTTP_CLIENT

}