package hexlet.code.component;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import java.util.Map;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {
  private static final String ADMIN_EMAIL = "hexlet@example.com";
  private static final Map<String, String> DEFAULT_TASK_STATUSES =
      Map.of(
          "draft", "Draft",
          "to_review", "ToReview",
          "to_be_fixed", "ToBeFixed",
          "to_publish", "ToPublish",
          "published", "Published");

  private final UserRepository userRepository;
  private final UserService userService;
  private final TaskStatusRepository taskStatusRepository;
  private final TaskStatusService taskStatusService;

  public DataInitializer(
      UserRepository userRepository,
      UserService userService,
      TaskStatusRepository taskStatusRepository,
      TaskStatusService taskStatusService) {
    this.userRepository = userRepository;
    this.userService = userService;
    this.taskStatusRepository = taskStatusRepository;
    this.taskStatusService = taskStatusService;
  }

  @Override
  public void run(ApplicationArguments args) {
    initializeAdmin();
    initializeTaskStatuses();
  }

  private void initializeAdmin() {
    if (userRepository.findByEmail(ADMIN_EMAIL).isEmpty()) {
      var dto = new UserCreateDTO();
      dto.setEmail(ADMIN_EMAIL);
      dto.setPassword("qwerty");
      userService.create(dto);
    }
  }

  private void initializeTaskStatuses() {
    DEFAULT_TASK_STATUSES.forEach(
        (slug, name) -> {
          if (taskStatusRepository.findBySlug(slug).isEmpty()) {
            var dto = new TaskStatusCreateDTO();
            dto.setName(name);
            dto.setSlug(slug);
            taskStatusService.create(dto);
          }
        });
  }
}
