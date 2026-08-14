package hexlet.code.component;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {
  private static final String ADMIN_EMAIL = "hexlet@example.com";

  private final UserRepository userRepository;
  private final UserService userService;

  public DataInitializer(UserRepository userRepository, UserService userService) {
    this.userRepository = userRepository;
    this.userService = userService;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (userRepository.findByEmail(ADMIN_EMAIL).isPresent()) {
      return;
    }

    var dto = new UserCreateDTO();
    dto.setEmail(ADMIN_EMAIL);
    dto.setPassword("qwerty");
    userService.create(dto);
  }
}
