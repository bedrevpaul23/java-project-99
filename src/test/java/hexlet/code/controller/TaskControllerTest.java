package hexlet.code.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@WithMockUser(username = "task-owner@example.com")
@Transactional
class TaskControllerTest {
  private static final String BASE_URL = "/api/tasks";
  private static final String OWNER_EMAIL = "task-owner@example.com";

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private TaskRepository taskRepository;
  @Autowired private TaskStatusRepository taskStatusRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private EntityManager entityManager;

  @Test
  void listsTasksWithTotalCount() throws Exception {
    var draft = getStatus("draft");
    saveTask("First task", draft, null);
    saveTask("Second task", draft, null);
    var expectedCount = taskRepository.count();

    mockMvc
        .perform(get(BASE_URL))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(header().string("X-Total-Count", String.valueOf(expectedCount)))
        .andExpect(jsonPath("$.length()").value(expectedCount));
  }

  @Test
  void getsTaskById() throws Exception {
    var assignee = saveUser(OWNER_EMAIL);
    var task = saveTask("Stored task", getStatus("to_review"), assignee);
    task.setIndex(12);
    task.setDescription("Stored content");
    taskRepository.saveAndFlush(task);

    mockMvc
        .perform(get(BASE_URL + "/{id}", task.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(task.getId()))
        .andExpect(jsonPath("$.index").value(12))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.assignee_id").value(assignee.getId()))
        .andExpect(jsonPath("$.title").value("Stored task"))
        .andExpect(jsonPath("$.content").value("Stored content"))
        .andExpect(jsonPath("$.status").value("to_review"));
  }

  @Test
  void createsTaskWithFullPayloadAndPersistsAssociations() throws Exception {
    var assignee = saveUser(OWNER_EMAIL);
    var status = getStatus("to_review");
    var request =
        Map.of(
            "index",
            12,
            "assignee_id",
            assignee.getId(),
            "title",
            "Full task",
            "content",
            "Full content",
            "status",
            status.getSlug());

    var result =
        mockMvc
            .perform(
                post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.index").value(12))
            .andExpect(jsonPath("$.createdAt").isNotEmpty())
            .andExpect(jsonPath("$.assignee_id").value(assignee.getId()))
            .andExpect(jsonPath("$.title").value("Full task"))
            .andExpect(jsonPath("$.content").value("Full content"))
            .andExpect(jsonPath("$.status").value("to_review"))
            .andExpect(jsonPath("$.assigneeId").doesNotExist())
            .andExpect(jsonPath("$.name").doesNotExist())
            .andExpect(jsonPath("$.description").doesNotExist())
            .andExpect(jsonPath("$.taskStatus").doesNotExist())
            .andExpect(jsonPath("$.assignee").doesNotExist())
            .andReturn();

    var taskId =
        objectMapper.readTree(result.getResponse().getContentAsString()).get("id").longValue();
    entityManager.flush();
    entityManager.clear();

    var persisted = taskRepository.findById(taskId).orElseThrow();
    assertThat(persisted.getName()).isEqualTo("Full task");
    assertThat(persisted.getDescription()).isEqualTo("Full content");
    assertThat(persisted.getIndex()).isEqualTo(12);
    assertThat(persisted.getCreatedAt()).isNotNull();
    assertThat(persisted.getTaskStatus().getId()).isEqualTo(status.getId());
    assertThat(persisted.getTaskStatus().getSlug()).isEqualTo("to_review");
    assertThat(persisted.getAssignee().getId()).isEqualTo(assignee.getId());
  }

  @Test
  void createsTaskWithOnlyMandatoryFields() throws Exception {
    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Minimal task\",\"status\":\"draft\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.index").value(nullValue()))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.assignee_id").value(nullValue()))
        .andExpect(jsonPath("$.title").value("Minimal task"))
        .andExpect(jsonPath("$.content").value(nullValue()))
        .andExpect(jsonPath("$.status").value("draft"));

    assertThat(taskRepository.findAll())
        .anySatisfy(
            task -> {
              assertThat(task.getName()).isEqualTo("Minimal task");
              assertThat(task.getTaskStatus().getSlug()).isEqualTo("draft");
              assertThat(task.getAssignee()).isNull();
            });
  }

  @Test
  void partiallyUpdatesTitleAndContent() throws Exception {
    var assignee = saveUser(OWNER_EMAIL);
    var task = saveTask("Old title", getStatus("draft"), assignee);
    task.setIndex(7);
    task.setDescription("Old content");
    taskRepository.saveAndFlush(task);

    mockMvc
        .perform(
            put(BASE_URL + "/{id}", task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"New title\",\"content\":\"New content\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("New title"))
        .andExpect(jsonPath("$.content").value("New content"))
        .andExpect(jsonPath("$.index").value(7))
        .andExpect(jsonPath("$.assignee_id").value(assignee.getId()))
        .andExpect(jsonPath("$.status").value("draft"));

    entityManager.flush();
    entityManager.clear();
    var updated = taskRepository.findById(task.getId()).orElseThrow();
    assertThat(updated.getName()).isEqualTo("New title");
    assertThat(updated.getDescription()).isEqualTo("New content");
  }

  @Test
  void changesOnlyTaskStatusBySlug() throws Exception {
    var task = saveTask("Status task", getStatus("draft"), null);

    mockMvc
        .perform(
            put(BASE_URL + "/{id}", task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"to_be_fixed\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Status task"))
        .andExpect(jsonPath("$.status").value("to_be_fixed"));

    entityManager.flush();
    entityManager.clear();
    var updated = taskRepository.findById(task.getId()).orElseThrow();
    assertThat(updated.getTaskStatus().getSlug()).isEqualTo("to_be_fixed");
  }

  @Test
  void changesOnlyIndex() throws Exception {
    var task = saveTask("Indexed task", getStatus("draft"), null);
    task.setIndex(1);
    taskRepository.saveAndFlush(task);

    mockMvc
        .perform(
            put(BASE_URL + "/{id}", task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"index\":99}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.index").value(99))
        .andExpect(jsonPath("$.title").value("Indexed task"))
        .andExpect(jsonPath("$.status").value("draft"));

    entityManager.flush();
    entityManager.clear();
    assertThat(taskRepository.findById(task.getId()).orElseThrow().getIndex()).isEqualTo(99);
  }

  @Test
  void assignsUserById() throws Exception {
    var assignee = saveUser(OWNER_EMAIL);
    var task = saveTask("Assignment task", getStatus("draft"), null);

    mockMvc
        .perform(
            put(BASE_URL + "/{id}", task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"assignee_id\":" + assignee.getId() + "}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.assignee_id").value(assignee.getId()))
        .andExpect(jsonPath("$.title").value("Assignment task"))
        .andExpect(jsonPath("$.status").value("draft"));

    entityManager.flush();
    entityManager.clear();
    assertThat(taskRepository.findById(task.getId()).orElseThrow().getAssignee().getId())
        .isEqualTo(assignee.getId());
  }

  @Test
  void clearsOptionalFieldsWithExplicitNull() throws Exception {
    var assignee = saveUser(OWNER_EMAIL);
    var task = saveTask("Clear task", getStatus("draft"), assignee);
    task.setIndex(5);
    task.setDescription("Content to clear");
    taskRepository.saveAndFlush(task);

    mockMvc
        .perform(
            put(BASE_URL + "/{id}", task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"index\":null,\"content\":null,\"assignee_id\":null}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.index").value(nullValue()))
        .andExpect(jsonPath("$.content").value(nullValue()))
        .andExpect(jsonPath("$.assignee_id").value(nullValue()))
        .andExpect(jsonPath("$.title").value("Clear task"))
        .andExpect(jsonPath("$.status").value("draft"));

    entityManager.flush();
    entityManager.clear();
    var updated = taskRepository.findById(task.getId()).orElseThrow();
    assertThat(updated.getIndex()).isNull();
    assertThat(updated.getDescription()).isNull();
    assertThat(updated.getAssignee()).isNull();
  }

  @Test
  void deletesTask() throws Exception {
    var task = saveTask("Delete task", getStatus("draft"), null);

    mockMvc.perform(delete(BASE_URL + "/{id}", task.getId())).andExpect(status().isNoContent());

    assertThat(taskRepository.existsById(task.getId())).isFalse();
    mockMvc.perform(get(BASE_URL + "/{id}", task.getId())).andExpect(status().isNotFound());
  }

  @Test
  void returnsNotFoundForUnknownTaskId() throws Exception {
    var missingId = Long.MAX_VALUE;

    mockMvc.perform(get(BASE_URL + "/{id}", missingId)).andExpect(status().isNotFound());
    mockMvc
        .perform(
            put(BASE_URL + "/{id}", missingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Missing task\"}"))
        .andExpect(status().isNotFound());
    mockMvc.perform(delete(BASE_URL + "/{id}", missingId)).andExpect(status().isNotFound());
  }

  @Test
  void returnsNotFoundForUnknownReferences() throws Exception {
    var task = saveTask("Existing task", getStatus("draft"), null);
    var missingUserId = Long.MAX_VALUE;

    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Unknown status\",\"status\":\"missing_status\"}"))
        .andExpect(status().isNotFound());
    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"title\":\"Unknown user\",\"status\":\"draft\",\"assignee_id\":"
                        + missingUserId
                        + "}"))
        .andExpect(status().isNotFound());
    mockMvc
        .perform(
            put(BASE_URL + "/{id}", task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"missing_status\"}"))
        .andExpect(status().isNotFound());
    mockMvc
        .perform(
            put(BASE_URL + "/{id}", task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"assignee_id\":" + missingUserId + "}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void rejectsInvalidCreateValues() throws Exception {
    assertBadPost("{\"status\":\"draft\"}");
    assertBadPost("{\"title\":\"   \",\"status\":\"draft\"}");
    assertBadPost("{\"title\":null,\"status\":\"draft\"}");
    assertBadPost("{\"title\":\"Task\"}");
    assertBadPost("{\"title\":\"Task\",\"status\":\"   \"}");
    assertBadPost("{\"title\":\"Task\",\"status\":null}");
  }

  @Test
  void rejectsInvalidSuppliedUpdateValues() throws Exception {
    var task = saveTask("Valid task", getStatus("draft"), null);

    assertBadPut(task.getId(), "{\"title\":\"   \"}");
    assertBadPut(task.getId(), "{\"title\":null}");
    assertBadPut(task.getId(), "{\"status\":\"   \"}");
    assertBadPut(task.getId(), "{\"status\":null}");
  }

  @Test
  @WithAnonymousUser
  void rejectsUnauthenticatedTaskRequests() throws Exception {
    var task = saveTask("Protected task", getStatus("draft"), null);

    mockMvc.perform(get(BASE_URL)).andExpect(status().isUnauthorized());
    mockMvc.perform(get(BASE_URL + "/{id}", task.getId())).andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Unauthorized\",\"status\":\"draft\"}"))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            put(BASE_URL + "/{id}", task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Unauthorized\"}"))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(delete(BASE_URL + "/{id}", task.getId())).andExpect(status().isUnauthorized());

    assertThat(taskRepository.existsById(task.getId())).isTrue();
  }

  @Test
  void rejectsDeletingAssignedUserAndKeepsRelations() throws Exception {
    var assignee = saveUser(OWNER_EMAIL);
    var taskStatus = getStatus("draft");
    var task = saveTask("Linked user task", taskStatus, assignee);

    mockMvc.perform(delete("/api/users/{id}", assignee.getId())).andExpect(status().isBadRequest());

    assertThat(userRepository.existsById(assignee.getId())).isTrue();
    assertThat(taskStatusRepository.existsById(taskStatus.getId())).isTrue();
    assertThat(taskRepository.findById(task.getId()))
        .hasValueSatisfying(
            persisted -> {
              assertThat(persisted.getAssignee().getId()).isEqualTo(assignee.getId());
              assertThat(persisted.getTaskStatus().getId()).isEqualTo(taskStatus.getId());
            });
  }

  @Test
  void rejectsDeletingUsedTaskStatusAndKeepsRelations() throws Exception {
    var taskStatus = getStatus("draft");
    var task = saveTask("Linked status task", taskStatus, null);

    mockMvc
        .perform(delete("/api/task_statuses/{id}", taskStatus.getId()))
        .andExpect(status().isBadRequest());

    assertThat(taskStatusRepository.existsById(taskStatus.getId())).isTrue();
    assertThat(taskRepository.findById(task.getId()))
        .hasValueSatisfying(
            persisted ->
                assertThat(persisted.getTaskStatus().getId()).isEqualTo(taskStatus.getId()));
  }

  private TaskStatus getStatus(String slug) {
    return taskStatusRepository
        .findBySlug(slug)
        .orElseGet(
            () -> {
              var taskStatus = new TaskStatus();
              taskStatus.setName("TaskTest_" + UUID.randomUUID());
              taskStatus.setSlug(slug);
              return taskStatusRepository.saveAndFlush(taskStatus);
            });
  }

  private User saveUser(String email) {
    var user = new User();
    user.setEmail(email);
    user.setFirstName("Task");
    user.setLastName("Owner");
    user.setPasswordDigest(passwordEncoder.encode("secret"));
    return userRepository.saveAndFlush(user);
  }

  private Task saveTask(String name, TaskStatus taskStatus, User assignee) {
    var task = new Task();
    task.setName(name);
    task.setTaskStatus(taskStatus);
    task.setAssignee(assignee);
    return taskRepository.saveAndFlush(task);
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
