package hexlet.code.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Void> handleResourceNotFound(ResourceNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Void> handleAuthentication(AuthenticationException exception) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Void> handleDataIntegrityViolation(
      DataIntegrityViolationException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }
}
