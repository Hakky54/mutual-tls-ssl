package nl.altindag.client.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.runBlocking
import nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.model.ClientResponse

abstract class KtorHttpClientService(var client: HttpClient): RequestService {

    override fun executeRequest(url: String): ClientResponse {
        return runBlocking {
            val httpResponse: HttpResponse = client.get(url) {
                header(HEADER_KEY_CLIENT_TYPE, clientType.value)
            }

            ClientResponse(httpResponse.content.readUTF8Line(), httpResponse.status.value)
        }
    }

}
