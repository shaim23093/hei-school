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
import school.hei.admin.dto.request.StudentCreateRequest;
import school.hei.admin.dto.request.StudentUpdateRequest;
import school.hei.admin.dto.response.AuthResponse;
import school.hei.admin.entity.Student;

class StudentResourceIT extends FacadeIT {
  @Autowired private TestRestTemplate restTemplate;

  @Test
  void admin_can_create_list_get_update_delete_student() {
    String adminToken = login("admin", "admin123");

    StudentCreateRequest createRequest =
        StudentCreateRequest.builder()
            .username("newstudent")
            .password("strongpassword")
            .promotionId(findAnyPromotionId(adminToken))
            .std("STD24010")
            .name("Test")
            .firstName("Student")
            .email("test.student@hei.school")
            .build();
    ResponseEntity<Student> created = post(adminToken, "/students", createRequest, Student.class);
    assertEquals(HttpStatus.OK, created.getStatusCode());
    assertNotNull(created.getBody().id());
    assertEquals("STD24010", created.getBody().std());
    UUID studentId = created.getBody().id();

    ResponseEntity<Student[]> listed = get(adminToken, "/students", Student[].class);
    assertEquals(HttpStatus.OK, listed.getStatusCode());
    assertTrue(listed.getBody().length >= 3);

    ResponseEntity<Student> got = get(adminToken, "/students/" + studentId, Student.class);
    assertEquals(HttpStatus.OK, got.getStatusCode());
    assertEquals("Test", got.getBody().name());

    StudentUpdateRequest updateRequest = StudentUpdateRequest.builder().name("Updated").build();
    ResponseEntity<Student> updated =
        put(adminToken, "/students/" + studentId, updateRequest, Student.class);
    assertEquals(HttpStatus.OK, updated.getStatusCode());
    assertEquals("Updated", updated.getBody().name());

    ResponseEntity<Void> deleted = delete(adminToken, "/students/" + studentId, Void.class);
    assertEquals(HttpStatus.OK, deleted.getStatusCode());
  }

  @Test
  void admin_create_rejects_invalid_email() {
    String adminToken = login("admin", "admin123");

    StudentCreateRequest request =
        StudentCreateRequest.builder()
            .username("badstudent")
            .password("strongpassword")
            .promotionId(findAnyPromotionId(adminToken))
            .std("STD24011")
            .name("Bad")
            .firstName("Email")
            .email("not-an-email")
            .build();
    ResponseEntity<String> response = post(adminToken, "/students", request, String.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void admin_create_rejects_short_password() {
    String adminToken = login("admin", "admin123");

    StudentCreateRequest request =
        StudentCreateRequest.builder()
            .username("badstudent")
            .password("short")
            .promotionId(findAnyPromotionId(adminToken))
            .std("STD24012")
            .name("Bad")
            .firstName("Pass")
            .email("bad.pass@hei.school")
            .build();
    ResponseEntity<String> response = post(adminToken, "/students", request, String.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void admin_create_rejects_invalid_std_format() {
    String adminToken = login("admin", "admin123");

    StudentCreateRequest request =
        StudentCreateRequest.builder()
            .username("badstudent")
            .password("strongpassword")
            .promotionId(findAnyPromotionId(adminToken))
            .std("INVALID")
            .name("Bad")
            .firstName("Std")
            .email("bad.std@hei.school")
            .build();
    ResponseEntity<String> response = post(adminToken, "/students", request, String.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void admin_create_rejects_blank_username() {
    String adminToken = login("admin", "admin123");

    StudentCreateRequest request =
        StudentCreateRequest.builder()
            .username("")
            .password("strongpassword")
            .promotionId(findAnyPromotionId(adminToken))
            .std("STD24013")
            .name("Bad")
            .firstName("User")
            .email("bad.user@hei.school")
            .build();
    ResponseEntity<String> response = post(adminToken, "/students", request, String.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void student_cannot_list_students() {
    String studentToken = login("student1", "student123");

    ResponseEntity<String> response = get(studentToken, "/students", String.class);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void student_cannot_create_student() {
    String studentToken = login("student1", "student123");

    StudentCreateRequest request =
        StudentCreateRequest.builder()
            .username("newstudent")
            .password("strongpassword")
            .std("STD24014")
            .name("Forbidden")
            .firstName("Create")
            .email("forbidden.create@hei.school")
            .build();
    ResponseEntity<String> response = post(studentToken, "/students", request, String.class);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void student_cannot_delete_student() {
    String studentToken = login("student1", "student123");
    String adminToken = login("admin", "admin123");

    StudentCreateRequest createRequest =
        StudentCreateRequest.builder()
            .username("to-delete")
            .password("strongpassword")
            .promotionId(findAnyPromotionId(adminToken))
            .std("STD24015")
            .name("ToDelete")
            .firstName("Temp")
            .email("to.delete@hei.school")
            .build();
    ResponseEntity<Student> created = post(adminToken, "/students", createRequest, Student.class);
    UUID id = created.getBody().id();

    ResponseEntity<String> response = delete(studentToken, "/students/" + id, String.class);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void teacher_can_list_students() {
    String teacherToken = login("teacher1", "teacher123");

    ResponseEntity<Student[]> response = get(teacherToken, "/students", Student[].class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void teacher_cannot_create_student() {
    String teacherToken = login("teacher1", "teacher123");

    StudentCreateRequest request =
        StudentCreateRequest.builder()
            .username("newstudent")
            .password("strongpassword")
            .std("STD24016")
            .name("Forbidden")
            .firstName("Teacher")
            .email("forbidden.teacher@hei.school")
            .build();
    ResponseEntity<String> response = post(teacherToken, "/students", request, String.class);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  private UUID findAnyPromotionId(String adminToken) {
    ResponseEntity<String> response = get(adminToken, "/students", String.class);
    assertTrue(response.getBody().contains("promotionId"));
    String body = response.getBody();
    int idx = body.indexOf("\"promotionId\":\"");
    if (idx < 0) {
      idx = body.indexOf("\"promotionId\":\"null");
      if (idx >= 0) {
        idx = body.indexOf("promotionId", idx + 1);
      }
    }
    String sub = body.substring(idx);
    int start = sub.indexOf(":\"") + 2;
    int end = sub.indexOf("\"", start);
    if (start < 2 || end < 0) {
      idx = body.indexOf("\"promotionId\":");
      sub = body.substring(idx);
      start = sub.indexOf(":") + 1;
      end = sub.indexOf(",", start);
      if (end < 0) end = sub.indexOf("}", start);
      return UUID.fromString(sub.substring(start, end).trim().replace("\"", ""));
    }
    return UUID.fromString(sub.substring(start, end));
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
