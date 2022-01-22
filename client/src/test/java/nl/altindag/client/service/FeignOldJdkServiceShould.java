package nl.altindag.client.service;

import feign.Feign;
import nl.altindag.client.ClientType;
import nl.altindag.client.TestConstants;
import nl.altindag.client.model.ClientResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeignOldJdkServiceShould {

    @InjectMocks
    private FeignOldJdkService victim;
    @Mock
    private Feign.Builder feignBuilder;

    @Test
    void executeRequest() throws Exception {
        FeignService.Server server = mock(FeignService.Server.class);

        Mockito.lenient().doReturn(server).when(feignBuilder).target(FeignService.Server.class, TestConstants.HTTP_SERVER_URL);
        Mockito.lenient().doReturn(server).when(feignBuilder).target(FeignService.Server.class, TestConstants.HTTPS_SERVER_URL);
        when(server.getHello(ClientType.FEIGN_OLD_JDK_HTTP_CLIENT.getValue())).thenReturn("Hello");

        ClientResponse clientResponse = victim.executeRequest(null);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");
    }

    @Test
    void getClientType() {
        assertThat(victim.getClientType()).isEqualTo(ClientType.FEIGN_OLD_JDK_HTTP_CLIENT);
    }

}
