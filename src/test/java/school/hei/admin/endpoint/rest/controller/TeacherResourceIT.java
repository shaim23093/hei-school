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
import school.hei.admin.dto.request.TeacherCreateRequest;
import school.hei.admin.dto.request.TeacherUpdateRequest;
import school.hei.admin.dto.response.AuthResponse;
import school.hei.admin.entity.Teacher;

class TeacherResourceIT extends FacadeIT {
  @Autowired private TestRestTemplate restTemplate;

  @Test
  void admin_can_create_list_get_update_delete_teacher() {
    String adminToken = login("admin", "admin123");

    TeacherCreateRequest createRequest =
        TeacherCreateRequest.builder()
            .username("newteacher")
            .password("strongpassword")
            .name("Test")
            .firstName("Teacher")
            .email("test.teacher@hei.school")
            .build();
    ResponseEntity<Teacher> created = post(adminToken, "/teachers", createRequest, Teacher.class);
    assertEquals(HttpStatus.OK, created.getStatusCode());
    assertNotNull(created.getBody().id());
    assertEquals("Test", created.getBody().name());
    UUID teacherId = created.getBody().id();

    ResponseEntity<Teacher[]> listed = get(adminToken, "/teachers", Teacher[].class);
    assertEquals(HttpStatus.OK, listed.getStatusCode());
    assertTrue(listed.getBody().length >= 3);

    ResponseEntity<Teacher> got = get(adminToken, "/teachers/" + teacherId, Teacher.class);
    assertEquals(HttpStatus.OK, got.getStatusCode());
    assertEquals("Test", got.getBody().name());

    TeacherUpdateRequest updateRequest = TeacherUpdateRequest.builder().name("Updated").build();
    ResponseEntity<Teacher> updated =
        put(adminToken, "/teachers/" + teacherId, updateRequest, Teacher.class);
    assertEquals(HttpStatus.OK, updated.getStatusCode());
    assertEquals("Updated", updated.getBody().name());

    ResponseEntity<Void> deleted = delete(adminToken, "/teachers/" + teacherId, Void.class);
    assertEquals(HttpStatus.OK, deleted.getStatusCode());
  }

  @Test
  void admin_create_rejects_invalid_email() {
    String adminToken = login("admin", "admin123");

    TeacherCreateRequest request =
        TeacherCreateRequest.builder()
            .username("badteacher")
            .password("strongpassword")
            .name("Bad")
            .firstName("Email")
            .email("not-an-email")
            .build();
    ResponseEntity<String> response = post(adminToken, "/teachers", request, String.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void admin_create_rejects_short_password() {
    String adminToken = login("admin", "admin123");

    TeacherCreateRequest request =
        TeacherCreateRequest.builder()
            .username("badteacher")
            .password("short")
            .name("Bad")
            .firstName("Pass")
            .email("bad.pass@hei.school")
            .build();
    ResponseEntity<String> response = post(adminToken, "/teachers", request, String.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void teacher_can_list_teachers() {
    String teacherToken = login("teacher1", "teacher123");

    ResponseEntity<Teacher[]> response = get(teacherToken, "/teachers", Teacher[].class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void teacher_cannot_create_teacher() {
    String teacherToken = login("teacher1", "teacher123");

    TeacherCreateRequest request =
        TeacherCreateRequest.builder()
            .username("newteacher")
            .password("strongpassword")
            .name("Forbidden")
            .firstName("Create")
            .email("forbidden.create@hei.school")
            .build();
    ResponseEntity<String> response = post(teacherToken, "/teachers", request, String.class);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void student_cannot_list_teachers() {
    String studentToken = login("student1", "student123");

    ResponseEntity<String> response = get(studentToken, "/teachers", String.class);
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
