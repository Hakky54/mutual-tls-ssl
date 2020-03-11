package nl.altindag.client.service;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class RetrofitServiceShould {

    @InjectMocks
    private RetrofitService victim;
    @Mock
    private Retrofit retrofit;

    @Test
    public void executeRequest() throws Exception {
        RetrofitService.Server server = mock(RetrofitService.Server.class);
        Call<String> helloCall = mock(Call.class);
        Response<String> response = mock(Response.class);

        when(retrofit.create(RetrofitService.Server.class)).thenReturn(server);
        when(server.getHello()).thenReturn(helloCall);
        when(helloCall.execute()).thenReturn(response);
        when(response.body()).thenReturn("Hello");
        when(response.code()).thenReturn(200);

        ClientResponse clientResponse = victim.executeRequest(null);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");
    }

    @Test
    public void getClientType() {
        assertThat(victim.getClientType()).isEqualTo(ClientType.RETROFIT);
    }

}
