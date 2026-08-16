package hexlet.code.specification;

import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import jakarta.persistence.criteria.Join;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {
  public Specification<Task> build(TaskParamsDTO params) {
    return withTitleCont(params.getTitleCont())
        .and(withAssigneeId(params.getAssigneeId()))
        .and(withStatus(params.getStatus()))
        .and(withLabelId(params.getLabelId()));
  }

  private Specification<Task> withTitleCont(String titleCont) {
    return (root, query, cb) -> {
      if (titleCont == null || titleCont.isBlank()) {
        return cb.conjunction();
      }
      return cb.like(cb.lower(root.get("name")), "%" + titleCont.toLowerCase(Locale.ROOT) + "%");
    };
  }

  private Specification<Task> withAssigneeId(Long assigneeId) {
    return (root, query, cb) -> {
      if (assigneeId == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("assignee").get("id"), assigneeId);
    };
  }

  private Specification<Task> withStatus(String status) {
    return (root, query, cb) -> {
      if (status == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("taskStatus").get("slug"), status);
    };
  }

  private Specification<Task> withLabelId(Long labelId) {
    return (root, query, cb) -> {
      if (labelId == null) {
        return cb.conjunction();
      }
      if (query != null) {
        query.distinct(true);
      }
      Join<Task, Label> labels = root.join("labels");
      return cb.equal(labels.get("id"), labelId);
    };
  }
}
