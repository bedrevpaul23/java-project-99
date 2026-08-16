package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
  public TaskDTO map(Task task) {
    var dto = new TaskDTO();
    dto.setId(task.getId());
    dto.setIndex(task.getIndex());
    dto.setCreatedAt(task.getCreatedAt());
    dto.setAssigneeId(task.getAssignee() == null ? null : task.getAssignee().getId());
    dto.setTitle(task.getName());
    dto.setContent(task.getDescription());
    dto.setStatus(task.getTaskStatus().getSlug());
    dto.setTaskLabelIds(task.getLabels().stream().map(label -> label.getId()).toList());
    return dto;
  }

  public Task map(TaskCreateDTO dto) {
    var task = new Task();
    task.setIndex(dto.getIndex());
    task.setName(dto.getTitle());
    task.setDescription(dto.getContent());
    return task;
  }

  public void update(TaskUpdateDTO dto, Task task) {
    if (dto.getIndex().isPresent()) {
      task.setIndex(dto.getIndex().orElse(null));
    }
    if (dto.getTitle().isPresent()) {
      task.setName(dto.getTitle().get());
    }
    if (dto.getContent().isPresent()) {
      task.setDescription(dto.getContent().orElse(null));
    }
  }
}
