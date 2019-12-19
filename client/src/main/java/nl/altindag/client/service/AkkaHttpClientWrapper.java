package nl.altindag.client.service;

import static nl.altindag.client.ClientType.AKKA_HTTP_CLIENT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;

import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpHeader;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import nl.altindag.client.ClientType;
import nl.altindag.client.Constants;
import nl.altindag.client.model.ClientResponse;

@Service
public class AkkaHttpClientWrapper extends RequestService {

    private final Http akkaHttpClient;

    @Autowired
    public AkkaHttpClientWrapper(Http akkaHttpClient) {
        this.akkaHttpClient = akkaHttpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) {
        return akkaHttpClient.singleRequest(HttpRequest.create(url).addHeader(HttpHeader.parse(Constants.HEADER_KEY_CLIENT_TYPE, getClientType().getValue())))
                             .thenApply(httpResponse -> new ClientResponse(extractBody(httpResponse), httpResponse.status().intValue()))
                             .toCompletableFuture()
                             .join();
    }

    @VisibleForTesting
    String extractBody(HttpResponse httpResponse) {
        return Unmarshaller.entityToString()
                           .unmarshal(httpResponse.entity(), null, null)
                           .toCompletableFuture()
                           .join();
    }

    @Override
    public ClientType getClientType() {
        return AKKA_HTTP_CLIENT;
    }

}
