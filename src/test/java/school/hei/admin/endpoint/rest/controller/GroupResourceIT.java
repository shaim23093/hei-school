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
import school.hei.admin.dto.request.GroupCreateRequest;
import school.hei.admin.dto.request.GroupUpdateRequest;
import school.hei.admin.dto.request.LoginRequest;
import school.hei.admin.dto.response.AuthResponse;
import school.hei.admin.entity.Group;
import school.hei.admin.entity.enums.Path;

class GroupResourceIT extends FacadeIT {
  @Autowired private TestRestTemplate restTemplate;

  @Test
  void admin_can_create_list_get_update_delete_group() {
    String adminToken = login("admin", "admin123");

    GroupCreateRequest createRequest =
        GroupCreateRequest.builder().name("NewGroup").path(Path.EL).build();
    ResponseEntity<Group> created = post(adminToken, "/groups", createRequest, Group.class);
    assertEquals(HttpStatus.OK, created.getStatusCode());
    assertNotNull(created.getBody().id());
    assertEquals("NewGroup", created.getBody().name());
    assertEquals(Path.EL, created.getBody().path());
    UUID groupId = created.getBody().id();

    ResponseEntity<Group[]> listed = get(adminToken, "/groups", Group[].class);
    assertEquals(HttpStatus.OK, listed.getStatusCode());
    assertTrue(listed.getBody().length >= 1);

    ResponseEntity<Group> got = get(adminToken, "/groups/" + groupId, Group.class);
    assertEquals(HttpStatus.OK, got.getStatusCode());
    assertEquals("NewGroup", got.getBody().name());

    GroupUpdateRequest updateRequest =
        GroupUpdateRequest.builder().name("UpdatedGroup").path(Path.TN).build();
    ResponseEntity<Group> updated =
        put(adminToken, "/groups/" + groupId, updateRequest, Group.class);
    assertEquals(HttpStatus.OK, updated.getStatusCode());
    assertEquals("UpdatedGroup", updated.getBody().name());
    assertEquals(Path.TN, updated.getBody().path());

    ResponseEntity<Void> deleted = delete(adminToken, "/groups/" + groupId, Void.class);
    assertEquals(HttpStatus.OK, deleted.getStatusCode());
  }

  @Test
  void admin_create_rejects_blank_name() {
    String adminToken = login("admin", "admin123");

    GroupCreateRequest request = GroupCreateRequest.builder().name("").path(Path.EL).build();
    ResponseEntity<String> response = post(adminToken, "/groups", request, String.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void admin_create_rejects_null_path() {
    String adminToken = login("admin", "admin123");

    GroupCreateRequest request = GroupCreateRequest.builder().name("NoPath").path(null).build();
    ResponseEntity<String> response = post(adminToken, "/groups", request, String.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void student_cannot_list_groups() {
    String studentToken = login("student1", "student123");

    ResponseEntity<String> response = get(studentToken, "/groups", String.class);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void student_cannot_create_group() {
    String studentToken = login("student1", "student123");

    GroupCreateRequest request =
        GroupCreateRequest.builder().name("Forbidden").path(Path.EL).build();
    ResponseEntity<String> response = post(studentToken, "/groups", request, String.class);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void teacher_can_list_groups() {
    String teacherToken = login("teacher1", "teacher123");

    ResponseEntity<Group[]> response = get(teacherToken, "/groups", Group[].class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void teacher_cannot_create_group() {
    String teacherToken = login("teacher1", "teacher123");

    GroupCreateRequest request =
        GroupCreateRequest.builder().name("Forbidden").path(Path.TN).build();
    ResponseEntity<String> response = post(teacherToken, "/groups", request, String.class);
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
