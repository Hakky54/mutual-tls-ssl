package nl.altindag.client.service

import nl.altindag.client.Constants
import nl.altindag.client.model.ClientResponse
import org.http4k.client.AsyncHttpClient
import org.http4k.core.Method
import org.http4k.core.Request
import java.util.concurrent.TimeUnit

abstract class Http4kAsyncClientService(val client: AsyncHttpClient): RequestService {

    lateinit var response: ClientResponse

    override fun executeRequest(url: String): ClientResponse {
        client(Request(Method.GET, url).header(Constants.HEADER_KEY_CLIENT_TYPE, clientType.value)) {
            response = ClientResponse(it.bodyString(), it.status.code)
        }

        var counter = 0
        do {
            TimeUnit.MILLISECONDS.sleep(100)
            counter = counter.inc()
        } while (!this::response.isInitialized && counter < 20)

        return response
    }

}