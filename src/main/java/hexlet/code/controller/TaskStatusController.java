package hexlet.code.controller;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {
  private final TaskStatusService taskStatusService;

  public TaskStatusController(TaskStatusService taskStatusService) {
    this.taskStatusService = taskStatusService;
  }

  @GetMapping
  public ResponseEntity<List<TaskStatusDTO>> index() {
    var taskStatuses = taskStatusService.getAll();
    return ResponseEntity.ok()
        .header("X-Total-Count", String.valueOf(taskStatuses.size()))
        .body(taskStatuses);
  }

  @GetMapping("/{id}")
  public TaskStatusDTO show(@PathVariable Long id) {
    return taskStatusService.getById(id);
  }

  @PostMapping
  public ResponseEntity<TaskStatusDTO> create(@Valid @RequestBody TaskStatusCreateDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(taskStatusService.create(dto));
  }

  @PutMapping("/{id}")
  public TaskStatusDTO update(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateDTO dto) {
    return taskStatusService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    taskStatusService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
