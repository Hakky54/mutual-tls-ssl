package nl.altindag.client.stepdefs;

import ch.qos.logback.classic.Level;
import nl.altindag.client.ClientException;
import nl.altindag.client.ClientType;
import nl.altindag.client.TestScenario;
import nl.altindag.client.aspect.LogExecutionTime;
import nl.altindag.client.model.ClientResponse;
import nl.altindag.client.service.RequestService;
import nl.altindag.log.LogCaptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static nl.altindag.client.util.AssertJCustomConditions.HTTP_OR_HTTPS_SERVER_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("ResultOfMethodCallIgnored")
public class HelloStepDefsShould {

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
        LogCaptor<HelloStepDefs> logCaptor = LogCaptor.forClass(HelloStepDefs.class);

        victim.serverIsAlive();

        List<String> logs = logCaptor.getLogs(Level.DEBUG);

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
    public void iSayHelloWithClientIsAnnotatedWithLogExecutionTime() throws NoSuchMethodException {
        Method method = victim.getClass().getMethod("iSayHelloWithClient", String.class);
        LogExecutionTime annotation = method.getAnnotation(LogExecutionTime.class);

        assertThat(annotation).isNotNull();
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

    @Test
    public void iDisplayTheTimeItTookToGetTheMessage() {
        LogCaptor<HelloStepDefs> logCaptor = LogCaptor.forClass(HelloStepDefs.class);

        when(testScenario.getExecutionTimeInMilliSeconds()).thenReturn(134L);

        victim.iDisplayTheTimeItTookToGetTheMessage();

        List<String> logs = logCaptor.getLogs(Level.INFO);
        assertThat(logs).hasSize(1);
        assertThat(logs).containsExactly("Executed request within 134 milliseconds");

        verify(testScenario, times(1)).getExecutionTimeInMilliSeconds();
    }

}
