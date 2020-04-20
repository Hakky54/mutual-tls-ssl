package nl.altindag.client.service

import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.httpGet
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.FUEL
import nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.model.ClientResponse
import nl.altindag.sslcontext.SSLFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FuelService(@Autowired(required = false) sslFactory: SSLFactory?): RequestService {

    init {
        if (sslFactory != null) {
            FuelManager.instance.hostnameVerifier = sslFactory.hostnameVerifier
            FuelManager.instance.socketFactory = sslFactory.sslContext.socketFactory
        }
    }

    override fun executeRequest(url: String): ClientResponse {
        val responseString = url.httpGet()
                .header(HEADER_KEY_CLIENT_TYPE, clientType.value)
                .responseString()

        return ClientResponse(responseString.third.component1(), responseString.second.statusCode)
    }

    override fun getClientType(): ClientType = FUEL

}