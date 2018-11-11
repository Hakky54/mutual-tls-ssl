package nl.altindag.client;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CucumberConfiguration.class, loader = SpringBootContextLoader.class)
public class SpringBootHelper {

}
