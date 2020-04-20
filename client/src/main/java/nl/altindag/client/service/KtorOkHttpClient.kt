package nl.altindag.client.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.KTOR_OK_HTTP
import nl.altindag.sslcontext.SSLFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class KtorOkHttpClient(@Autowired(required = false) sslFactory: SSLFactory?) : KtorHttpClientService(HttpClient(OkHttp) {
    if (sslFactory != null) {
        engine {
            config {
                sslSocketFactory(sslFactory.sslContext.socketFactory, sslFactory.trustManager)
                hostnameVerifier(sslFactory.hostnameVerifier)
            }
        }
    }
}) {

    override fun getClientType(): ClientType = KTOR_OK_HTTP

}