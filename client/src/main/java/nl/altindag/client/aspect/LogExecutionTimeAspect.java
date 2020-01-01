package nl.altindag.client.aspect;

import java.util.function.Supplier;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import nl.altindag.client.TestScenario;

@Aspect
@Configuration
@EnableAspectJAutoProxy
public class LogExecutionTimeAspect {

    private static final Supplier<Long> currentTime = System::currentTimeMillis;
    private TestScenario testScenario;

    @Autowired
    public LogExecutionTimeAspect(TestScenario testScenario) {
        this.testScenario = testScenario;
    }

    @Before("@annotation(nl.altindag.client.aspect.LogExecutionTime)")
    public void setStartTime() {
        this.testScenario.setStartTime(currentTime.get());
    }

    @After("@annotation(nl.altindag.client.aspect.LogExecutionTime)")
    public void setEndTime() {
        this.testScenario.setEndTime(currentTime.get());
    }

}
