package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class TaskDTO {
  private Long id;
  private Integer index;
  private LocalDate createdAt;

  @JsonProperty("assignee_id")
  private Long assigneeId;

  private String title;
  private String content;
  private String status;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getIndex() {
    return index;
  }

  public void setIndex(Integer index) {
    this.index = index;
  }

  public LocalDate getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDate createdAt) {
    this.createdAt = createdAt;
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
}
