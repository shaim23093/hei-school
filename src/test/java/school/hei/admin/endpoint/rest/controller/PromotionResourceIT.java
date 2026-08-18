package school.hei.admin.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import school.hei.admin.dto.request.PromotionCreateRequest;
import school.hei.admin.dto.request.PromotionUpdateRequest;
import school.hei.admin.dto.response.AuthResponse;
import school.hei.admin.entity.Promotion;

class PromotionResourceIT extends FacadeIT {
  @Autowired private TestRestTemplate restTemplate;

  @Test
  void admin_can_create_list_get_update_delete_promotion() {
    String adminToken = login("admin", "admin123");

    PromotionCreateRequest createRequest =
        PromotionCreateRequest.builder().name("Promo 2026").entryYear(2026).build();
    ResponseEntity<Promotion> created =
        post(adminToken, "/promotions", createRequest, Promotion.class);
    assertEquals(HttpStatus.OK, created.getStatusCode());
    assertNotNull(created.getBody().id());
    assertEquals("Promo 2026", created.getBody().name());
    assertEquals(2026, created.getBody().entryYear());
    UUID promoId = created.getBody().id();

    ResponseEntity<Promotion[]> listed = get(adminToken, "/promotions", Promotion[].class);
    assertEquals(HttpStatus.OK, listed.getStatusCode());
    assertTrue(listed.getBody().length >= 1);

    ResponseEntity<Promotion> got = get(adminToken, "/promotions/" + promoId, Promotion.class);
    assertEquals(HttpStatus.OK, got.getStatusCode());
    assertEquals("Promo 2026", got.getBody().name());

    PromotionUpdateRequest updateRequest =
        PromotionUpdateRequest.builder().name("Promo 2026 Updated").build();
    ResponseEntity<Promotion> updated =
        put(adminToken, "/promotions/" + promoId, updateRequest, Promotion.class);
    assertEquals(HttpStatus.OK, updated.getStatusCode());
    assertEquals("Promo 2026 Updated", updated.getBody().name());
    assertEquals(2026, updated.getBody().entryYear());

    ResponseEntity<Void> deleted = delete(adminToken, "/promotions/" + promoId, Void.class);
    assertEquals(HttpStatus.OK, deleted.getStatusCode());
  }

  @Test
  void admin_create_rejects_blank_name() {
    String adminToken = login("admin", "admin123");

    PromotionCreateRequest request =
        PromotionCreateRequest.builder().name("").entryYear(2026).build();
    ResponseEntity<String> response = post(adminToken, "/promotions", request, String.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void admin_create_rejects_invalid_entry_year() {
    String adminToken = login("admin", "admin123");

    PromotionCreateRequest request =
        PromotionCreateRequest.builder().name("Bad Year").entryYear(1999).build();
    ResponseEntity<String> response = post(adminToken, "/promotions", request, String.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void student_cannot_list_promotions() {
    String studentToken = login("student1", "student123");

    ResponseEntity<String> response = get(studentToken, "/promotions", String.class);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void student_cannot_create_promotion() {
    String studentToken = login("student1", "student123");

    PromotionCreateRequest request =
        PromotionCreateRequest.builder().name("Forbidden").entryYear(2026).build();
    ResponseEntity<String> response = post(studentToken, "/promotions", request, String.class);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void teacher_can_list_promotions() {
    String teacherToken = login("teacher1", "teacher123");

    ResponseEntity<Promotion[]> response = get(teacherToken, "/promotions", Promotion[].class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void teacher_cannot_create_promotion() {
    String teacherToken = login("teacher1", "teacher123");

    PromotionCreateRequest request =
        PromotionCreateRequest.builder().name("Forbidden").entryYear(2026).build();
    ResponseEntity<String> response = post(teacherToken, "/promotions", request, String.class);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
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

  private <T> ResponseEntity<T> post(String token, String url, Object body, Class<T> type) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), type);
  }

  private <T> ResponseEntity<T> put(String token, String url, Object body, Class<T> type) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(body, headers), type);
  }

  private <T> ResponseEntity<T> delete(String token, String url, Class<T> type) {
    return restTemplate.exchange(url, HttpMethod.DELETE, entity(token), type);
  }

  private HttpEntity<Void> entity(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    return new HttpEntity<>(headers);
  }
}
