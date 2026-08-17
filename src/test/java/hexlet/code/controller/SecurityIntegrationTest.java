package hexlet.code.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest extends IntegrationTestSupport {
  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void resetDatabase() throws Exception {
    clearDatabase();
    initializeDefaults();
  }

  @Test
  void keepsRootAndWelcomePublic() throws Exception {
    mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(forwardedUrl("index.html"));

    mockMvc
        .perform(get("/index.html"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
        .andExpect(content().string(containsString("<div id=\"root\"></div>")));

    mockMvc
        .perform(get("/welcome"))
        .andExpect(status().isOk())
        .andExpect(content().string("Welcome to Spring"));
  }

  @Test
  void logsInAdminAndUsesRealJwt() throws Exception {
    var token = login("hexlet@example.com", "qwerty");

    assertThat(token).isNotBlank();
    assertThat(token.split("\\.")).hasSize(3);

    mockMvc
        .perform(get("/api/users").header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].email").value("hexlet@example.com"))
        .andExpect(jsonPath("$[0].password").doesNotExist())
        .andExpect(jsonPath("$[0].passwordDigest").doesNotExist());
  }

  @Test
  void rejectsInvalidCredentials() throws Exception {
    assertUnauthorizedLogin("{\"username\":\"hexlet@example.com\",\"password\":\"wrong\"}");
    assertUnauthorizedLogin("{\"username\":\"unknown@example.com\",\"password\":\"qwerty\"}");
  }

  @Test
  void rejectsMalformedLoginRequests() throws Exception {
    mockMvc.perform(post("/api/login")).andExpect(status().isBadRequest());
    assertBadLogin("{}");
    assertBadLogin("{\"username\":\"hexlet@example.com\"}");
    assertBadLogin("{\"password\":\"qwerty\"}");
    assertBadLogin("{\"username\":\"   \",\"password\":\"qwerty\"}");
    assertBadLogin("{\"username\":\"invalid\",\"password\":\"qwerty\"}");
    assertBadLogin("{\"username\":\"hexlet@example.com\",\"password\":\"   \"}");
  }

  @Test
  void rejectsUnauthenticatedUserRoutes() throws Exception {
    var admin = userRepository.findByEmail("hexlet@example.com").orElseThrow();
    var validUser = "{\"email\":\"new@example.com\",\"password\":\"secret\"}";

    mockMvc.perform(get("/api/users")).andExpect(status().isUnauthorized());
    mockMvc.perform(get("/api/users/{id}", admin.getId())).andExpect(status().isUnauthorized());
    mockMvc
        .perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(validUser))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            put("/api/users/{id}", admin.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Changed\"}"))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(delete("/api/users/{id}", admin.getId())).andExpect(status().isUnauthorized());

    assertThat(userRepository.existsById(admin.getId())).isTrue();
  }

  @Test
  void rejectsMalformedBearerToken() throws Exception {
    mockMvc
        .perform(get("/api/users").header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void allowsAuthenticatedReadAndCreate() throws Exception {
    var admin = userRepository.findByEmail("hexlet@example.com").orElseThrow();
    var token = login("hexlet@example.com", "qwerty");

    mockMvc
        .perform(get("/api/users").header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk());
    mockMvc
        .perform(
            get("/api/users/{id}", admin.getId()).header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.passwordDigest").doesNotExist());
    mockMvc
        .perform(
            post("/api/users")
                .header(HttpHeaders.AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"new@example.com\",\"password\":\"secret\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.passwordDigest").doesNotExist());
  }

  @Test
  void allowsOwnerToUpdateSelf() throws Exception {
    var owner = saveUser("owner@example.com", "Owner", "Original", "secret");
    var token = login("owner@example.com", "secret");

    mockMvc
        .perform(
            put("/api/users/{id}", owner.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Updated\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Updated"));
  }

  @Test
  void forbidsUpdatingAnotherUserBeforeMutation() throws Exception {
    var owner = saveUser("owner@example.com", "Owner", "User", "secret");
    var other = saveUser("other@example.com", "Other", "Original", "secret");
    var token = login(owner.getEmail(), "secret");

    mockMvc
        .perform(
            put("/api/users/{id}", other.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Changed\"}"))
        .andExpect(status().isForbidden());

    var unchanged = userRepository.findById(other.getId()).orElseThrow();
    assertThat(unchanged.getFirstName()).isEqualTo("Other");
    assertThat(unchanged.getLastName()).isEqualTo("Original");
  }

  @Test
  void allowsOwnerToDeleteSelf() throws Exception {
    var owner = saveUser("owner@example.com", "Owner", "User", "secret");
    var token = login(owner.getEmail(), "secret");

    mockMvc
        .perform(
            delete("/api/users/{id}", owner.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isNoContent());

    assertThat(userRepository.existsById(owner.getId())).isFalse();
  }

  @Test
  void forbidsDeletingAnotherUserBeforeMutation() throws Exception {
    var owner = saveUser("owner@example.com", "Owner", "User", "secret");
    var other = saveUser("other@example.com", "Other", "User", "secret");
    var token = login(owner.getEmail(), "secret");

    mockMvc
        .perform(
            delete("/api/users/{id}", other.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isForbidden());

    assertThat(userRepository.existsById(other.getId())).isTrue();
  }

  @Test
  void returnsNotFoundWhenUpdatingMissingUser() throws Exception {
    var token = login("hexlet@example.com", "qwerty");

    mockMvc
        .perform(
            put("/api/users/{id}", Long.MAX_VALUE)
                .header(HttpHeaders.AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Updated\"}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void returnsNotFoundWhenDeletingMissingUser() throws Exception {
    var token = login("hexlet@example.com", "qwerty");

    mockMvc
        .perform(
            delete("/api/users/{id}", Long.MAX_VALUE)
                .header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isNotFound());
  }

  @Test
  void keepsValidationForAuthenticatedRequests() throws Exception {
    var admin = userRepository.findByEmail("hexlet@example.com").orElseThrow();
    var token = login("hexlet@example.com", "qwerty");

    mockMvc
        .perform(
            post("/api/users")
                .header(HttpHeaders.AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"invalid\",\"password\":\"ab\"}"))
        .andExpect(status().isBadRequest());
    mockMvc
        .perform(
            put("/api/users/{id}", admin.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":null}"))
        .andExpect(status().isBadRequest());
  }

  private String login(String username, String password) throws Exception {
    return mockMvc
        .perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
  }

  private void assertUnauthorizedLogin(String body) throws Exception {
    mockMvc
        .perform(post("/api/login").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isUnauthorized());
  }

  private void assertBadLogin(String body) throws Exception {
    mockMvc
        .perform(post("/api/login").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest());
  }

  private User saveUser(String email, String firstName, String lastName, String rawPassword) {
    var user = new User();
    user.setEmail(email);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setPasswordDigest(passwordEncoder.encode(rawPassword));
    return userRepository.saveAndFlush(user);
  }

  private String bearer(String token) {
    return "Bearer " + token;
  }
}
