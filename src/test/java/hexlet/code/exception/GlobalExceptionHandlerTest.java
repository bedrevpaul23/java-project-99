package hexlet.code.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(new TestController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void returnsSafeResponseForUnexpectedException() throws Exception {
    var result =
        mockMvc
            .perform(get("/test/boom"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("Internal server error"))
            .andReturn();

    var responseBody = result.getResponse().getContentAsString();
    assertThat(responseBody)
        .doesNotContain("internal secret")
        .doesNotContain(IllegalStateException.class.getName());
  }

  @Test
  void keepsResourceNotFoundHandlerPriority() throws Exception {
    mockMvc
        .perform(get("/test/missing"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Task not found"));
  }

  @RestController
  static class TestController {
    @GetMapping("/test/boom")
    void boom() {
      throw new IllegalStateException("internal secret");
    }

    @GetMapping("/test/missing")
    void missing() {
      throw new ResourceNotFoundException("Task not found");
    }
  }
}
