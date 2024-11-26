/*
 * Copyright 2018 Thunderberry.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.altindag.client.service

import com.twitter.finagle.http.{Request, RequestBuilder, Response}
import com.twitter.finagle.ssl.client.SslClientConfiguration
import com.twitter.finagle.ssl.{KeyCredentials, TrustCredentials}
import com.twitter.finagle.{Http, Service}
import nl.altindag.client.ClientType.FINAGLE
import nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.model.ClientResponse
import nl.altindag.client.{ClientType, Constants}
import nl.altindag.ssl.SSLFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.stereotype
import org.springframework.stereotype.Component

import java.net.URI
import java.util.concurrent.TimeUnit
import scala.jdk.javaapi.OptionConverters

@stereotype.Service
class FinagleHttpClientService2(@Qualifier("finagleClient") service: Service[Request, Response]) extends RequestService {

  private val TIMEOUT_AMOUNT_IN_SECONDS = 5

  override def executeRequest(url: String): ClientResponse = {
    val request = RequestBuilder()
      .addHeader(HEADER_KEY_CLIENT_TYPE, getClientType.getValue)
      .url(url)
      .buildGet()

    service.apply(request)
      .map(response => new ClientResponse(response.contentString, response.statusCode))
      .toJavaFuture
      .get(TIMEOUT_AMOUNT_IN_SECONDS, TimeUnit.SECONDS)
  }

  override def getClientType: ClientType = FINAGLE
}

@Component
class FinagleHttpClientConfiguration {

  @Bean(name = Array("finagleClient"))
  def createFinagle(sslFactory: SSLFactory): Service[Request, Response] = {
    val uri = new URI(Constants.getServerUrl)
    var client = Http.client.withNoHttp2

    if (uri.getScheme == "https") {
      val sslClientConfiguration = SslClientConfiguration(
        keyCredentials = OptionConverters.toScala(sslFactory.getKeyManagerFactory)
          .map(kmf => KeyCredentials.KeyManagerFactory(kmf))
          .getOrElse(KeyCredentials.Unspecified),
        trustCredentials = OptionConverters.toScala(sslFactory.getTrustManagerFactory)
          .map(tmf => TrustCredentials.TrustManagerFactory(tmf))
          .getOrElse(TrustCredentials.Unspecified)
      )

      client = client.withTransport.tls(sslClientConfiguration)
    }

    client.newService(uri.getHost + ":" + uri.getPort)
  }

}
