package nl.altindag.client.aspect;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import nl.altindag.client.TestScenario;

import java.time.LocalTime;

@RunWith(MockitoJUnitRunner.class)
public class LogExecutionTimeAspectShould {

    @InjectMocks
    private LogExecutionTimeAspect logExecutionTimeAspect;
    @Mock
    private TestScenario testScenario;

    @Test
    public void setStartTime() {
        logExecutionTimeAspect.setStartTime();

        verify(testScenario, times(1)).setStartTime(any(LocalTime.class));
    }

    @Test
    public void setEndTime() {
        logExecutionTimeAspect.setEndTime();

        verify(testScenario, times(1)).setEndTime(any(LocalTime.class));
    }

}
