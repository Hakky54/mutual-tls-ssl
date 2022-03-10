package nl.altindag.client.service

import javax.net.ssl.HttpsURLConnection
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.SCALAJ_HTTP_CLIENT
import nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.model.ClientResponse
import nl.altindag.ssl.SSLFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.{Component, Service}
import scalaj.http.Http
import scalaj.http.HttpOptions.HttpOption

@Service
class ScalaJHttpClientService(httpOption: HttpOption) extends RequestService {

  override def executeRequest(url: String): ClientResponse = {
    val response = Http(url)
      .method("GET")
      .header(HEADER_KEY_CLIENT_TYPE, getClientType.getValue)
      .option(httpOption)
      .asString

    new ClientResponse(response.body, response.code)
  }

  override def getClientType: ClientType = SCALAJ_HTTP_CLIENT

}

@Component
class ScalaJHttpClientConfiguration {

  @Bean
  def createHttpOption(sslFactory: SSLFactory): HttpOption = {
    case httpsURLConnection: HttpsURLConnection =>
      httpsURLConnection.setHostnameVerifier(sslFactory.getHostnameVerifier)
      httpsURLConnection.setSSLSocketFactory(sslFactory.getSslSocketFactory)
    case _ =>
  }

}
