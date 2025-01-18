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
package nl.altindag.server.aspect;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LogFromAspectShould {

    private final LogFromHeaderAspect logFromHeaderAspect = new LogFromHeaderAspect();

    @Test
    void logFromHeaderIfPresent() {
        LogCaptor logCaptor = LogCaptor.forClass(LogFromHeaderAspect.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("from", "Foo");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        logFromHeaderAspect.logIfPresent();

        List<String> logs = logCaptor.getLogs();
        assertThat(logs).containsExactly("Hello there! It seems like Foo is knocking on my door...");
    }

    @Test
    void notLogFromHeaderIfAbsent() {
        LogCaptor logCaptor = LogCaptor.forClass(LogFromHeaderAspect.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        logFromHeaderAspect.logIfPresent();

        List<String> logs = logCaptor.getLogs();
        assertThat(logs).isEmpty();
    }

}
