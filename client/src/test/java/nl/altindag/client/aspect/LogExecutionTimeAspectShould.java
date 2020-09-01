package nl.altindag.client.aspect;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import nl.altindag.client.TestScenario;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

@ExtendWith(MockitoExtension.class)
class LogExecutionTimeAspectShould {

    @InjectMocks
    private LogExecutionTimeAspect logExecutionTimeAspect;
    @Mock
    private TestScenario testScenario;

    @Test
    void setStartTime() {
        logExecutionTimeAspect.setStartTime();

        verify(testScenario, times(1)).setStartTime(any(LocalTime.class));
    }

    @Test
    void setEndTime() {
        logExecutionTimeAspect.setEndTime();

        verify(testScenario, times(1)).setEndTime(any(LocalTime.class));
    }

}
