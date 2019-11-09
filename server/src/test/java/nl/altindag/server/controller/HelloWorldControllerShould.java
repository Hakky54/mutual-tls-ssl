package nl.altindag.server.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import nl.altindag.server.aspect.LogCertificate;
import nl.altindag.server.aspect.LogClientType;

public class HelloWorldControllerShould {

    private HelloWorldController victim;

    @Before
    public void setUp() {
        victim = new HelloWorldController();
    }

    @Test
    public void returnHelloMessage() {
        ResponseEntity<String> response = victim.hello();

        assertThat(response.getBody()).isEqualTo("Hello");
    }

    @Test
    public void returnStatusCode200() {
        ResponseEntity<String> response = victim.hello();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    public void annotatedWithLogCertificate() throws NoSuchMethodException {
        Method helloMethod = HelloWorldController.class.getMethod("hello");
        LogCertificate annotation = helloMethod.getAnnotation(LogCertificate.class);

        assertThat(annotation).isNotNull();
    }

    @Test
    public void annotatedWithLogClientType() throws NoSuchMethodException {
        Method helloMethod = HelloWorldController.class.getMethod("hello");
        LogClientType annotation = helloMethod.getAnnotation(LogClientType.class);

        assertThat(annotation).isNotNull();
    }

}
