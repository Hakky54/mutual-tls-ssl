package nl.altindag.server.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class HelloWorldControllerTest {

    private HelloWorldController victim;

    @Before
    public void setUp() {
        victim = new HelloWorldController();
    }

    @Test
    public void shouldReturnHelloMessage() {
        ResponseEntity<String> response = victim.hello();

        assertThat(response.getBody()).isEqualTo("Hello");
    }

    @Test
    public void shouldReturnStatusCode200() {
        ResponseEntity<String> response = victim.hello();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

}
