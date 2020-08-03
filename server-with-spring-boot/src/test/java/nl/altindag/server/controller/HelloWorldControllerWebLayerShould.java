package nl.altindag.server.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest
public class HelloWorldControllerWebLayerShould {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void returnHelloMessage() throws Exception {
        this.mockMvc.perform(get("/api/hello"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Hello")));
    }

}
