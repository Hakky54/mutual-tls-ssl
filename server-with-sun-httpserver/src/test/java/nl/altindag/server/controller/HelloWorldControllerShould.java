package nl.altindag.server.controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HelloWorldControllerShould {

    private static HelloWorldController victim;

    @BeforeAll
    static void setUp() {
        victim = new HelloWorldController();
    }

    @Test
    void returnHelloMessage() throws IOException {
        HttpExchange httpExchange = spy(HttpExchange.class);
        OutputStream outputStream = new ByteArrayOutputStream();
        Headers headers = spy(Headers.class);

        when(httpExchange.getResponseBody()).thenReturn(outputStream);
        when(httpExchange.getResponseHeaders()).thenReturn(headers);

        victim.handle(httpExchange);
        String body = outputStream.toString();

        assertThat(body).isEqualTo("Hello");
    }

    @Test
    void returnStatusCode200() throws IOException {
        HttpExchange httpExchange = spy(HttpExchange.class);
        OutputStream outputStream = new ByteArrayOutputStream();
        Headers headers = spy(Headers.class);

        when(httpExchange.getResponseBody()).thenReturn(outputStream);
        when(httpExchange.getResponseHeaders()).thenReturn(headers);

        ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        victim.handle(httpExchange);

        verify(httpExchange, times(1)).sendResponseHeaders(integerArgumentCaptor.capture(), anyLong());

        assertThat(integerArgumentCaptor.getValue()).isEqualTo(200);
    }

}
