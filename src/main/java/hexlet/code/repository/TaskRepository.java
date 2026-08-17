package hexlet.code.repository;

import hexlet.code.model.Task;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
  @EntityGraph(attributePaths = {"taskStatus", "assignee", "labels"})
  List<Task> findAll(Specification<Task> specification);

  @Override
  @EntityGraph(attributePaths = {"taskStatus", "assignee", "labels"})
  Optional<Task> findById(Long id);
}
