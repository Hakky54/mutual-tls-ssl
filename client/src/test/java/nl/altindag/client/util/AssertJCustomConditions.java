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
package nl.altindag.client.util;

import static nl.altindag.client.TestConstants.HTTPS_URL;
import static nl.altindag.client.TestConstants.HTTP_URL;

import java.util.List;
import java.util.Set;

import org.assertj.core.api.Condition;

import retrofit2.Converter;
import retrofit2.converter.gson.GsonConverterFactory;

public final class AssertJCustomConditions {

    private static final Set<String> URLS = Set.of(HTTP_URL, HTTPS_URL);
    public static final Condition<String> HTTP_OR_HTTPS_SERVER_URL = new Condition<>(URLS::contains, "Validates if url is equal to the http or https url of the server");
    public static final Condition<String> SUBSTRING_OF_HTTP_OR_HTTPS_SERVER_URL = new Condition<>(url -> URLS.stream().anyMatch(urlItem -> urlItem.contains(url)), "Validates if url contains http or https url of the server");
    public static final Condition<List<? extends Converter.Factory>> GSON_CONVERTER_FACTORY = new Condition<>(factories -> factories.stream().anyMatch(factory -> factory instanceof GsonConverterFactory), "Validates if list contains GsonConverterFactory");

    private AssertJCustomConditions() {}

}
