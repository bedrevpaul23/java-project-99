package hexlet.code.mapper;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.model.Label;
import org.springframework.stereotype.Component;

@Component
public class LabelMapper {
  public LabelDTO map(Label label) {
    var dto = new LabelDTO();
    dto.setId(label.getId());
    dto.setName(label.getName());
    dto.setCreatedAt(label.getCreatedAt());
    return dto;
  }

  public Label map(LabelCreateDTO dto) {
    var label = new Label();
    label.setName(dto.getName());
    return label;
  }

  public void update(LabelUpdateDTO dto, Label label) {
    if (dto.getName().isPresent()) {
      label.setName(dto.getName().get());
    }
  }
}
