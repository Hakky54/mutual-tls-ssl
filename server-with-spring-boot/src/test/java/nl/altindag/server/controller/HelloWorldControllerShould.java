package nl.altindag.server.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import nl.altindag.server.aspect.LogCertificate;
import nl.altindag.server.aspect.LogClientType;

class HelloWorldControllerShould {

    private HelloWorldController victim;

    @BeforeEach
    public void setUp() {
        victim = new HelloWorldController();
    }

    @Test
    void returnHelloMessage() {
        ResponseEntity<String> response = victim.hello();

        assertThat(response.getBody()).isEqualTo("Hello");
    }

    @Test
    void returnStatusCode200() {
        ResponseEntity<String> response = victim.hello();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void annotatedWithLogCertificate() throws NoSuchMethodException {
        Method helloMethod = HelloWorldController.class.getMethod("hello");
        LogCertificate annotation = helloMethod.getAnnotation(LogCertificate.class);

        assertThat(annotation).isNotNull();
    }

    @Test
    void annotatedWithLogClientType() throws NoSuchMethodException {
        Method helloMethod = HelloWorldController.class.getMethod("hello");
        LogClientType annotation = helloMethod.getAnnotation(LogClientType.class);

        assertThat(annotation).isNotNull();
    }

}
