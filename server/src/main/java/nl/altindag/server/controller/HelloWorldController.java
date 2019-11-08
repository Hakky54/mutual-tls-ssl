package nl.altindag.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import nl.altindag.server.aspect.LogCertificate;
import nl.altindag.server.aspect.LogClientType;

@Controller
public class HelloWorldController {

    @GetMapping("/api/hello")
    @LogClientType
    @LogCertificate(detailed = true)
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello");
    }

}
