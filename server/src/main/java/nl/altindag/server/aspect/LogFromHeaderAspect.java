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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static java.util.Objects.nonNull;

@Aspect
@Configuration
@EnableAspectJAutoProxy
public class LogFromHeaderAspect {

    private static final Logger LOGGER = LogManager.getLogger(LogFromHeaderAspect.class);
    private static final String HEADER_KEY_FROM = "from";

    @Before("@annotation(nl.altindag.server.aspect.LogFromHeader)")
    public void logIfPresent() {
        String from = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest()
                .getHeader(HEADER_KEY_FROM);

        if (nonNull(from)) {
            LOGGER.info("Hello there! It seems like {} is knocking on my door...", from);
        }
    }

}
