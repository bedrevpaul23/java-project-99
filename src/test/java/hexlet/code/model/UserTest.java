package hexlet.code.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserTest {
  @Autowired private TestEntityManager entityManager;

  @Test
  void persistsUserWithAuditingFields() {
    var user = new User();
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmail("john.doe@example.com");
    user.setPasswordDigest("password-digest");

    var persistedUser = entityManager.persistAndFlush(user);

    assertThat(persistedUser.getId()).isNotNull();
    assertThat(persistedUser.getCreatedAt()).isNotNull();
    assertThat(persistedUser.getUpdatedAt()).isNotNull();

    var count =
        (Number)
            entityManager
                .getEntityManager()
                .createNativeQuery("select count(*) from users where id = :id")
                .setParameter("id", persistedUser.getId())
                .getSingleResult();

    assertThat(count.longValue()).isEqualTo(1);
  }

  @Test
  void comparesPersistedEntityWithHibernateReference() {
    var user = new User();
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmail("proxy@example.com");
    user.setPasswordDigest("password-digest");
    var persistedUser = entityManager.persistAndFlush(user);

    entityManager.clear();
    var reference =
        entityManager.getEntityManager().getReference(User.class, persistedUser.getId());

    assertThat(persistedUser).isEqualTo(reference);
    assertThat(reference).isEqualTo(persistedUser);
  }
}
