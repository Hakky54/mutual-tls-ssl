package nl.altindag.client.stepdefs;

import nl.altindag.client.ClientException;
import nl.altindag.client.ClientType;
import nl.altindag.client.TestScenario;
import nl.altindag.client.aspect.LogExecutionTime;
import nl.altindag.client.model.ClientResponse;
import nl.altindag.client.service.RequestService;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static nl.altindag.client.util.AssertJCustomConditions.HTTP_OR_HTTPS_SERVER_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HelloStepDefsShould {

    private HelloStepDefs victim;
    private TestScenario testScenario;
    private RequestService requestService;

    @BeforeEach
    void setUp() {
        testScenario = mock(TestScenario.class);
        requestService = mock(RequestService.class);

        when(requestService.getClientType()).thenReturn(ClientType.APACHE_HTTP_CLIENT);

        victim = new HelloStepDefs(testScenario, Collections.singletonList(requestService));
    }

    @Test
    void logDebugMessageWhenCallingServerIsAlive() {
        LogCaptor logCaptor = LogCaptor.forClass(HelloStepDefs.class);

        victim.serverIsAlive();

        List<String> logs = logCaptor.getDebugLogs();

        assertThat(logs)
                .hasSize(1)
                .contains("Assuming the server is up and running");
    }

    @Test
    void notLogDebugMessageWhenLogLevelIsInfoWhileCallingServerIsAlive() {
        LogCaptor logCaptor = LogCaptor.forClass(HelloStepDefs.class);
        logCaptor.setLogLevelToInfo();

        victim.serverIsAlive();

        assertThat(logCaptor.getLogs()).isEmpty();

        logCaptor.resetLogLevel();
    }

    @Test
    void iSayHelloWithClientApacheHttpClient() {
        when(requestService.execute(anyString())).thenReturn(new ClientResponse("Hello", 200));

        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);

        victim.iSayHelloWithClient("Apache HttpClient");

        verify(requestService, atLeast(1)).execute(urlArgumentCaptor.capture());
        assertThat(urlArgumentCaptor.getValue()).is(HTTP_OR_HTTPS_SERVER_URL);
    }

    @Test
    void iSayHelloWithClientIsAnnotatedWithLogExecutionTime() throws NoSuchMethodException {
        Method method = victim.getClass().getMethod("iSayHelloWithClient", String.class);
        LogExecutionTime annotation = method.getAnnotation(LogExecutionTime.class);

        assertThat(annotation).isNotNull();
    }

    @Test
    void throwExceptionWhenISayHelloWithClientUnknownClient() {
        assertThatThrownBy(() -> victim.iSayHelloWithClient("some dirty client"))
                .isInstanceOf(ClientException.class)
                .hasMessage("Could not find the provided [some dirty client] client type");
    }

    @Test
    void throwExceptionWhenISayHelloWithClientUnsupportedClient() {
        assertThatThrownBy(() -> victim.iSayHelloWithClient("none"))
                .isInstanceOf(ClientException.class)
                .hasMessage("Received a not supported [none] client type");
    }

    @Test
    void iExpectToReceiveStatusCode200() {
        ClientResponse clientResponse = new ClientResponse("Hello", 200);
        when(testScenario.getClientResponse()).thenReturn(clientResponse);

        victim.iExpectToReceiveStatusCodeStatusCode(200);

        verify(testScenario, times(1)).getClientResponse();
    }

    @Test
    void iExpectToReceiveBodyHello() {
        ClientResponse clientResponse = new ClientResponse("Hello", 200);
        when(testScenario.getClientResponse()).thenReturn(clientResponse);

        victim.iExpectToReceiveBody("Hello");

        verify(testScenario, times(1)).getClientResponse();
    }

    @Test
    void iDisplayTheTimeItTookToGetTheMessage() {
        LogCaptor logCaptor = LogCaptor.forClass(HelloStepDefs.class);

        when(testScenario.getExecutionTimeInMilliSeconds()).thenReturn(134L);

        victim.iDisplayTheTimeItTookToGetTheMessage();

        List<String> logs = logCaptor.getInfoLogs();
        assertThat(logs)
                .hasSize(1)
                .contains("Executed request within 134 milliseconds");

        verify(testScenario, times(1)).getExecutionTimeInMilliSeconds();
    }

}
