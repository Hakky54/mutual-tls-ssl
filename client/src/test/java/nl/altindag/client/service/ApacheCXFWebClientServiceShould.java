package nl.altindag.client.service;

import nl.altindag.client.ClientConfig;
import nl.altindag.client.model.ClientResponse;
import nl.altindag.client.util.MockServerTestHelper;
import nl.altindag.client.util.SSLFactoryTestHelper;
import nl.altindag.ssl.SSLFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static nl.altindag.client.ClientType.APACHE_CXF_WEB_CLIENT;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApacheCXFWebClientServiceShould {

    @Test
    void createClientWithSslMaterial() throws Exception {
        MockServerTestHelper.mockResponseForClient(APACHE_CXF_WEB_CLIENT);

        SSLFactory sslFactory = SSLFactoryTestHelper.createSSLFactory(false, true);
        org.apache.cxf.jaxrs.client.WebClient cxfWebClient = new ClientConfig().cxfWebClient(sslFactory);

        ApacheCXFWebClientService victim = new ApacheCXFWebClientService(cxfWebClient);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");

        verify(sslFactory, times(1)).getSslSocketFactory();
        verify(sslFactory, times(1)).getHostnameVerifier();
    }

    @Test
    void executeRequest() throws Exception {
        MockServerTestHelper.mockResponseForClient(APACHE_CXF_WEB_CLIENT);

        org.apache.cxf.jaxrs.client.WebClient cxfWebClient = new ClientConfig().cxfWebClient(null);

        ApacheCXFWebClientService victim = new ApacheCXFWebClientService(cxfWebClient);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");
    }
}
