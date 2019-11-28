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
