package school.hei.admin.exception;

import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
    return error(ex.getStatus(), ex.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
    return error(HttpStatus.FORBIDDEN, "Forbidden: insufficient role");
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
    return error(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    String errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
    return error(HttpStatus.BAD_REQUEST, errors.isEmpty() ? "Validation failed" : errors);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex) {
    return error(HttpStatus.BAD_REQUEST, "Malformed request body or invalid value");
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Map<String, Object>> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    return error(HttpStatus.BAD_REQUEST, "Invalid parameter value for '" + ex.getName() + "'");
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, Object>> handleDataIntegrity(
      DataIntegrityViolationException ex) {
    return error(HttpStatus.CONFLICT, "Conflict: resource already exists");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
    log.error("Unhandled exception", ex);
    return error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
  }

  private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
    Map<String, Object> body = new HashMap<>();
    body.put("message", message);
    body.put("status", status.value());
    body.put("timestamp", Instant.now());
    return ResponseEntity.status(status).body(body);
  }
}
