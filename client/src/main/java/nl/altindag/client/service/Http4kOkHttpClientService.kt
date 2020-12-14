package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4K_OK_HTTP_CLIENT
import nl.altindag.sslcontext.SSLFactory
import okhttp3.OkHttpClient
import org.http4k.client.OkHttp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Http4kOkHttpClientService(
        @Autowired(required = false)
        sslFactory: SSLFactory?
) : Http4kClientService(
        OkHttp(
                client = sslFactory?.let { factory ->
                    OkHttpClient().newBuilder()
                            .sslSocketFactory(factory.sslSocketFactory, factory.trustManager.orElseThrow())
                            .hostnameVerifier(factory.hostnameVerifier)
                            .build()
                } ?: OkHttpClient()
        )
) {

    override fun getClientType(): ClientType = HTTP4K_OK_HTTP_CLIENT

}