package nl.altindag.client.service;

import nl.altindag.client.model.ClientResponse;

public abstract class RequestService {

    public abstract ClientResponse executeRequest(String url) throws Exception;

}
