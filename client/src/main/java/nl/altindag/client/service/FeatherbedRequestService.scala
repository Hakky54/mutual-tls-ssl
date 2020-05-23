package nl.altindag.client.service

import java.net.{URI, URL}
import java.nio.charset.{Charset, StandardCharsets}
import java.util.concurrent.TimeUnit

import com.twitter.finagle.Http
import com.twitter.finagle.http.Response
import nl.altindag.client.ClientType.FEATHERBED
import nl.altindag.client.Constants.SERVER_URL
import nl.altindag.client.model.ClientResponse
import nl.altindag.client.{ClientType, Constants}
import nl.altindag.sslcontext.SSLFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.{Component, Service}

@Service
class FeatherbedRequestService(client: featherbed.Client) extends RequestService {

  private val TIMEOUT_AMOUNT_IN_SECONDS = 5

  override def executeRequest(url: String): ClientResponse = {
    client.get(URI.create(url).getPath)
      .withHeader(Constants.HEADER_KEY_CLIENT_TYPE, getClientType.getValue)
      .send[Response]()
      .map { response => new ClientResponse(response.getContentString(), response.statusCode) }
      .toJavaFuture
      .get(TIMEOUT_AMOUNT_IN_SECONDS, TimeUnit.SECONDS)
  }

  override def getClientType: ClientType = FEATHERBED

}

@Component
class FeatherbedClientConfig {

  @Bean
  def createFeatherbedClient(@Autowired(required = false) sslFactory: SSLFactory): featherbed.Client = {
    new CustomizedFeatherbedClient(sslFactory, new URI(SERVER_URL).toURL, StandardCharsets.UTF_8)
  }

}

class CustomizedFeatherbedClient(sslFactory: SSLFactory, val baseUrl: URL, val charset: Charset) extends featherbed.Client(baseUrl, charset) {

  override protected def clientTransform(client: Http.Client): Http.Client = {
    if (sslFactory != null) {
      super.clientTransform(client)
          .withTransport
          .tls(sslFactory.getSslContext)
    } else {
      super.clientTransform(client)
    }
  }

}
