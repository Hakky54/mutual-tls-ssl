package nl.altindag.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloWorldController {

    @GetMapping("/api/hello")
    public ResponseEntity hello() {
        return ResponseEntity.ok("Hello");
    }

}
