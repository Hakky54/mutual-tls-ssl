package nl.altindag.server.aspect;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import nl.altindag.server.util.LogTestHelper;

public class LogClientTypeAspectShould extends LogTestHelper<LogClientTypeAspect> {

    private LogClientTypeAspect logClientTypeAspect = new LogClientTypeAspect();

    @Test
    public void logClientTypeIfPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("client-type", "okhttp");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        logClientTypeAspect.logClientTypeIfPresent();

        List<String> logs = super.getLogs();
        Assertions.assertThat(logs).containsExactly("Received the request from the following client: okhttp");
    }

    @Override
    protected Class<LogClientTypeAspect> getTargetClass() {
        return LogClientTypeAspect.class;
    }

}
