package nl.altindag.client.service

import nl.altindag.client.Constants
import nl.altindag.client.model.ClientResponse
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request

abstract class Http4kClientService(val client: HttpHandler): RequestService {

    override fun executeRequest(url: String): ClientResponse {
        val request = Request(Method.GET, url).header(Constants.HEADER_KEY_CLIENT_TYPE, clientType.value)

        val response = client(request)
        return ClientResponse(response.bodyString(), response.status.code)
    }

}