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
