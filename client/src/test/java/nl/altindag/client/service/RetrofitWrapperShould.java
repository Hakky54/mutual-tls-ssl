package nl.altindag.client.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import nl.altindag.client.ClientException;
import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class RetrofitWrapperShould {

    @InjectMocks
    private RetrofitWrapper victim;
    @Mock
    private Retrofit retrofit;

    @Test
    public void executeRequest() throws Exception {
        RetrofitWrapper.Server server = mock(RetrofitWrapper.Server.class);
        Call<String> helloCall = mock(Call.class);
        Response<String> response = mock(Response.class);

        when(retrofit.create(RetrofitWrapper.Server.class)).thenReturn(server);
        when(server.getHello()).thenReturn(helloCall);
        when(helloCall.execute()).thenReturn(response);
        when(response.body()).thenReturn("Hello");
        when(response.code()).thenReturn(200);

        ClientResponse clientResponse = victim.executeRequest(null);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");
    }

    @Test
    public void executeRequestThrowsExceptionWhenServerIsNotReachable() throws Exception {
        RetrofitWrapper.Server server = mock(RetrofitWrapper.Server.class);
        Call<String> helloCall = mock(Call.class);

        when(retrofit.create(RetrofitWrapper.Server.class)).thenReturn(server);
        when(server.getHello()).thenReturn(helloCall);
        when(helloCall.execute()).thenThrow(new IOException("KABOOOM!!!"));

        assertThatThrownBy(() -> victim.executeRequest(null))
                .isInstanceOf(ClientException.class)
                .hasMessage("could not execute the request, received the following message: KABOOOM!!!");
    }

    @Test
    public void getClientType() {
        assertThat(victim.getClientType()).isEqualTo(ClientType.RETROFIT);
    }

}
