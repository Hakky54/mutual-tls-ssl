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
package nl.altindag.client;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.util.Objects;

/**
 * Suppressed warning: java:S1118 - "Utility classes should not have public constructors"
 *                                  Spring Framework can't initialize this configuration class without a public constructor
 */
@SuppressWarnings("java:S1118")
@Configuration
public class PropertyResolver {

    private static final String CLIENT_PROPERTY_FILE = "application.yml";

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        var propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        var yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource(CLIENT_PROPERTY_FILE));
        propertySourcesPlaceholderConfigurer.setProperties(Objects.requireNonNull(yaml.getObject()));
        return propertySourcesPlaceholderConfigurer;
    }

}
