package nl.altindag.client.service

import cats.effect.{ExitCode, IO, IOApp, Resource}
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4S_BLAZE_CLIENT
import nl.altindag.ssl.SSLFactory
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.{Component, Service}

import scala.concurrent.ExecutionContext.Implicits.global

@Service
class Http4sBlazeClientService(@Qualifier("blazeClient") client: Resource[IO, Client[IO]]) extends Http4sService(client) {

  override def getClientType: ClientType = HTTP4S_BLAZE_CLIENT

}

@Component
class BlazeClientConfiguration extends IOApp {

  @Bean(name = Array("blazeClient"))
  def createBlazeClient(sslFactory: SSLFactory): Resource[IO, Client[IO]] = {
      BlazeClientBuilder[IO](global)
        .withSslContext(sslFactory.getSslContext)
        .resource
  }

  override def run(args: List[String]): IO[ExitCode] = null

}
