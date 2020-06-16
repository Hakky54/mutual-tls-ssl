package nl.altindag.client.aspect;

import nl.altindag.client.TestScenario;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.time.LocalTime;

@Aspect
@Configuration
@EnableAspectJAutoProxy
@SuppressWarnings({"SpringFacetCodeInspection", "SpringJavaInjectionPointsAutowiringInspection"})
public class LogExecutionTimeAspect {

    private TestScenario testScenario;

    @Autowired
    public LogExecutionTimeAspect(TestScenario testScenario) {
        this.testScenario = testScenario;
    }

    @Before("@annotation(nl.altindag.client.aspect.LogExecutionTime)")
    public void setStartTime() {
        this.testScenario.setStartTime(LocalTime.now());
    }

    @After("@annotation(nl.altindag.client.aspect.LogExecutionTime)")
    public void setEndTime() {
        this.testScenario.setEndTime(LocalTime.now());
    }

}
