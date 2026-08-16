package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import org.openapitools.jackson.nullable.JsonNullable;

public class TaskStatusUpdateDTO {
  private JsonNullable<@NotBlank String> name = JsonNullable.undefined();
  private JsonNullable<@NotBlank String> slug = JsonNullable.undefined();

  public JsonNullable<String> getName() {
    return name;
  }

  public void setName(JsonNullable<String> name) {
    this.name = name;
  }

  public JsonNullable<String> getSlug() {
    return slug;
  }

  public void setSlug(JsonNullable<String> slug) {
    this.slug = slug;
  }
}
