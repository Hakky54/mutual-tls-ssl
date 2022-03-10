package nl.altindag.client.service

import dispatch.Defaults._
import dispatch._
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.DISPATCH_REBOOT_HTTP_CLIENT
import nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.model.ClientResponse
import nl.altindag.ssl.SSLFactory
import nl.altindag.ssl.util.NettySslUtils
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.{Component, Service}

import scala.language.postfixOps

@Service
class DispatchRebootService(httpClient: Http) extends RequestService {

  override def executeRequest(uri: String): ClientResponse = {
    val request: Req = url(uri).GET addHeader (HEADER_KEY_CLIENT_TYPE, getClientType.getValue)

    httpClient(request)
      .map(response => new ClientResponse(response.getResponseBody, response.getStatusCode))
      .apply()
  }

  override def getClientType: ClientType = DISPATCH_REBOOT_HTTP_CLIENT

}

@Component
class DispatchRebootHttpClientConfig {

  @Bean
  def createDispatchRebootHttpClient(sslFactory: SSLFactory): Http = {
    val sslContext = NettySslUtils.forClient(sslFactory).build
    Http.withConfiguration(builder => builder.setSslContext(sslContext))
  }

}
