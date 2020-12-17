package nl.altindag.client.service;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.apache.hc.client5.http.classic.methods.ClassicHttpRequests;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Service;

import static nl.altindag.client.ClientType.APACHE5_HTTP_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class Apache5HttpClientService implements RequestService {

    private final CloseableHttpClient httpClient;

    public Apache5HttpClientService(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        HttpUriRequest request = ClassicHttpRequests.get(url);
        request.addHeader(HEADER_KEY_CLIENT_TYPE, getClientType().getValue());

        CloseableHttpResponse response = httpClient.execute(request);

        return new ClientResponse(EntityUtils.toString(response.getEntity()), response.getCode());
    }

    @Override
    public ClientType getClientType() {
        return APACHE5_HTTP_CLIENT;
    }

}
