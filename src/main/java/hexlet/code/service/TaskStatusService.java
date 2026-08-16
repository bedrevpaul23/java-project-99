package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.DuplicateResourceException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskStatusService {
  private final TaskStatusRepository taskStatusRepository;
  private final TaskStatusMapper taskStatusMapper;

  public TaskStatusService(
      TaskStatusRepository taskStatusRepository, TaskStatusMapper taskStatusMapper) {
    this.taskStatusRepository = taskStatusRepository;
    this.taskStatusMapper = taskStatusMapper;
  }

  @Transactional(readOnly = true)
  public List<TaskStatusDTO> getAll() {
    return taskStatusRepository.findAll().stream().map(taskStatusMapper::map).toList();
  }

  @Transactional(readOnly = true)
  public TaskStatusDTO getById(Long id) {
    return taskStatusMapper.map(findById(id));
  }

  @Transactional
  public TaskStatusDTO create(TaskStatusCreateDTO dto) {
    checkNameAvailable(dto.getName(), null);
    checkSlugAvailable(dto.getSlug(), null);
    return taskStatusMapper.map(taskStatusRepository.save(taskStatusMapper.map(dto)));
  }

  @Transactional
  public TaskStatusDTO update(Long id, TaskStatusUpdateDTO dto) {
    var taskStatus = findById(id);
    if (dto.getName().isPresent()) {
      checkNameAvailable(dto.getName().get(), taskStatus.getId());
    }
    if (dto.getSlug().isPresent()) {
      checkSlugAvailable(dto.getSlug().get(), taskStatus.getId());
    }
    taskStatusMapper.update(dto, taskStatus);
    return taskStatusMapper.map(taskStatusRepository.save(taskStatus));
  }

  @Transactional
  public void delete(Long id) {
    taskStatusRepository.delete(findById(id));
  }

  private TaskStatus findById(Long id) {
    return taskStatusRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Task status not found: " + id));
  }

  private void checkNameAvailable(String name, Long currentId) {
    taskStatusRepository
        .findByName(name)
        .filter(taskStatus -> !Objects.equals(taskStatus.getId(), currentId))
        .ifPresent(
            taskStatus -> {
              throw new DuplicateResourceException("Task status name already exists: " + name);
            });
  }

  private void checkSlugAvailable(String slug, Long currentId) {
    taskStatusRepository
        .findBySlug(slug)
        .filter(taskStatus -> !Objects.equals(taskStatus.getId(), currentId))
        .ifPresent(
            taskStatus -> {
              throw new DuplicateResourceException("Task status slug already exists: " + slug);
            });
  }
}
