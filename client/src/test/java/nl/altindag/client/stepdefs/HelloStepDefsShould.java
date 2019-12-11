package nl.altindag.client.stepdefs;

import static nl.altindag.client.util.AssertJCustomConditions.HTTP_OR_HTTPS_SERVER_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import ch.qos.logback.classic.Level;
import nl.altindag.client.ClientException;
import nl.altindag.client.ClientType;
import nl.altindag.client.TestScenario;
import nl.altindag.client.model.ClientResponse;
import nl.altindag.client.service.RequestService;
import nl.altindag.client.util.LogTestHelper;

@RunWith(MockitoJUnitRunner.class)
public class HelloStepDefsShould extends LogTestHelper<HelloStepDefs> {

    private HelloStepDefs victim;
    private TestScenario testScenario;
    private RequestService requestService;

    @Before
    public void setUp() {
        testScenario = mock(TestScenario.class);
        requestService = mock(RequestService.class);

        when(requestService.getClientType()).thenReturn(ClientType.APACHE_HTTP_CLIENT);

        victim = new HelloStepDefs(testScenario, Collections.singletonList(requestService));
    }

    @Test
    public void serverIsAlive() {
        victim.serverIsAlive();

        List<String> logs = super.getLogs(Level.DEBUG);

        assertThat(logs).hasSize(1);
        assertThat(logs).containsExactly("Assuming the server is up and running");
    }

    @Test
    public void iSayHelloWithClientApacheHttpClient() throws Exception {
        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);

        victim.iSayHelloWithClient("Apache HttpClient");

        verify(requestService, atLeast(1)).executeRequest(urlArgumentCaptor.capture());
        assertThat(urlArgumentCaptor.getValue()).is(HTTP_OR_HTTPS_SERVER_URL);
    }


    @Test
    public void throwExceptionWhenISayHelloWithClientUnknownClient() {
        assertThatThrownBy(() -> victim.iSayHelloWithClient("some dirty client"))
                .isInstanceOf(ClientException.class)
                .hasMessage("Could not find the provided [some dirty client] client type");
    }

    @Test
    public void throwExceptionWhenISayHelloWithClientUnsupportedClient() {
        assertThatThrownBy(() -> victim.iSayHelloWithClient("none"))
                .isInstanceOf(ClientException.class)
                .hasMessage("Received a not supported [none] client type");
    }

    @Test
    public void iExpectToReceiveStatusCode200() {
        ClientResponse clientResponse = new ClientResponse("Hello", 200);
        when(testScenario.getClientResponse()).thenReturn(clientResponse);

        victim.iExpectToReceiveStatusCodeStatusCode(200);

        verify(testScenario, times(1)).getClientResponse();
    }

    @Test
    public void iExpectToReceiveBodyHello() {
        ClientResponse clientResponse = new ClientResponse("Hello", 200);
        when(testScenario.getClientResponse()).thenReturn(clientResponse);

        victim.iExpectToReceiveBody("Hello");

        verify(testScenario, times(1)).getClientResponse();
    }

    @Override
    protected Class<HelloStepDefs> getTargetClass() {
        return HelloStepDefs.class;
    }

}
