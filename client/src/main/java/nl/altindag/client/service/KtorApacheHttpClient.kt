package nl.altindag.client.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.runBlocking
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.KTOR_APACHE_HTTP_CLIENT
import nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.model.ClientResponse
import nl.altindag.sslcontext.SSLFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class KtorApacheHttpClient (@Autowired(required = false) sslFactory: SSLFactory?) : RequestService {

    val client = HttpClient(Apache) {
        if (sslFactory != null) {
            engine {
                sslContext = sslFactory.sslContext
            }
        }
    }

    override fun executeRequest(url: String): ClientResponse {
        return runBlocking {
            val httpResponse: HttpResponse = client.get(url) {
                header(HEADER_KEY_CLIENT_TYPE, clientType.value)
            }

            ClientResponse(httpResponse.content.readUTF8Line(), httpResponse.status.value)
        }
    }

    override fun getClientType(): ClientType = KTOR_APACHE_HTTP_CLIENT

}