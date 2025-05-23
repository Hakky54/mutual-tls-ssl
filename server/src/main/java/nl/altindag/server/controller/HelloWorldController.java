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
package nl.altindag.server.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import nl.altindag.server.aspect.LogCertificate;
import nl.altindag.server.aspect.LogClientType;
import nl.altindag.server.aspect.LogFromHeader;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class HelloWorldController {

    @LogFromHeader
    @LogClientType
    @LogCertificate(detailed = true)
    @GetMapping(value = "/api/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> hello(@RequestHeader(name = HttpHeaders.FROM, required = false) String from) {
        return from == null ? ResponseEntity.ok("Hello") : ResponseEntity.ok(String.format("Hello %s!", from));
    }

}
