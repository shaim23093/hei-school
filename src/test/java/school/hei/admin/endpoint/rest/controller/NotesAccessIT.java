package school.hei.admin.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import school.hei.admin.conf.FacadeIT;
import school.hei.admin.dto.request.GradeUpdateRequest;
import school.hei.admin.dto.request.LoginRequest;
import school.hei.admin.dto.response.AuthResponse;
import school.hei.admin.dto.response.GradeResponse;
import school.hei.admin.dto.response.GraduatedStudentResponse;
import school.hei.admin.dto.response.PromotionResultsResponse;
import school.hei.admin.dto.response.StudentGradesResponse;
import school.hei.admin.repository.AccountRepository;
import school.hei.admin.repository.CourseRepository;
import school.hei.admin.repository.ExamRepository;
import school.hei.admin.repository.GradeRepository;
import school.hei.admin.repository.StudentRepository;
import school.hei.admin.repository.model.JCourse;
import school.hei.admin.repository.model.JExam;
import school.hei.admin.repository.model.JGrade;
import school.hei.admin.repository.model.JStudent;

@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class NotesAccessIT extends FacadeIT {
  @Autowired private TestRestTemplate restTemplate;
  @Autowired private AccountRepository accountRepository;
  @Autowired private StudentRepository studentRepository;
  @Autowired private CourseRepository courseRepository;
  @Autowired private ExamRepository examRepository;
  @Autowired private GradeRepository gradeRepository;

  @Test
  void student_and_teacher_scoping_on_grades() {
    JStudent student1 =
        studentRepository
            .findByAccountId(accountRepository.findByUsername("student1").orElseThrow().getId())
            .orElseThrow();
    JStudent student2 =
        studentRepository
            .findByAccountId(accountRepository.findByUsername("student2").orElseThrow().getId())
            .orElseThrow();

    String student1Token = login("student1", "student123");
    String student2Token = login("student2", "student123");
    String teacher1Token = login("teacher1", "teacher123");
    String adminToken = login("admin", "admin123");

    ResponseEntity<StudentGradesResponse> ownGrades =
        get(
            student1Token,
            "/students/" + student1.getId() + "/grades",
            StudentGradesResponse.class);
    assertEquals(HttpStatus.OK, ownGrades.getStatusCode());
    assertFalse(ownGrades.getBody().results().isEmpty());
    assertEquals("STD24001", ownGrades.getBody().std());

    ResponseEntity<String> otherStudentGrades =
        get(student2Token, "/students/" + student1.getId() + "/grades", String.class);
    assertEquals(HttpStatus.FORBIDDEN, otherStudentGrades.getStatusCode());

    ResponseEntity<String> transcript =
        get(
            student1Token,
            "/students/" + student1.getId() + "/transcript?semester=1",
            String.class);
    assertEquals(HttpStatus.OK, transcript.getStatusCode());

    JGrade prog1Grade = gradeOf(student1, "PROG1", "CC1");
    ResponseEntity<GradeResponse> updateOk =
        put(
            teacher1Token,
            "/grades/" + prog1Grade.getId(),
            new GradeUpdateRequest(13),
            GradeResponse.class);
    assertEquals(HttpStatus.OK, updateOk.getStatusCode());
    assertEquals(13.0, updateOk.getBody().value());

    JGrade tn1Grade = gradeOf(student2, "TN1", "Exam");
    ResponseEntity<GradeResponse> updateForbidden =
        put(
            teacher1Token,
            "/grades/" + tn1Grade.getId(),
            new GradeUpdateRequest(12),
            GradeResponse.class);
    assertEquals(HttpStatus.FORBIDDEN, updateForbidden.getStatusCode());

    ResponseEntity<String> history =
        get(teacher1Token, "/grades/" + prog1Grade.getId() + "/history", String.class);
    assertEquals(HttpStatus.OK, history.getStatusCode());
    assertTrue(history.getBody().contains("teacher1"));
    assertTrue(history.getBody().contains("12.0"));

    UUID promotionId = student1.getPromotion().getId();
    ResponseEntity<GraduatedStudentResponse[]> diplomas =
        get(
            teacher1Token,
            "/promotions/" + promotionId + "/diplomes",
            GraduatedStudentResponse[].class);
    assertEquals(HttpStatus.OK, diplomas.getStatusCode());
    assertTrue(List.of(diplomas.getBody()).stream().anyMatch(d -> d.std().equals("STD24002")));

    ResponseEntity<PromotionResultsResponse> results =
        get(adminToken, "/promotions/" + promotionId + "/results", PromotionResultsResponse.class);
    assertEquals(HttpStatus.OK, results.getStatusCode());
    assertEquals(2, results.getBody().results().size());
  }

  private JGrade gradeOf(JStudent student, String courseCode, String examTitle) {
    JCourse course =
        courseRepository.findAll().stream()
            .filter(c -> c.getCode().equals(courseCode))
            .findFirst()
            .orElseThrow();
    JExam exam =
        examRepository.findAll().stream()
            .filter(
                e ->
                    e.getCourse().getId().equals(course.getId())
                        && e.getPromotion().getId().equals(student.getPromotion().getId())
                        && e.getTitle().equals(examTitle))
            .findFirst()
            .orElseThrow();
    return gradeRepository.findByStudentIdAndExamId(student.getId(), exam.getId()).get(0);
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

  private <T> ResponseEntity<T> put(String token, String url, Object body, Class<T> type) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(body, headers), type);
  }

  private HttpEntity<Void> entity(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    return new HttpEntity<>(headers);
  }
}
