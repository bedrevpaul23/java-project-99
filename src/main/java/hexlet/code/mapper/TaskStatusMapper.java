package hexlet.code.mapper;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusMapper {
  public TaskStatusDTO map(TaskStatus taskStatus) {
    var dto = new TaskStatusDTO();
    dto.setId(taskStatus.getId());
    dto.setName(taskStatus.getName());
    dto.setSlug(taskStatus.getSlug());
    dto.setCreatedAt(taskStatus.getCreatedAt());
    return dto;
  }

  public TaskStatus map(TaskStatusCreateDTO dto) {
    var taskStatus = new TaskStatus();
    taskStatus.setName(dto.getName());
    taskStatus.setSlug(dto.getSlug());
    return taskStatus;
  }

  public void update(TaskStatusUpdateDTO dto, TaskStatus taskStatus) {
    if (dto.getName().isPresent()) {
      taskStatus.setName(dto.getName().get());
    }
    if (dto.getSlug().isPresent()) {
      taskStatus.setSlug(dto.getSlug().get());
    }
  }
}
