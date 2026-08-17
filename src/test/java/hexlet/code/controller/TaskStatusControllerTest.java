package hexlet.code.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
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
class TaskStatusControllerTest extends IntegrationTestSupport {
  private static final String BASE_URL = "/api/task_statuses";

  @Autowired private MockMvc mockMvc;
  @Autowired private TaskStatusRepository taskStatusRepository;

  @BeforeEach
  void resetTaskStatuses() throws Exception {
    clearDatabase();
    initializeDefaults();
  }

  @Test
  void listsTaskStatusesWithTotalCount() throws Exception {
    saveStatus("Testing", "testing");
    var expectedCount = taskStatusRepository.count();

    mockMvc
        .perform(get(BASE_URL))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(header().string("X-Total-Count", String.valueOf(expectedCount)))
        .andExpect(jsonPath("$.length()").value(expectedCount));
  }

  @Test
  void getsTaskStatusById() throws Exception {
    var taskStatus = taskStatusRepository.findBySlug("draft").orElseThrow();

    mockMvc
        .perform(get(BASE_URL + "/{id}", taskStatus.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(taskStatus.getId()))
        .andExpect(jsonPath("$.name").value("Draft"))
        .andExpect(jsonPath("$.slug").value("draft"))
        .andExpect(jsonPath("$.createdAt").isNotEmpty());
  }

  @Test
  void createsTaskStatus() throws Exception {
    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Testing\",\"slug\":\"testing\"}"))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.name").value("Testing"))
        .andExpect(jsonPath("$.slug").value("testing"))
        .andExpect(jsonPath("$.createdAt").isNotEmpty());

    assertThat(taskStatusRepository.findBySlug("testing"))
        .hasValueSatisfying(taskStatus -> assertThat(taskStatus.getName()).isEqualTo("Testing"));
  }

  @Test
  void partiallyUpdatesOnlyName() throws Exception {
    var taskStatus = taskStatusRepository.findBySlug("draft").orElseThrow();

    mockMvc
        .perform(
            put(BASE_URL + "/{id}", taskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"NewDraft\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("NewDraft"))
        .andExpect(jsonPath("$.slug").value("draft"));

    var updated = taskStatusRepository.findById(taskStatus.getId()).orElseThrow();
    assertThat(updated.getName()).isEqualTo("NewDraft");
    assertThat(updated.getSlug()).isEqualTo("draft");
  }

  @Test
  void partiallyUpdatesOnlySlug() throws Exception {
    var taskStatus = taskStatusRepository.findBySlug("draft").orElseThrow();

    mockMvc
        .perform(
            put(BASE_URL + "/{id}", taskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"slug\":\"new_draft\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Draft"))
        .andExpect(jsonPath("$.slug").value("new_draft"));

    var updated = taskStatusRepository.findById(taskStatus.getId()).orElseThrow();
    assertThat(updated.getName()).isEqualTo("Draft");
    assertThat(updated.getSlug()).isEqualTo("new_draft");
  }

  @Test
  void deletesTaskStatus() throws Exception {
    var taskStatus = saveStatus("Testing", "testing");

    mockMvc
        .perform(delete(BASE_URL + "/{id}", taskStatus.getId()))
        .andExpect(status().isNoContent());

    assertThat(taskStatusRepository.existsById(taskStatus.getId())).isFalse();
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
  void rejectsInvalidCreateValues() throws Exception {
    assertBadPost("{\"name\":\"\",\"slug\":\"valid_slug\"}");
    assertBadPost("{\"name\":null,\"slug\":\"valid_slug\"}");
    assertBadPost("{\"name\":\"Valid\",\"slug\":\"\"}");
    assertBadPost("{\"name\":\"Valid\",\"slug\":null}");
  }

  @Test
  void rejectsInvalidSuppliedUpdateValues() throws Exception {
    var taskStatus = taskStatusRepository.findBySlug("draft").orElseThrow();

    assertBadPut(taskStatus.getId(), "{\"name\":\"\"}");
    assertBadPut(taskStatus.getId(), "{\"name\":null}");
    assertBadPut(taskStatus.getId(), "{\"slug\":\"\"}");
    assertBadPut(taskStatus.getId(), "{\"slug\":null}");
  }

  @Test
  void rejectsDuplicateNameAndSlugOnCreate() throws Exception {
    assertBadPost("{\"name\":\"Draft\",\"slug\":\"unique_slug\"}");
    assertBadPost("{\"name\":\"UniqueName\",\"slug\":\"draft\"}");
  }

  @Test
  void rejectsDuplicateNameAndSlugOnUpdate() throws Exception {
    var taskStatus = taskStatusRepository.findBySlug("draft").orElseThrow();

    assertBadPut(taskStatus.getId(), "{\"name\":\"ToReview\"}");
    assertBadPut(taskStatus.getId(), "{\"slug\":\"to_review\"}");

    var unchanged = taskStatusRepository.findById(taskStatus.getId()).orElseThrow();
    assertThat(unchanged.getName()).isEqualTo("Draft");
    assertThat(unchanged.getSlug()).isEqualTo("draft");
  }

  @Test
  void acceptsOwnUnchangedUniqueValues() throws Exception {
    var taskStatus = taskStatusRepository.findBySlug("draft").orElseThrow();

    mockMvc
        .perform(
            put(BASE_URL + "/{id}", taskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Draft\",\"slug\":\"draft\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Draft"))
        .andExpect(jsonPath("$.slug").value("draft"));
  }

  @Test
  void findsTaskStatusBySlug() {
    var expected = saveStatus("Testing", "testing");

    assertThat(taskStatusRepository.findBySlug("testing"))
        .hasValueSatisfying(
            taskStatus -> {
              assertThat(taskStatus.getId()).isEqualTo(expected.getId());
              assertThat(taskStatus.getName()).isEqualTo("Testing");
            });
  }

  @Test
  void initializesDefaultTaskStatusesIdempotently() throws Exception {
    var arguments = new DefaultApplicationArguments(new String[0]);

    dataInitializer.run(arguments);
    dataInitializer.run(arguments);

    assertThat(taskStatusRepository.findAll())
        .extracting(TaskStatus::getName, TaskStatus::getSlug)
        .containsExactlyInAnyOrder(
            tuple("Draft", "draft"),
            tuple("ToReview", "to_review"),
            tuple("ToBeFixed", "to_be_fixed"),
            tuple("ToPublish", "to_publish"),
            tuple("Published", "published"));
    assertThat(taskStatusRepository.count()).isEqualTo(5);
  }

  @Test
  @WithAnonymousUser
  void rejectsUnauthenticatedMutations() throws Exception {
    var taskStatus = taskStatusRepository.findBySlug("draft").orElseThrow();

    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Testing\",\"slug\":\"testing\"}"))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            put(BASE_URL + "/{id}", taskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Changed\"}"))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(delete(BASE_URL + "/{id}", taskStatus.getId()))
        .andExpect(status().isUnauthorized());

    assertThat(taskStatusRepository.findById(taskStatus.getId()))
        .hasValueSatisfying(status -> assertThat(status.getName()).isEqualTo("Draft"));
    assertThat(taskStatusRepository.findBySlug("testing")).isEmpty();
  }

  private TaskStatus saveStatus(String name, String slug) {
    var taskStatus = new TaskStatus();
    taskStatus.setName(name);
    taskStatus.setSlug(slug);
    return taskStatusRepository.saveAndFlush(taskStatus);
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
