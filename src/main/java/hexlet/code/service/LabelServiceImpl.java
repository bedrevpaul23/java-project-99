package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LabelServiceImpl implements LabelService {
  private final LabelRepository labelRepository;
  private final LabelMapper labelMapper;

  public LabelServiceImpl(LabelRepository labelRepository, LabelMapper labelMapper) {
    this.labelRepository = labelRepository;
    this.labelMapper = labelMapper;
  }

  @Override
  @Transactional(readOnly = true)
  public List<LabelDTO> getAll() {
    return labelRepository.findAll().stream().map(labelMapper::map).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public LabelDTO getById(Long id) {
    return labelMapper.map(findById(id));
  }

  @Override
  @Transactional
  public LabelDTO create(LabelCreateDTO dto) {
    return labelMapper.map(labelRepository.save(labelMapper.map(dto)));
  }

  @Override
  @Transactional
  public LabelDTO update(Long id, LabelUpdateDTO dto) {
    var label = findById(id);
    labelMapper.update(dto, label);
    return labelMapper.map(labelRepository.save(label));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    labelRepository.delete(findById(id));
  }

  private Label findById(Long id) {
    return labelRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Label not found: " + id));
  }
}
