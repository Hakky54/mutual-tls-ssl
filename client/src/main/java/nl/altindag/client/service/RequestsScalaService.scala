package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.model.ClientResponse
import nl.altindag.ssl.SSLFactory
import org.springframework.stereotype.Service

@Service
class RequestsScalaService(sslFactory: SSLFactory) extends RequestService {

  override def executeRequest(url: String): ClientResponse = {
    val response = requests.get(
      url,
      headers = Map(HEADER_KEY_CLIENT_TYPE -> getClientType.getValue),
      sslContext = sslFactory.getSslContext
    )

    new ClientResponse(response.text(), response.statusCode)
  }

  override def getClientType: ClientType = ClientType.REQUESTS_SCALA

}
