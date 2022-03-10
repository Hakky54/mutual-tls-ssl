package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4K_OK_HTTP_CLIENT
import nl.altindag.ssl.SSLFactory
import okhttp3.OkHttpClient
import org.http4k.client.OkHttp
import org.springframework.stereotype.Service

@Service
class Http4kOkHttpClientService(
    sslFactory: SSLFactory
) : Http4kClientService(
    OkHttp(
        client = OkHttpClient().newBuilder()
            .sslSocketFactory(sslFactory.sslSocketFactory, sslFactory.trustManager.orElseThrow())
            .hostnameVerifier(sslFactory.hostnameVerifier)
            .build()
    )
) {

    override fun getClientType(): ClientType = HTTP4K_OK_HTTP_CLIENT

}