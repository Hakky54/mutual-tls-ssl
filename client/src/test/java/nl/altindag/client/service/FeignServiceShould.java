package nl.altindag.client.service;

import feign.Feign;
import nl.altindag.client.ClientType;
import nl.altindag.client.TestConstants;
import nl.altindag.client.model.ClientResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FeignServiceShould {

    @InjectMocks
    private FeignService victim;
    @Mock
    private Feign.Builder feignBuilder;

    @Test
    public void executeRequest() throws Exception {
        FeignService.Server server = mock(FeignService.Server.class);

        doReturn(server).when(feignBuilder).target(FeignService.Server.class, TestConstants.SERVER_URL);
        when(server.getHello()).thenReturn("Hello");

        ClientResponse clientResponse = victim.executeRequest(null);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");
    }

    @Test
    public void getClientType() {
        assertThat(victim.getClientType()).isEqualTo(ClientType.FEIGN);
    }

}
