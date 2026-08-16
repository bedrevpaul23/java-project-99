package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class TaskCreateDTO {
  private Integer index;

  @JsonProperty("assignee_id")
  private Long assigneeId;

  @NotBlank private String title;

  private String content;

  @NotBlank private String status;

  private List<Long> taskLabelIds;

  public Integer getIndex() {
    return index;
  }

  public void setIndex(Integer index) {
    this.index = index;
  }

  public Long getAssigneeId() {
    return assigneeId;
  }

  public void setAssigneeId(Long assigneeId) {
    this.assigneeId = assigneeId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<Long> getTaskLabelIds() {
    return taskLabelIds;
  }

  public void setTaskLabelIds(List<Long> taskLabelIds) {
    this.taskLabelIds = taskLabelIds;
  }
}
