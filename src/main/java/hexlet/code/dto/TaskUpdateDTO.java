package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;

public class TaskUpdateDTO {
  private JsonNullable<Integer> index = JsonNullable.undefined();

  @JsonProperty("assignee_id")
  private JsonNullable<Long> assigneeId = JsonNullable.undefined();

  private JsonNullable<@NotBlank String> title = JsonNullable.undefined();
  private JsonNullable<String> content = JsonNullable.undefined();
  private JsonNullable<@NotBlank String> status = JsonNullable.undefined();
  private JsonNullable<List<Long>> taskLabelIds = JsonNullable.undefined();

  public JsonNullable<Integer> getIndex() {
    return index;
  }

  public void setIndex(JsonNullable<Integer> index) {
    this.index = index;
  }

  public JsonNullable<Long> getAssigneeId() {
    return assigneeId;
  }

  public void setAssigneeId(JsonNullable<Long> assigneeId) {
    this.assigneeId = assigneeId;
  }

  public JsonNullable<String> getTitle() {
    return title;
  }

  public void setTitle(JsonNullable<String> title) {
    this.title = title;
  }

  public JsonNullable<String> getContent() {
    return content;
  }

  public void setContent(JsonNullable<String> content) {
    this.content = content;
  }

  public JsonNullable<String> getStatus() {
    return status;
  }

  public void setStatus(JsonNullable<String> status) {
    this.status = status;
  }

  public JsonNullable<List<Long>> getTaskLabelIds() {
    return taskLabelIds;
  }

  public void setTaskLabelIds(JsonNullable<List<Long>> taskLabelIds) {
    this.taskLabelIds = taskLabelIds;
  }
}
