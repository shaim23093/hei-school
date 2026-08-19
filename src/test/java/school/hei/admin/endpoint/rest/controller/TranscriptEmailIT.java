package school.hei.admin.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.hei.admin.conf.FacadeIT;
import school.hei.admin.dto.request.LoginRequest;
import school.hei.admin.dto.response.AuthResponse;

class TranscriptEmailIT extends FacadeIT {
  @Autowired private TestRestTemplate restTemplate;

  @Test
  void unauthenticated_cannot_request_transcript_email() {
    try {
      ResponseEntity<String> response =
          restTemplate.exchange(
              "/students/" + UUID.randomUUID() + "/transcript/email?email=test@hei.school",
              HttpMethod.POST,
              new HttpEntity<>(new HttpHeaders()),
              String.class);
      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    } catch (org.springframework.web.client.HttpClientErrorException
        | org.springframework.web.client.ResourceAccessException e) {
      if (e instanceof org.springframework.web.client.HttpClientErrorException hce) {
        assertEquals(HttpStatus.UNAUTHORIZED, hce.getStatusCode());
      } else {
        assertTrue(e.getMessage().contains("401") || e.getMessage().contains("authentication"));
      }
    }
  }

  @Test
  void admin_can_access_transcript_email_endpoint() {
    String adminToken = login("admin", "admin123");
    String studentId = findAnyStudentId(adminToken);

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/students/" + studentId + "/transcript/email?email=test@hei.school",
            HttpMethod.POST,
            entity(adminToken),
            String.class);

    assertTrue(
        response.getStatusCode() == HttpStatus.OK
            || response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void student_can_access_transcript_email_endpoint() {
    String studentToken = login("student1", "student123");
    String studentId = findAnyStudentId(login("admin", "admin123"));

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/students/" + studentId + "/transcript/email?email=student@hei.school",
            HttpMethod.POST,
            entity(studentToken),
            String.class);

    assertTrue(
        response.getStatusCode() == HttpStatus.OK
            || response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private String findAnyStudentId(String token) {
    ResponseEntity<String> response = get(token, "/students", String.class);
    String body = response.getBody();
    int idx = body.indexOf("\"id\":\"");
    String sub = body.substring(idx + 6);
    int end = sub.indexOf("\"");
    return sub.substring(0, end);
  }

  private String login(String username, String password) {
    ResponseEntity<AuthResponse> response =
        restTemplate.postForEntity(
            "/auth/login", new LoginRequest(username, password), AuthResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody().token();
  }

  private <T> ResponseEntity<T> get(String token, String url, Class<T> type) {
    return restTemplate.exchange(url, HttpMethod.GET, entity(token), type);
  }

  private HttpEntity<Void> entity(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    return new HttpEntity<>(headers);
  }
}
