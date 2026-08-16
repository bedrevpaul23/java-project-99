package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.specification.TaskSpecification;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {
  private final TaskRepository taskRepository;
  private final TaskStatusRepository taskStatusRepository;
  private final UserRepository userRepository;
  private final LabelRepository labelRepository;
  private final TaskMapper taskMapper;
  private final TaskSpecification taskSpecification;

  public TaskService(
      TaskRepository taskRepository,
      TaskStatusRepository taskStatusRepository,
      UserRepository userRepository,
      LabelRepository labelRepository,
      TaskMapper taskMapper,
      TaskSpecification taskSpecification) {
    this.taskRepository = taskRepository;
    this.taskStatusRepository = taskStatusRepository;
    this.userRepository = userRepository;
    this.labelRepository = labelRepository;
    this.taskMapper = taskMapper;
    this.taskSpecification = taskSpecification;
  }

  @Transactional(readOnly = true)
  public List<TaskDTO> getAll(TaskParamsDTO params) {
    var specification = taskSpecification.build(params);
    return taskRepository.findAll(specification).stream().map(taskMapper::map).toList();
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
    task.setLabels(findLabelsByIds(dto.getTaskLabelIds()));
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
    if (dto.getTaskLabelIds().isPresent()) {
      task.setLabels(findLabelsByIds(dto.getTaskLabelIds().orElse(List.of())));
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

  private Set<Label> findLabelsByIds(List<Long> ids) {
    var labels = new HashSet<Label>();
    if (ids == null) {
      return labels;
    }
    for (var id : ids) {
      labels.add(
          labelRepository
              .findById(id)
              .orElseThrow(() -> new ResourceNotFoundException("Label not found: " + id)));
    }
    return labels;
  }
}
