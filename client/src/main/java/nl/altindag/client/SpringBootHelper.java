package nl.altindag.client;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CucumberConfig.class, loader = SpringBootContextLoader.class)
public class SpringBootHelper {

    static {
        List.of("org.springframework.test.context.support.DefaultTestContextBootstrapper",
                "org.springframework.http.converter.json.Jackson2ObjectMapperBuilder",
                "org.springframework.test.context.support.AbstractContextLoader")
                .stream()
                .map(clazz -> (Logger) LoggerFactory.getLogger(clazz))
                .forEach(logger -> logger.setLevel(Level.OFF));
    }

}
