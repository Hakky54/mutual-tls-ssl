package nl.altindag.server.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import nl.altindag.server.aspect.LogCertificate;
import nl.altindag.server.aspect.LogClientType;

@Controller
public class HelloWorldController {

    @LogClientType
    @LogCertificate(detailed = true)
    @GetMapping(value = "/api/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello");
    }

}
