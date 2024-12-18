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
        ResponseEntity<String> response = victim.hello(null);

        assertThat(response.getBody()).isEqualTo("Hello");
    }

    @Test
    void returnHelloWithFromHeaderValueMessage() {
        ResponseEntity<String> response = victim.hello("Foo");

        assertThat(response.getBody()).isEqualTo("Hello Foo!");
    }

    @Test
    void returnStatusCode200() {
        ResponseEntity<String> response = victim.hello(null);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void annotatedWithLogCertificate() throws NoSuchMethodException {
        Method helloMethod = HelloWorldController.class.getMethod("hello", String.class);
        LogCertificate annotation = helloMethod.getAnnotation(LogCertificate.class);

        assertThat(annotation).isNotNull();
    }

    @Test
    void annotatedWithLogClientType() throws NoSuchMethodException {
        Method helloMethod = HelloWorldController.class.getMethod("hello", String.class);
        LogClientType annotation = helloMethod.getAnnotation(LogClientType.class);

        assertThat(annotation).isNotNull();
    }

}
