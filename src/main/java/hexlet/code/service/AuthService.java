package hexlet.code.service;

import hexlet.code.dto.AuthRequest;

public interface AuthService {
  String authenticate(AuthRequest request);
}
