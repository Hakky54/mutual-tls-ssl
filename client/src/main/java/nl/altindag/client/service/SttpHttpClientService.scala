package nl.altindag.client.service

import java.net.URI

import javax.net.ssl.HttpsURLConnection
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType._
import nl.altindag.client.model.ClientResponse
import nl.altindag.sslcontext.SSLFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sttp.client._
import sttp.model._

@Service
class SttpHttpClientService(@Autowired(required = false) sslFactory: SSLFactory) extends RequestService {

  implicit val backend: SttpBackend[Identity, Nothing, NothingT] = {
    HttpURLConnectionBackend(customizeConnection = {
      case httpsConnection: HttpsURLConnection if sslFactory != null =>
        httpsConnection.setHostnameVerifier(sslFactory.getHostnameVerifier)
        httpsConnection.setSSLSocketFactory(sslFactory.getSslContext.getSocketFactory)
      case _ =>
    })
  }

  override def executeRequest(url: String): ClientResponse = {
    val request = basicRequest.get(uri = Uri(javaUri = URI.create(url)))
    val response = request.send()

    new ClientResponse(response.body.toOption.orNull, response.code.code)
  }

  override def getClientType: ClientType = STTP

}
