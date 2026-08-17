package hexlet.code.controller;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<List<UserDTO>> index() {
    var users = userService.getAll();
    return ResponseEntity.ok().header("X-Total-Count", String.valueOf(users.size())).body(users);
  }

  @GetMapping("/{id}")
  public UserDTO show(@PathVariable Long id) {
    return userService.getById(id);
  }

  @PostMapping
  public ResponseEntity<UserDTO> create(@Valid @RequestBody UserCreateDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
  }

  @PutMapping("/{id}")
  @PreAuthorize("@userAccess.isOwner(#id, authentication)")
  public UserDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
    return userService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@userAccess.isOwner(#id, authentication)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
