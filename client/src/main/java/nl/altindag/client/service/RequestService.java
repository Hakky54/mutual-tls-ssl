package nl.altindag.client.service;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;

public interface RequestService {

    ClientResponse executeRequest(String url) throws Exception;

    ClientType getClientType();

}
