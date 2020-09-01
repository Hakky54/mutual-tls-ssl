package nl.altindag.server.controller;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HelloWorldControllerShould {

    private static HelloWorldController victim;

    @BeforeAll
    static void setUp() {
        victim = new HelloWorldController();
    }

    @Test
    void returnHelloMessage() {
        Response response = victim.hello();

        assertThat(response.getEntity()).isEqualTo("Hello");
    }

    @Test
    void returnStatusCode200() {
        Response response = victim.hello();

        assertThat(response.getStatus()).isEqualTo(200);
    }

}
