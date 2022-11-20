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

import cats.effect.{IO, Resource}
import nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE
import nl.altindag.client.model.ClientResponse
import org.http4s._
import org.http4s.client.Client

abstract class Http4sService(client: Resource[IO, Client[IO]]) extends RequestService {

  override def executeRequest(url: String): ClientResponse = {
    val request: Request[IO] = Request(
      method = Method.GET,
      uri = Uri.fromString(url).toOption.get,
      headers = Headers.of(Header(HEADER_KEY_CLIENT_TYPE, getClientType.getValue))
    )

    val responseBody = client
      .use(client => client.expect[String](request))
      .unsafeRunSync()

    // the client will throw a runtime exception for any other status code than 2xx
    // therefore it won't even reach to this point if it gets a non 2xx status code.
    // If it is getting this far it is safe to assume that the status code is 200
    new ClientResponse(responseBody, 200)
  }

}
