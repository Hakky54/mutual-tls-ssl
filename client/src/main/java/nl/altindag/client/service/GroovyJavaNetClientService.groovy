package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.client.model.ClientResponse
import nl.altindag.ssl.SSLFactory
import org.springframework.stereotype.Service

import javax.net.ssl.HttpsURLConnection

import static nl.altindag.client.ClientType.GROOVY_JAVA_NET_CLIENT
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE

@Service
class GroovyJavaNetClientService implements RequestService {

    private final SSLFactory sslFactory

    GroovyJavaNetClientService(SSLFactory sslFactory) {
        this.sslFactory = sslFactory
    }

    @Override
    ClientResponse executeRequest(String url) throws Exception {
        def connection = new URL(url).openConnection() as HttpURLConnection
        if (connection instanceof HttpsURLConnection) {
            connection.SSLSocketFactory = sslFactory.getSslSocketFactory()
            connection.hostnameVerifier = sslFactory.getHostnameVerifier()
        }

        connection.setRequestProperty(HEADER_KEY_CLIENT_TYPE, getClientType().getValue())

        new ClientResponse(connection.inputStream.text, connection.responseCode)
    }

    @Override
    ClientType getClientType() {
        return GROOVY_JAVA_NET_CLIENT
    }

}