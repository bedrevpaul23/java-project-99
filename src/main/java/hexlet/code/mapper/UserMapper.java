package hexlet.code.mapper;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
  public UserDTO map(User user) {
    var dto = new UserDTO();
    dto.setId(user.getId());
    dto.setEmail(user.getEmail());
    dto.setFirstName(user.getFirstName());
    dto.setLastName(user.getLastName());
    dto.setCreatedAt(user.getCreatedAt());
    dto.setUpdatedAt(user.getUpdatedAt());
    return dto;
  }

  public User map(UserCreateDTO dto) {
    var user = new User();
    user.setEmail(dto.getEmail());
    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    return user;
  }

  public void update(UserUpdateDTO dto, User user) {
    if (dto.getEmail().isPresent()) {
      user.setEmail(dto.getEmail().get());
    }
    if (dto.getFirstName().isPresent()) {
      user.setFirstName(dto.getFirstName().orElse(null));
    }
    if (dto.getLastName().isPresent()) {
      user.setLastName(dto.getLastName().orElse(null));
    }
  }
}
