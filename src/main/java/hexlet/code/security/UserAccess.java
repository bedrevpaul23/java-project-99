package hexlet.code.security;

import hexlet.code.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userAccess")
public class UserAccess {
  private final UserRepository userRepository;

  public UserAccess(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean isOwner(Long userId, Authentication authentication) {
    return userRepository
        .findById(userId)
        .map(user -> user.getEmail().equals(authentication.getName()))
        .orElse(true);
  }
}
