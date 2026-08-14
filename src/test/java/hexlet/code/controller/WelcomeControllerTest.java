package hexlet.code.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.AppApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = AppApplication.class)
@AutoConfigureMockMvc
class WelcomeControllerTest {
  @Autowired private MockMvc mockMvc;

  @Test
  void returnsWelcomeMessage() throws Exception {
    mockMvc
        .perform(get("/welcome"))
        .andExpect(status().isOk())
        .andExpect(content().string("Welcome to Spring"));
  }
}
