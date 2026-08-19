package hexlet.code.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
class LabelControllerTest extends IntegrationTestSupport {
  private static final String BASE_URL = "/api/labels";

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private LabelRepository labelRepository;

  @BeforeEach
  void resetDatabase() {
    clearDatabase();
  }

  @Test
  void listsLabelsWithTotalCount() throws Exception {
    saveLabel("List " + UUID.randomUUID());
    var expectedCount = labelRepository.count();

    mockMvc
        .perform(get(BASE_URL))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(header().string("X-Total-Count", String.valueOf(expectedCount)))
        .andExpect(jsonPath("$.length()").value(expectedCount));
  }

  @Test
  void getsLabelById() throws Exception {
    var label = saveLabel("Get " + UUID.randomUUID());

    mockMvc
        .perform(get(BASE_URL + "/{id}", label.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(label.getId()))
        .andExpect(jsonPath("$.name").value(label.getName()))
        .andExpect(jsonPath("$.createdAt").isNotEmpty());
  }

  @Test
  void createsUpdatesAndDeletesLabel() throws Exception {
    var name = "Create " + UUID.randomUUID();
    var result =
        mockMvc
            .perform(
                post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("name", name))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.createdAt").isNotEmpty())
            .andReturn();
    var id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").longValue();
    var updatedName = "Updated " + UUID.randomUUID();

    mockMvc
        .perform(
            put(BASE_URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("name", updatedName))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(updatedName));
    mockMvc.perform(delete(BASE_URL + "/{id}", id)).andExpect(status().isNoContent());
    assertThat(labelRepository.existsById(id)).isFalse();
  }

  @Test
  void returnsNotFoundForUnknownId() throws Exception {
    var missingId = Long.MAX_VALUE;

    mockMvc.perform(get(BASE_URL + "/{id}", missingId)).andExpect(status().isNotFound());
    mockMvc
        .perform(
            put(BASE_URL + "/{id}", missingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Missing\"}"))
        .andExpect(status().isNotFound());
    mockMvc.perform(delete(BASE_URL + "/{id}", missingId)).andExpect(status().isNotFound());
  }

  @Test
  void validatesNameLengthAndPresence() throws Exception {
    assertBadPost("{}");
    assertBadPost("{\"name\":null}");
    assertBadPost("{\"name\":\"   \"}");
    assertBadPost("{\"name\":\"a\"}");
    assertBadPost("{\"name\":\"ab\"}");
    assertBadPost(objectMapper.writeValueAsString(Map.of("name", "a".repeat(1001))));

    assertCreated("abc");
    assertCreated("b".repeat(1000));
  }

  @Test
  void validatesSuppliedUpdateName() throws Exception {
    var label = saveLabel("Valid " + UUID.randomUUID());

    assertBadPut(label.getId(), "{\"name\":null}");
    assertBadPut(label.getId(), "{\"name\":\"\"}");
    assertBadPut(label.getId(), "{\"name\":\"ab\"}");
    assertBadPut(label.getId(), objectMapper.writeValueAsString(Map.of("name", "a".repeat(1001))));
    mockMvc
        .perform(
            put(BASE_URL + "/{id}", label.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(label.getName()));
  }

  @Test
  void enforcesUniqueNameAndAllowsOwnName() throws Exception {
    var first = saveLabel("First " + UUID.randomUUID());
    var second = saveLabel("Second " + UUID.randomUUID());

    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("name", first.getName()))))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("{\"message\":\"Data integrity violation\"}"));
    assertBadPut(second.getId(), objectMapper.writeValueAsString(Map.of("name", first.getName())));
    mockMvc
        .perform(
            put(BASE_URL + "/{id}", first.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("name", first.getName()))))
        .andExpect(status().isOk());
  }

  @Test
  void findsLabelByNameAndInitializesDefaultsIdempotently() throws Exception {
    dataInitializer.run(new DefaultApplicationArguments(new String[0]));
    dataInitializer.run(new DefaultApplicationArguments(new String[0]));

    assertThat(labelRepository.findByName("feature")).isPresent();
    assertThat(labelRepository.findByName("bug")).isPresent();
    assertThat(
            labelRepository.findAll().stream().filter(label -> label.getName().equals("feature")))
        .hasSize(1);
    assertThat(labelRepository.findAll().stream().filter(label -> label.getName().equals("bug")))
        .hasSize(1);
  }

  @Test
  @WithAnonymousUser
  void rejectsUnauthenticatedLabelRequests() throws Exception {
    var label = saveLabel("Protected " + UUID.randomUUID());

    mockMvc.perform(get(BASE_URL)).andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"New\"}"))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            put(BASE_URL + "/{id}", label.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Changed\"}"))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(delete(BASE_URL + "/{id}", label.getId())).andExpect(status().isUnauthorized());
  }

  private Label saveLabel(String name) {
    var label = new Label();
    label.setName(name);
    return labelRepository.saveAndFlush(label);
  }

  private void assertCreated(String name) throws Exception {
    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("name", name))))
        .andExpect(status().isCreated());
  }

  private void assertBadPost(String body) throws Exception {
    mockMvc
        .perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest());
  }

  private void assertBadPut(Long id, String body) throws Exception {
    mockMvc
        .perform(put(BASE_URL + "/{id}", id).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest());
  }
}
