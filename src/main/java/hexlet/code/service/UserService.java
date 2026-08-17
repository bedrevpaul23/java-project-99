package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import java.util.List;

public interface UserService {
  List<UserDTO> getAll();

  UserDTO getById(Long id);

  UserDTO create(UserCreateDTO dto);

  UserDTO update(Long id, UserUpdateDTO dto);

  void delete(Long id);
}
