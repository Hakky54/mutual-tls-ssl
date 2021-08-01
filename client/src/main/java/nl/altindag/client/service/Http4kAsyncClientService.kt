package nl.altindag.client.service

import nl.altindag.client.Constants
import nl.altindag.client.model.ClientResponse
import org.awaitility.Awaitility.await
import org.http4k.client.AsyncHttpClient
import org.http4k.core.Method
import org.http4k.core.Request
import java.util.concurrent.TimeUnit

abstract class Http4kAsyncClientService(val client: AsyncHttpClient): RequestService {

    override fun executeRequest(url: String): ClientResponse {
        var response: ClientResponse? = null

        client(Request(Method.GET, url).header(Constants.HEADER_KEY_CLIENT_TYPE, clientType.value)) {
            response = ClientResponse(it.bodyString(), it.status.code)
        }

        // Waiting till the async call finishes
        return await()
            .atMost(500, TimeUnit.MILLISECONDS)
            .until { response != null }
            .let { response }!!
    }

}