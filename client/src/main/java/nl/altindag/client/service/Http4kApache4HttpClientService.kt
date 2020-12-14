package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4K_APACHE4_HTTP_CLIENT
import nl.altindag.sslcontext.SSLFactory
import nl.altindag.sslcontext.util.ApacheSslContextUtils
import org.apache.http.impl.client.HttpClients
import org.http4k.client.Apache4Client
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Http4kApache4HttpClientService(
        @Autowired(required = false)
        sslFactory: SSLFactory?
) : Http4kClientService(
        Apache4Client(
                client = sslFactory?.let {
                    HttpClients.custom()
                            .setSSLSocketFactory(ApacheSslContextUtils.toSocketFactory(it))
                            .build()
                } ?: HttpClients.createDefault()
        )
) {

    override fun getClientType(): ClientType = HTTP4K_APACHE4_HTTP_CLIENT

}