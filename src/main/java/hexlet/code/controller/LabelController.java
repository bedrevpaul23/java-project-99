package hexlet.code.controller;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.service.LabelService;
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
@RequestMapping("/api/labels")
public class LabelController {
  private final LabelService labelService;

  public LabelController(LabelService labelService) {
    this.labelService = labelService;
  }

  @GetMapping
  public ResponseEntity<List<LabelDTO>> index() {
    var labels = labelService.getAll();
    return ResponseEntity.ok().header("X-Total-Count", String.valueOf(labels.size())).body(labels);
  }

  @GetMapping("/{id}")
  public LabelDTO show(@PathVariable Long id) {
    return labelService.getById(id);
  }

  @PostMapping
  public ResponseEntity<LabelDTO> create(@Valid @RequestBody LabelCreateDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(labelService.create(dto));
  }

  @PutMapping("/{id}")
  public LabelDTO update(@PathVariable Long id, @Valid @RequestBody LabelUpdateDTO dto) {
    return labelService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    labelService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
