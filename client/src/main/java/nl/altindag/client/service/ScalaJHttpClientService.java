package nl.altindag.client.service;

import com.google.common.annotations.VisibleForTesting;
import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import nl.altindag.sslcontext.SSLFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.runtime.BoxedUnit;
import scalaj.http.Http;
import scalaj.http.HttpRequest;
import scalaj.http.HttpResponse;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;

import static nl.altindag.client.ClientType.SCALAJ_HTTP_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class ScalaJHttpClientService implements RequestService {

    private final SSLFactory sslFactory;

    @Autowired
    public ScalaJHttpClientService(SSLFactory sslFactory) {
        this.sslFactory = sslFactory;
    }

    @Override
    public ClientResponse executeRequest(String url) throws IOException {
        HttpResponse<String> response = createRequest(url)
                .method("GET")
                .header(HEADER_KEY_CLIENT_TYPE, getClientType().getValue())
                .option(httpUrlConnection -> {
                   if (httpUrlConnection instanceof HttpsURLConnection && sslFactory.isSecurityEnabled()) {
                       HttpsURLConnection httpsURLConnection = (HttpsURLConnection) httpUrlConnection;
                       httpsURLConnection.setSSLSocketFactory(sslFactory.getSslContext().getSocketFactory());
                       httpsURLConnection.setHostnameVerifier(sslFactory.getHostnameVerifier());
                   }
                   return BoxedUnit.UNIT;
               }).asString();

        return new ClientResponse(response.body(), response.code());
    }

    @VisibleForTesting
    HttpRequest createRequest(String url) {
        return Http.apply(url);
    }

    @Override
    public ClientType getClientType() {
        return SCALAJ_HTTP_CLIENT;
    }

}
