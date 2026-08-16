package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.DuplicateResourceException;
import hexlet.code.exception.ResourceInUseException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LabelService {
  private final LabelRepository labelRepository;
  private final TaskRepository taskRepository;
  private final LabelMapper labelMapper;

  public LabelService(
      LabelRepository labelRepository, TaskRepository taskRepository, LabelMapper labelMapper) {
    this.labelRepository = labelRepository;
    this.taskRepository = taskRepository;
    this.labelMapper = labelMapper;
  }

  @Transactional(readOnly = true)
  public List<LabelDTO> getAll() {
    return labelRepository.findAll().stream().map(labelMapper::map).toList();
  }

  @Transactional(readOnly = true)
  public LabelDTO getById(Long id) {
    return labelMapper.map(findById(id));
  }

  @Transactional
  public LabelDTO create(LabelCreateDTO dto) {
    ensureUniqueName(dto.getName(), null);
    return labelMapper.map(labelRepository.save(labelMapper.map(dto)));
  }

  @Transactional
  public LabelDTO update(Long id, LabelUpdateDTO dto) {
    var label = findById(id);
    if (dto.getName().isPresent()) {
      ensureUniqueName(dto.getName().get(), id);
    }
    labelMapper.update(dto, label);
    return labelMapper.map(labelRepository.save(label));
  }

  @Transactional
  public void delete(Long id) {
    var label = findById(id);
    if (taskRepository.existsByLabelsId(id)) {
      throw new ResourceInUseException("Label is used by a task: " + id);
    }
    labelRepository.delete(label);
  }

  private Label findById(Long id) {
    return labelRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Label not found: " + id));
  }

  private void ensureUniqueName(String name, Long currentId) {
    labelRepository
        .findByName(name)
        .filter(label -> !label.getId().equals(currentId))
        .ifPresent(
            label -> {
              throw new DuplicateResourceException("Label name already exists: " + name);
            });
  }
}
