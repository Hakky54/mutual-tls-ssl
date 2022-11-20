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

import cats.effect.{Blocker, ExitCode, IO, IOApp, Resource}
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4S_JAVA_NET_CLIENT
import nl.altindag.ssl.SSLFactory
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.{Component, Service}

import scala.concurrent.ExecutionContext.Implicits.global

@Service
class Http4sJavaNetClientService(@Qualifier("javaNetClient") client: Resource[IO, Client[IO]]) extends Http4sService(client) {

  override def getClientType: ClientType = HTTP4S_JAVA_NET_CLIENT

}

@Component
class JavaNetClientConfiguration extends IOApp {

  @Bean(name = Array("javaNetClient"))
  def createJavaNetClient(sslFactory: SSLFactory): Resource[IO, Client[IO]] = {
    JavaNetClientBuilder[IO](Blocker.liftExecutionContext(global))
      .withSslSocketFactory(sslFactory.getSslSocketFactory)
      .withHostnameVerifier(sslFactory.getHostnameVerifier)
      .resource
  }

  override def run(args: List[String]): IO[ExitCode] = null

}
