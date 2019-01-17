package nl.altindag.server.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

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
        ResponseEntity response = victim.hello();

        assertThat(response.getBody(), equalTo("Hello"));
    }

    @Test
    public void shouldReturnStatusCode200() {
        ResponseEntity response = victim.hello();

        assertThat(response.getStatusCode().value(), equalTo(200));
    }

}
