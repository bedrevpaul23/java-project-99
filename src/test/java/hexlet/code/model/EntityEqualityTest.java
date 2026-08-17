package hexlet.code.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class EntityEqualityTest {
  @Test
  void userEqualityIsIdBasedAndHashCodeIsStable() {
    assertIdBasedEquality(User::new, User::setId);
  }

  @Test
  void taskStatusEqualityIsIdBasedAndHashCodeIsStable() {
    assertIdBasedEquality(TaskStatus::new, TaskStatus::setId);
  }

  @Test
  void labelEqualityIsIdBasedAndHashCodeIsStable() {
    assertIdBasedEquality(Label::new, Label::setId);
  }

  @Test
  void taskEqualityIsIdBasedAndHashCodeIsStable() {
    assertIdBasedEquality(Task::new, Task::setId);
  }

  private <T> void assertIdBasedEquality(Supplier<T> factory, BiConsumer<T, Long> setId) {
    var first = factory.get();
    var second = factory.get();
    var hashCodeBeforeIdAssignment = first.hashCode();

    assertThat(first).isEqualTo(first);
    assertThat(first).isNotEqualTo(second);

    setId.accept(first, 1L);
    setId.accept(second, 1L);
    assertThat(first).isEqualTo(second);
    assertThat(first.hashCode()).isEqualTo(hashCodeBeforeIdAssignment);

    setId.accept(second, 2L);
    assertThat(first).isNotEqualTo(second);
  }
}
