package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

public class LabelUpdateDTO {
  private JsonNullable<@NotBlank @Size(min = 3, max = 1000) String> name = JsonNullable.undefined();

  public JsonNullable<String> getName() {
    return name;
  }

  public void setName(JsonNullable<String> name) {
    this.name = name;
  }
}
