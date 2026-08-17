package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskStatusServiceImpl implements TaskStatusService {
  private final TaskStatusRepository taskStatusRepository;
  private final TaskStatusMapper taskStatusMapper;

  public TaskStatusServiceImpl(
      TaskStatusRepository taskStatusRepository, TaskStatusMapper taskStatusMapper) {
    this.taskStatusRepository = taskStatusRepository;
    this.taskStatusMapper = taskStatusMapper;
  }

  @Override
  @Transactional(readOnly = true)
  public List<TaskStatusDTO> getAll() {
    return taskStatusRepository.findAll().stream().map(taskStatusMapper::map).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public TaskStatusDTO getById(Long id) {
    return taskStatusMapper.map(findById(id));
  }

  @Override
  @Transactional
  public TaskStatusDTO create(TaskStatusCreateDTO dto) {
    return taskStatusMapper.map(taskStatusRepository.save(taskStatusMapper.map(dto)));
  }

  @Override
  @Transactional
  public TaskStatusDTO update(Long id, TaskStatusUpdateDTO dto) {
    var taskStatus = findById(id);
    taskStatusMapper.update(dto, taskStatus);
    return taskStatusMapper.map(taskStatusRepository.save(taskStatus));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    taskStatusRepository.delete(findById(id));
  }

  private TaskStatus findById(Long id) {
    return taskStatusRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Task status not found: " + id));
  }
}
