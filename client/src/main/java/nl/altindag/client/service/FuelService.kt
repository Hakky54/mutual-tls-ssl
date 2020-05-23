package nl.altindag.client.service

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.FUEL
import nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.model.ClientResponse
import nl.altindag.sslcontext.SSLFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Suppress("UNUSED_VARIABLE")
class FuelService(
        @Autowired(required = false) sslFactory: SSLFactory?
): RequestService {

    init {
        sslFactory?.let { factory ->
            FuelManager.instance.hostnameVerifier = factory.hostnameVerifier
            FuelManager.instance.socketFactory = factory.sslContext.socketFactory
        }
    }

    override fun executeRequest(url: String): ClientResponse {
        val (request, response, result) = url.httpGet()
                .header(HEADER_KEY_CLIENT_TYPE, clientType.value)
                .responseString()

        return ClientResponse(result.get(), response.statusCode)
    }

    override fun getClientType(): ClientType = FUEL

}