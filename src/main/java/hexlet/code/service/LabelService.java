package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import java.util.List;

public interface LabelService {
  List<LabelDTO> getAll();

  LabelDTO getById(Long id);

  LabelDTO create(LabelCreateDTO dto);

  LabelDTO update(Long id, LabelUpdateDTO dto);

  void delete(Long id);
}
