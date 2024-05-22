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

import nl.altindag.client.ClientType
import nl.altindag.client.ClientType._
import nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.model.ClientResponse
import nl.altindag.ssl.SSLFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.{Component, Service}
import sttp.client4.httpurlconnection.HttpURLConnectionBackend
import sttp.client4.{SyncBackend, basicRequest}
import sttp.model._

import java.net.URI
import javax.net.ssl.HttpsURLConnection

@Service
class SttpHttpClientService(sttpBackend: SyncBackend) extends RequestService {

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
  def createSttpBackendClient(sslFactory: SSLFactory): SyncBackend = {
    HttpURLConnectionBackend(customizeConnection = {
      case httpsConnection: HttpsURLConnection =>
        httpsConnection.setHostnameVerifier(sslFactory.getHostnameVerifier)
        httpsConnection.setSSLSocketFactory(sslFactory.getSslSocketFactory)
      case _ =>
    })
  }

}
