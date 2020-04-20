package nl.altindag.client.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.util.KtorExperimentalAPI
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.KTOR_CIO_HTTP_CLIENT
import nl.altindag.sslcontext.SSLFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@KtorExperimentalAPI
class KtorCioHttpClient(@Autowired(required = false) sslFactory: SSLFactory?): KtorHttpClientService(HttpClient(CIO) {
    if (sslFactory != null) {
        engine {
            https {
                //todo missing support for sslcontext
                //todo missing support for keymanager
                trustManager = sslFactory.trustManager
            }
        }
    }
}) {

    override fun getClientType(): ClientType = KTOR_CIO_HTTP_CLIENT

}