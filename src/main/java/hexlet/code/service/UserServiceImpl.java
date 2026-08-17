package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(
      UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDTO> getAll() {
    return userRepository.findAll().stream().map(userMapper::map).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public UserDTO getById(Long id) {
    return userMapper.map(findById(id));
  }

  @Override
  @Transactional
  public UserDTO create(UserCreateDTO dto) {
    var user = userMapper.map(dto);
    user.setPasswordDigest(passwordEncoder.encode(dto.getPassword()));
    return userMapper.map(userRepository.save(user));
  }

  @Override
  @Transactional
  public UserDTO update(Long id, UserUpdateDTO dto) {
    var user = findById(id);
    userMapper.update(dto, user);
    if (dto.getPassword().isPresent()) {
      user.setPasswordDigest(passwordEncoder.encode(dto.getPassword().get()));
    }
    return userMapper.map(userRepository.save(user));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    userRepository.delete(findById(id));
  }

  private User findById(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
  }
}
