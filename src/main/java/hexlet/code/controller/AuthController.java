package hexlet.code.controller;

import hexlet.code.dto.AuthRequest;
import hexlet.code.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/api/login")
  public ResponseEntity<String> login(@Valid @RequestBody AuthRequest request) {
    return ResponseEntity.ok(authService.authenticate(request));
  }
}
