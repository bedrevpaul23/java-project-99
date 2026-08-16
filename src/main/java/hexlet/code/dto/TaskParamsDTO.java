package hexlet.code.dto;

public class TaskParamsDTO {
  private String titleCont;
  private Long assigneeId;
  private String status;
  private Long labelId;

  public String getTitleCont() {
    return titleCont;
  }

  public void setTitleCont(String titleCont) {
    this.titleCont = titleCont;
  }

  public Long getAssigneeId() {
    return assigneeId;
  }

  public void setAssigneeId(Long assigneeId) {
    this.assigneeId = assigneeId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getLabelId() {
    return labelId;
  }

  public void setLabelId(Long labelId) {
    this.labelId = labelId;
  }
}
