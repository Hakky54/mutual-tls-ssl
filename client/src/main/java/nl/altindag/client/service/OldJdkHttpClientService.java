package nl.altindag.client.service;

import static nl.altindag.client.ClientType.OLD_JDK_HTTP_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;

import nl.altindag.client.ClientException;
import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import nl.altindag.ssl.SSLFactory;

@Service
public class OldJdkHttpClientService implements RequestService {

    private static final String HTTP_REQUEST = "http:";
    private static final String HTTPS_REQUEST = "https:";

    private final SSLFactory sslFactory;

    public OldJdkHttpClientService(SSLFactory sslFactory) {
        this.sslFactory = sslFactory;
    }

    @Override
    public ClientResponse executeRequest(String url) throws IOException {
        HttpURLConnection connection;
        if (url.contains(HTTP_REQUEST)) {
            connection = createHttpURLConnection(url);
        } else if (url.contains(HTTPS_REQUEST)) {
            var httpsURLConnection = createHttpsURLConnection(url);
            httpsURLConnection.setHostnameVerifier(sslFactory.getHostnameVerifier());
            httpsURLConnection.setSSLSocketFactory(sslFactory.getSslSocketFactory());
            connection = httpsURLConnection;
        } else {
            throw new ClientException("Could not create a http client for one of these reasons: "
                    + "invalid url, "
                    + "security is enable while using an url with http or "
                    + "security is disable while using an url with https");
        }

        connection.setRequestMethod(HttpGet.METHOD_NAME);
        connection.setRequestProperty(HEADER_KEY_CLIENT_TYPE, getClientType().getValue());
        var responseBody = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
        return new ClientResponse(responseBody, connection.getResponseCode());
    }

    @VisibleForTesting
    HttpURLConnection createHttpURLConnection(String url) throws IOException {
        return (HttpURLConnection) new URL(url).openConnection();
    }

    @VisibleForTesting
    HttpsURLConnection createHttpsURLConnection(String url) throws IOException {
        return (HttpsURLConnection) new URL(url).openConnection();
    }

    @Override
    public ClientType getClientType() {
        return OLD_JDK_HTTP_CLIENT;
    }

}
