package school.hei.admin.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import jakarta.persistence.EntityNotFoundException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleApiException() {
    ApiException ex = new ApiException("Not allowed", FORBIDDEN);
    Map<String, Object> body = handler.handleApiException(ex).getBody();
    assertEquals("Not allowed", body.get("message"));
    assertEquals(FORBIDDEN.value(), body.get("status"));
  }

  @Test
  void handleAccessDenied() {
    Map<String, Object> body =
        handler.handleAccessDenied(new AccessDeniedException("denied")).getBody();
    assertEquals("Forbidden: insufficient role", body.get("message"));
    assertEquals(FORBIDDEN.value(), body.get("status"));
  }

  @Test
  void handleEntityNotFound() {
    Map<String, Object> body =
        handler.handleEntityNotFound(new EntityNotFoundException("missing")).getBody();
    assertEquals("missing", body.get("message"));
    assertEquals(NOT_FOUND.value(), body.get("status"));
  }

  @Test
  void handleValidation() {
    var bindingResult = new BeanPropertyBindingResult(new Object(), "obj");
    bindingResult.addError(new FieldError("obj", "field", "must not be null"));
    MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
    Map<String, Object> body = handler.handleValidation(ex).getBody();
    assertNotNull(body.get("message"));
    assertEquals(BAD_REQUEST.value(), body.get("status"));
  }

  @Test
  void handleUnreadable() {
    Map<String, Object> body =
        handler.handleUnreadable(new HttpMessageNotReadableException("bad json")).getBody();
    assertEquals("Malformed request body or invalid value", body.get("message"));
    assertEquals(BAD_REQUEST.value(), body.get("status"));
  }

  @Test
  void handleTypeMismatch() {
    MethodArgumentTypeMismatchException ex =
        new MethodArgumentTypeMismatchException("value", String.class, "param", null, null);
    Map<String, Object> body = handler.handleTypeMismatch(ex).getBody();
    assertEquals("Invalid parameter value for 'param'", body.get("message"));
    assertEquals(BAD_REQUEST.value(), body.get("status"));
  }

  @Test
  void handleDataIntegrity() {
    Map<String, Object> body =
        handler.handleDataIntegrity(new DataIntegrityViolationException("dup")).getBody();
    assertEquals("Conflict: resource already exists", body.get("message"));
    assertEquals(CONFLICT.value(), body.get("status"));
  }

  @Test
  void handleGenericException() {
    Map<String, Object> body =
        handler.handleGenericException(new RuntimeException("boom")).getBody();
    assertEquals("boom", body.get("message"));
    assertEquals(INTERNAL_SERVER_ERROR.value(), body.get("status"));
  }
}
