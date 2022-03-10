package nl.altindag.client.service

import java.net.URI

import javax.net.ssl.HttpsURLConnection
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType._
import nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.model.ClientResponse
import nl.altindag.ssl.SSLFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.{Component, Service}
import sttp.client._
import sttp.model._

@Service
class SttpHttpClientService(sttpBackend: SttpBackend[Identity, Any]) extends RequestService {

  override def executeRequest(url: String): ClientResponse = {
    val request = basicRequest.get(uri = Uri(javaUri = URI.create(url)))
                              .header(HEADER_KEY_CLIENT_TYPE, getClientType.getValue)

    val response = request.send(sttpBackend)
    new ClientResponse(response.body.toOption.orNull, response.code.code)
  }

  override def getClientType: ClientType = STTP

}

@Component
class SttpHttpClientConfiguration {

  @Bean
  def createSttpBackendClient(sslFactory: SSLFactory): SttpBackend[Identity, Any] = {
    HttpURLConnectionBackend(customizeConnection = {
      case httpsConnection: HttpsURLConnection =>
        httpsConnection.setHostnameVerifier(sslFactory.getHostnameVerifier)
        httpsConnection.setSSLSocketFactory(sslFactory.getSslSocketFactory)
      case _ =>
    })
  }

}
