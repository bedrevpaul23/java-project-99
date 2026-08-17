package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import java.util.List;

public interface TaskStatusService {
  List<TaskStatusDTO> getAll();

  TaskStatusDTO getById(Long id);

  TaskStatusDTO create(TaskStatusCreateDTO dto);

  TaskStatusDTO update(Long id, TaskStatusUpdateDTO dto);

  void delete(Long id);
}
