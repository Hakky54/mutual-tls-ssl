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

import dispatch.Defaults._
import dispatch._
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.DISPATCH_REBOOT_HTTP_CLIENT
import nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.model.ClientResponse
import nl.altindag.ssl.SSLFactory
import nl.altindag.ssl.netty.util.NettySslUtils
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
