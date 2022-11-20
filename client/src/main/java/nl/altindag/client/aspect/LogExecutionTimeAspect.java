/*
 * Copyright 2018 Thunderberry.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
