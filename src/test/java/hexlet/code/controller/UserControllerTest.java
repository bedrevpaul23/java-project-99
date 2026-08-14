package hexlet.code.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.DataInitializer;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class UserControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private DataInitializer dataInitializer;

  @BeforeEach
  void cleanDatabase() {
    userRepository.deleteAll();
  }

  @Test
  void listsUsersWithoutPrivateData() throws Exception {
    var user = saveUser("jack@google.com", "Jack", "Jons", "secret");

    mockMvc
        .perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(user.getId()))
        .andExpect(jsonPath("$[0].email").value("jack@google.com"))
        .andExpect(jsonPath("$[0].password").doesNotExist())
        .andExpect(jsonPath("$[0].passwordDigest").doesNotExist());
  }

  @Test
  void getsUserWithoutPrivateData() throws Exception {
    var user = saveUser("jack@google.com", "Jack", "Jons", "secret");

    mockMvc
        .perform(get("/api/users/{id}", user.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.email").value("jack@google.com"))
        .andExpect(jsonPath("$.firstName").value("Jack"))
        .andExpect(jsonPath("$.lastName").value("Jons"))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.updatedAt").isNotEmpty())
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.passwordDigest").doesNotExist());
  }

  @Test
  void returnsNotFoundForMissingUser() throws Exception {
    mockMvc.perform(get("/api/users/{id}", 999999)).andExpect(status().isNotFound());
  }

  @Test
  void createsUserAndHashesPassword() throws Exception {
    var request =
        Map.of(
            "email", "jack@google.com",
            "firstName", "Jack",
            "lastName", "Jons",
            "password", "some-password");

    mockMvc
        .perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.email").value("jack@google.com"))
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.passwordDigest").doesNotExist());

    var persistedUser = userRepository.findByEmail("jack@google.com").orElseThrow();
    assertThat(persistedUser.getPasswordDigest()).isNotEqualTo("some-password");
    assertThat(passwordEncoder.matches("some-password", persistedUser.getPasswordDigest()))
        .isTrue();
  }

  @Test
  void rejectsInvalidCreateRequests() throws Exception {
    assertBadPost("{\"password\":\"secret\"}");
    assertBadPost("{\"email\":\"invalid\",\"password\":\"secret\"}");
    assertBadPost("{\"email\":\"jack@google.com\"}");
    assertBadPost("{\"email\":\"jack@google.com\",\"password\":\"ab\"}");
  }

  @Test
  void createsUserWithoutOptionalNames() throws Exception {
    mockMvc
        .perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"jack@google.com\",\"password\":\"secret\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("jack@google.com"))
        .andExpect(jsonPath("$.firstName").isEmpty())
        .andExpect(jsonPath("$.lastName").isEmpty());
  }

  @Test
  void partiallyUpdatesOnlySuppliedFields() throws Exception {
    var user = saveUser("jack@google.com", "Jack", "Jons", "secret");
    var originalDigest = user.getPasswordDigest();

    mockMvc
        .perform(
            put("/api/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"John\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.lastName").value("Jons"))
        .andExpect(jsonPath("$.email").value("jack@google.com"));

    var updatedUser = userRepository.findById(user.getId()).orElseThrow();
    assertThat(updatedUser.getFirstName()).isEqualTo("John");
    assertThat(updatedUser.getLastName()).isEqualTo("Jons");
    assertThat(updatedUser.getEmail()).isEqualTo("jack@google.com");
    assertThat(updatedUser.getPasswordDigest()).isEqualTo(originalDigest);
  }

  @Test
  void updatesAndHashesPassword() throws Exception {
    var user = saveUser("jack@google.com", "Jack", "Jons", "secret");
    var originalDigest = user.getPasswordDigest();

    mockMvc
        .perform(
            put("/api/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"new-password\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.passwordDigest").doesNotExist());

    var updatedUser = userRepository.findById(user.getId()).orElseThrow();
    assertThat(updatedUser.getPasswordDigest()).isNotEqualTo(originalDigest);
    assertThat(updatedUser.getPasswordDigest()).isNotEqualTo("new-password");
    assertThat(passwordEncoder.matches("new-password", updatedUser.getPasswordDigest())).isTrue();
  }

  @Test
  void rejectsInvalidUpdateValues() throws Exception {
    var user = saveUser("jack@google.com", "Jack", "Jons", "secret");

    assertBadPut(user.getId(), "{\"email\":\"invalid\"}");
    assertBadPut(user.getId(), "{\"email\":null}");
    assertBadPut(user.getId(), "{\"password\":\"ab\"}");
    assertBadPut(user.getId(), "{\"password\":null}");
  }

  @Test
  void returnsNotFoundWhenUpdatingMissingUser() throws Exception {
    mockMvc
        .perform(
            put("/api/users/{id}", 999999)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"John\"}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void deletesUser() throws Exception {
    var user = saveUser("jack@google.com", "Jack", "Jons", "secret");

    mockMvc.perform(delete("/api/users/{id}", user.getId())).andExpect(status().isNoContent());

    assertThat(userRepository.existsById(user.getId())).isFalse();
  }

  @Test
  void returnsNotFoundWhenDeletingMissingUser() throws Exception {
    mockMvc.perform(delete("/api/users/{id}", 999999)).andExpect(status().isNotFound());
  }

  @Test
  void initializesAdminIdempotently() throws Exception {
    var arguments = new DefaultApplicationArguments(new String[0]);

    dataInitializer.run(arguments);
    dataInitializer.run(arguments);

    var admin = userRepository.findByEmail("hexlet@example.com").orElseThrow();
    var adminCount =
        userRepository.findAll().stream()
            .filter(user -> "hexlet@example.com".equals(user.getEmail()))
            .count();

    assertThat(adminCount).isEqualTo(1);
    assertThat(admin.getPasswordDigest()).isNotEqualTo("qwerty");
    assertThat(passwordEncoder.matches("qwerty", admin.getPasswordDigest())).isTrue();
  }

  private User saveUser(String email, String firstName, String lastName, String rawPassword) {
    var user = new User();
    user.setEmail(email);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setPasswordDigest(passwordEncoder.encode(rawPassword));
    return userRepository.saveAndFlush(user);
  }

  private void assertBadPost(String body) throws Exception {
    mockMvc
        .perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest());
  }

  private void assertBadPut(Long id, String body) throws Exception {
    mockMvc
        .perform(put("/api/users/{id}", id).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest());
  }
}
