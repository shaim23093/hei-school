package school.hei.admin.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

class GraduationExcelIT extends FacadeIT {
  @Autowired private TestRestTemplate restTemplate;

  @Test
  void admin_can_download_diplomas_excel() {
    String adminToken = login("admin", "admin123");
    String promotionId = findAnyPromotionId(adminToken);

    ResponseEntity<byte[]> response =
        restTemplate.exchange(
            "/promotions/" + promotionId + "/diplomes/excel",
            HttpMethod.GET,
            entity(adminToken),
            byte[].class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().length > 0);
    assertTrue(response.getHeaders().getContentType().toString().contains("octet-stream"));
  }

  @Test
  void teacher_can_download_diplomas_excel() {
    String teacherToken = login("teacher1", "teacher123");
    String promotionId = findAnyPromotionId(login("admin", "admin123"));

    ResponseEntity<byte[]> response =
        restTemplate.exchange(
            "/promotions/" + promotionId + "/diplomes/excel",
            HttpMethod.GET,
            entity(teacherToken),
            byte[].class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void unauthenticated_cannot_download_diplomas_excel() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/promotions/" + java.util.UUID.randomUUID() + "/diplomes/excel",
            HttpMethod.GET,
            new HttpEntity<>(new HttpHeaders()),
            String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  private String findAnyPromotionId(String token) {
    ResponseEntity<String> response = get(token, "/promotions", String.class);
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
