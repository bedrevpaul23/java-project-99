package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {
  private final TaskRepository taskRepository;
  private final TaskStatusRepository taskStatusRepository;
  private final UserRepository userRepository;
  private final TaskMapper taskMapper;

  public TaskService(
      TaskRepository taskRepository,
      TaskStatusRepository taskStatusRepository,
      UserRepository userRepository,
      TaskMapper taskMapper) {
    this.taskRepository = taskRepository;
    this.taskStatusRepository = taskStatusRepository;
    this.userRepository = userRepository;
    this.taskMapper = taskMapper;
  }

  @Transactional(readOnly = true)
  public List<TaskDTO> getAll() {
    return taskRepository.findAll().stream().map(taskMapper::map).toList();
  }

  @Transactional(readOnly = true)
  public TaskDTO getById(Long id) {
    return taskMapper.map(findById(id));
  }

  @Transactional
  public TaskDTO create(TaskCreateDTO dto) {
    var task = taskMapper.map(dto);
    task.setTaskStatus(findTaskStatusBySlug(dto.getStatus()));
    if (dto.getAssigneeId() != null) {
      task.setAssignee(findUserById(dto.getAssigneeId()));
    }
    return taskMapper.map(taskRepository.save(task));
  }

  @Transactional
  public TaskDTO update(Long id, TaskUpdateDTO dto) {
    var task = findById(id);
    taskMapper.update(dto, task);
    if (dto.getStatus().isPresent()) {
      task.setTaskStatus(findTaskStatusBySlug(dto.getStatus().get()));
    }
    if (dto.getAssigneeId().isPresent()) {
      var assigneeId = dto.getAssigneeId().orElse(null);
      task.setAssignee(assigneeId == null ? null : findUserById(assigneeId));
    }
    return taskMapper.map(taskRepository.save(task));
  }

  @Transactional
  public void delete(Long id) {
    taskRepository.delete(findById(id));
  }

  private Task findById(Long id) {
    return taskRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
  }

  private TaskStatus findTaskStatusBySlug(String slug) {
    return taskStatusRepository
        .findBySlug(slug)
        .orElseThrow(() -> new ResourceNotFoundException("Task status not found: " + slug));
  }

  private User findUserById(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
  }
}
