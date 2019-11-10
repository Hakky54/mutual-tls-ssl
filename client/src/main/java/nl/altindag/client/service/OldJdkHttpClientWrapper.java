package nl.altindag.client.service;

import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.Constants.OLD_JDK_HTTP_CLIENT;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.altindag.client.ClientException;
import nl.altindag.client.SSLTrustManagerHelper;
import nl.altindag.client.model.ClientResponse;

@Service
public class OldJdkHttpClientWrapper extends RequestService {

    private static final String HTTP_REQUEST = "http:";
    private static final String HTTPS_REQUEST = "https:";

    private final SSLTrustManagerHelper sslTrustManagerHelper;

    @Autowired
    public OldJdkHttpClientWrapper(SSLTrustManagerHelper sslTrustManagerHelper) {
        this.sslTrustManagerHelper = sslTrustManagerHelper;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        HttpURLConnection connection;
        if (url.contains(HTTP_REQUEST)) {
            connection = createHttpURLConnection(url);
        } else if (url.contains(HTTPS_REQUEST)) {
            HttpsURLConnection httpsURLConnection = createHttpsURLConnection(url);
            httpsURLConnection.setSSLSocketFactory(sslTrustManagerHelper.getSslContext().getSocketFactory());
            connection = httpsURLConnection;
        } else {
            throw new ClientException("Could not create a http client for one of these reasons: "
                                              + "invalid url, "
                                              + "security is enable while using an url with http or "
                                              + "security is disable while using an url with https");
        }

        connection.setRequestMethod("GET");
        connection.setRequestProperty(HEADER_KEY_CLIENT_TYPE, OLD_JDK_HTTP_CLIENT);
        String responseBody = IOUtils.toString(connection.getInputStream());
        return new ClientResponse(responseBody, connection.getResponseCode());
    }

    HttpURLConnection createHttpURLConnection(String url) throws IOException {
        return (HttpURLConnection) new URL(url).openConnection();
    }

    HttpsURLConnection createHttpsURLConnection(String url) throws IOException {
        return (HttpsURLConnection) new URL(url).openConnection();
    }

}
