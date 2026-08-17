package hexlet.code.controller;

import hexlet.code.component.DataInitializer;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;

public abstract class IntegrationTestSupport {
  @Autowired protected TaskRepository taskRepository;
  @Autowired protected LabelRepository labelRepository;
  @Autowired protected TaskStatusRepository taskStatusRepository;
  @Autowired protected UserRepository userRepository;
  @Autowired protected DataInitializer dataInitializer;

  protected void clearDatabase() {
    taskRepository.deleteAll();
    labelRepository.deleteAll();
    taskStatusRepository.deleteAll();
    userRepository.deleteAll();
  }

  protected void initializeDefaults() throws Exception {
    dataInitializer.run(new DefaultApplicationArguments(new String[0]));
  }
}
