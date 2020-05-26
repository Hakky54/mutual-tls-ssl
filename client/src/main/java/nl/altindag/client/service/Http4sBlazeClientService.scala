package nl.altindag.client.service

import java.util.Objects.nonNull
import cats.effect.{ExitCode, IO, IOApp, Resource}
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4S_BLAZE_CLIENT
import nl.altindag.sslcontext.SSLFactory
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.springframework.beans.factory.annotation.{Autowired, Qualifier}
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
  def createBlazeClient(@Autowired(required = false) sslFactory: SSLFactory): Resource[IO, Client[IO]] = {
    if (nonNull(sslFactory)) {
      BlazeClientBuilder[IO](global)
        .withSslContext(sslFactory.getSslContext)
        .resource
    } else {
      BlazeClientBuilder[IO](global)
        .resource
    }
  }

  override def run(args: List[String]): IO[ExitCode] = null

}
