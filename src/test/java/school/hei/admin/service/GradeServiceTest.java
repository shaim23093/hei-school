package school.hei.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import school.hei.admin.dto.request.GradeUpdateRequest;
import school.hei.admin.dto.response.CourseGradeResult;
import school.hei.admin.dto.response.StudentSummary;
import school.hei.admin.entity.enums.Path;
import school.hei.admin.entity.enums.Role;
import school.hei.admin.exception.ForbiddenException;
import school.hei.admin.exception.NotFoundException;
import school.hei.admin.exception.UnprocessableEntityException;
import school.hei.admin.repository.AccountRepository;
import school.hei.admin.repository.CourseGroupRepository;
import school.hei.admin.repository.CourseRepository;
import school.hei.admin.repository.ExamRepository;
import school.hei.admin.repository.GradeHistoryRepository;
import school.hei.admin.repository.GradeRepository;
import school.hei.admin.repository.StudentGroupRepository;
import school.hei.admin.repository.StudentRepository;
import school.hei.admin.repository.TeacherCourseRepository;
import school.hei.admin.repository.TeacherRepository;
import school.hei.admin.repository.model.JAccount;
import school.hei.admin.repository.model.JCourse;
import school.hei.admin.repository.model.JCourseGroup;
import school.hei.admin.repository.model.JExam;
import school.hei.admin.repository.model.JGrade;
import school.hei.admin.repository.model.JGroup;
import school.hei.admin.repository.model.JPromotion;
import school.hei.admin.repository.model.JStudent;
import school.hei.admin.repository.model.JStudentGroup;
import school.hei.admin.repository.model.JTeacher;
import school.hei.admin.repository.model.JTeacherCourse;

class GradeServiceTest {
  private final AccountRepository accountRepository = mock(AccountRepository.class);
  private final StudentRepository studentRepository = mock(StudentRepository.class);
  private final TeacherRepository teacherRepository = mock(TeacherRepository.class);
  private final TeacherCourseRepository teacherCourseRepository =
      mock(TeacherCourseRepository.class);
  private final StudentGroupRepository studentGroupRepository = mock(StudentGroupRepository.class);
  private final CourseGroupRepository courseGroupRepository = mock(CourseGroupRepository.class);
  private final CourseRepository courseRepository = mock(CourseRepository.class);
  private final ExamRepository examRepository = mock(ExamRepository.class);
  private final GradeRepository gradeRepository = mock(GradeRepository.class);
  private final GradeHistoryRepository gradeHistoryRepository = mock(GradeHistoryRepository.class);

  private final GradeService service =
      new GradeService(
          accountRepository,
          studentRepository,
          teacherRepository,
          teacherCourseRepository,
          studentGroupRepository,
          courseGroupRepository,
          courseRepository,
          examRepository,
          gradeRepository,
          gradeHistoryRepository);

  private JPromotion promotion;
  private JGroup group;
  private JStudent student;

  @BeforeEach
  void setUp() {
    promotion =
        JPromotion.builder().id(UUID.randomUUID()).name("Promo 2024").entryYear(2024).build();
    group = JGroup.builder().id(UUID.randomUUID()).name("Group1").path(Path.EL).build();
    student =
        JStudent.builder()
            .id(UUID.randomUUID())
            .std("STD24001")
            .name("Andria")
            .firstName("Tiana")
            .promotion(promotion)
            .build();
    when(studentGroupRepository.findByStudentId(student.getId()))
        .thenReturn(
            List.of(
                JStudentGroup.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .group(group)
                    .semester(1)
                    .build()));
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  private void linkCourse(JCourse course) {
    when(courseGroupRepository.findByPromotionIdAndGroupIdIn(
            promotion.getId(), List.of(group.getId())))
        .thenReturn(
            List.of(
                JCourseGroup.builder()
                    .id(UUID.randomUUID())
                    .course(course)
                    .group(group)
                    .promotion(promotion)
                    .build()));
  }

  private JCourse course(String code, int semester, int credits) {
    return JCourse.builder()
        .id(UUID.randomUUID())
        .code(code)
        .name(code)
        .semester(semester)
        .credits(credits)
        .path(Path.EL)
        .build();
  }

  private JExam exam(JCourse course, double coefficient) {
    return JExam.builder()
        .id(UUID.randomUUID())
        .course(course)
        .promotion(promotion)
        .title("Exam")
        .dateTime(Instant.now())
        .durationMinutes(120)
        .coefficient(coefficient)
        .build();
  }

  private JAccount account(Role role) {
    return JAccount.builder().id(UUID.randomUUID()).username("u").role(role).build();
  }

  private void authenticate(JAccount account) {
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(account.getId().toString(), null, List.of()));
    when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
  }

  @Test
  void computes_weighted_average_when_all_exams_are_graded() {
    JCourse course = course("PROG1", 1, 6);
    JExam cc1 = exam(course, 1);
    JExam exam1 = exam(course, 2);
    linkCourse(course);
    when(courseRepository.findAllById(List.of(course.getId()))).thenReturn(List.of(course));
    when(examRepository.findByCourseIdInAndPromotionId(List.of(course.getId()), promotion.getId()))
        .thenReturn(List.of(cc1, exam1));
    when(gradeRepository.findByStudentId(student.getId()))
        .thenReturn(
            List.of(
                JGrade.builder().id(UUID.randomUUID()).student(student).exam(cc1).value(12).build(),
                JGrade.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .exam(exam1)
                    .value(15)
                    .build()));

    List<CourseGradeResult> results = service.computeCourseResults(student, null);

    assertEquals(1, results.size());
    assertEquals(14.0, results.get(0).average(), 0.001);
    assertTrue(results.get(0).complete());
    assertTrue(results.get(0).validated());
  }

  @Test
  void marks_course_incomplete_when_an_exam_has_no_grade() {
    JCourse course = course("PROG1", 1, 6);
    JExam cc1 = exam(course, 1);
    JExam exam1 = exam(course, 2);
    linkCourse(course);
    when(courseRepository.findAllById(List.of(course.getId()))).thenReturn(List.of(course));
    when(examRepository.findByCourseIdInAndPromotionId(List.of(course.getId()), promotion.getId()))
        .thenReturn(List.of(cc1, exam1));
    when(gradeRepository.findByStudentId(student.getId()))
        .thenReturn(
            List.of(
                JGrade.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .exam(cc1)
                    .value(12)
                    .build()));

    List<CourseGradeResult> results = service.computeCourseResults(student, null);

    CourseGradeResult result = results.get(0);
    assertNull(result.average());
    assertFalse(result.complete());
    assertFalse(result.validated());
  }

  @Test
  void filters_courses_by_semester() {
    JCourse sem1 = course("PROG1", 1, 6);
    JCourse sem2 = course("WEB1", 2, 5);
    when(courseGroupRepository.findByPromotionIdAndGroupIdIn(
            promotion.getId(), List.of(group.getId())))
        .thenReturn(
            List.of(
                JCourseGroup.builder()
                    .id(UUID.randomUUID())
                    .course(sem1)
                    .group(group)
                    .promotion(promotion)
                    .build(),
                JCourseGroup.builder()
                    .id(UUID.randomUUID())
                    .course(sem2)
                    .group(group)
                    .promotion(promotion)
                    .build()));
    when(courseRepository.findAllById(List.of(sem1.getId(), sem2.getId())))
        .thenReturn(List.of(sem1, sem2));
    when(examRepository.findByCourseIdInAndPromotionId(
            List.of(sem1.getId(), sem2.getId()), promotion.getId()))
        .thenReturn(List.of());
    when(gradeRepository.findByStudentId(student.getId())).thenReturn(List.of());

    List<CourseGradeResult> results = service.computeCourseResults(student, 2);

    assertEquals(1, results.size());
    assertEquals("WEB1", results.get(0).code());
  }

  @Test
  void summary_computes_average_validated_credits_and_status() {
    CourseGradeResult valid =
        CourseGradeResult.builder()
            .courseId(UUID.randomUUID())
            .code("PROG1")
            .credits(6)
            .average(13.5)
            .complete(true)
            .validated(true)
            .build();
    CourseGradeResult missing =
        CourseGradeResult.builder()
            .courseId(UUID.randomUUID())
            .code("WEB2")
            .credits(4)
            .average(null)
            .complete(false)
            .validated(false)
            .build();

    StudentSummary summary = service.summary(List.of(valid, missing));

    assertEquals(13.5, summary.average());
    assertEquals(6, summary.validatedCredits());
    assertEquals(10, summary.totalCredits());
    assertEquals("PROVISOIRE", summary.status());
  }

  @Test
  void summary_marks_complet_and_empty_states() {
    CourseGradeResult valid =
        CourseGradeResult.builder()
            .courseId(UUID.randomUUID())
            .code("PROG1")
            .credits(6)
            .average(11.0)
            .complete(true)
            .validated(true)
            .build();

    assertEquals("COMPLET", service.summary(List.of(valid)).status());
    assertEquals("NON_EVALUE", service.summary(List.of()).status());
    assertNull(service.summary(List.of()).average());
  }

  @Test
  void student_can_access_own_profile() {
    JAccount account = account(Role.STUDENT);
    authenticate(account);
    when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
    when(studentRepository.findByAccountId(account.getId())).thenReturn(Optional.of(student));

    assertEquals(student, service.requireStudentForCurrentUser(student.getId()));
  }

  @Test
  void student_cannot_access_another_student_profile() {
    JAccount account = account(Role.STUDENT);
    authenticate(account);
    JStudent current = JStudent.builder().id(UUID.randomUUID()).std("STD24001").build();
    JStudent other = JStudent.builder().id(UUID.randomUUID()).std("STD24002").build();
    when(studentRepository.findByAccountId(account.getId())).thenReturn(Optional.of(current));
    when(studentRepository.findById(other.getId())).thenReturn(Optional.of(other));

    assertThrows(
        ForbiddenException.class, () -> service.requireStudentForCurrentUser(other.getId()));
  }

  @Test
  void admin_can_access_any_student_profile() {
    JAccount account = account(Role.ADMIN);
    authenticate(account);
    when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));

    assertEquals(student, service.requireStudentForCurrentUser(student.getId()));
  }

  @Test
  void require_teacher_for_course_rejects_teacher_not_assigned() {
    JAccount account = account(Role.TEACHER);
    authenticate(account);
    JTeacher teacher = JTeacher.builder().id(UUID.randomUUID()).account(account).build();
    when(teacherRepository.findByAccountId(account.getId())).thenReturn(Optional.of(teacher));
    JExam exam = exam(course("PROG1", 1, 6), 2);
    when(teacherCourseRepository.findByTeacherIdAndCourseIdAndPromotionId(
            teacher.getId(), exam.getCourse().getId(), exam.getPromotion().getId()))
        .thenReturn(Optional.empty());

    assertThrows(ForbiddenException.class, () -> service.requireTeacherForCourse(exam));
  }

  @Test
  void require_teacher_for_course_accepts_assigned_teacher() {
    JAccount account = account(Role.TEACHER);
    authenticate(account);
    JTeacher teacher = JTeacher.builder().id(UUID.randomUUID()).account(account).build();
    when(teacherRepository.findByAccountId(account.getId())).thenReturn(Optional.of(teacher));
    JExam exam = exam(course("PROG1", 1, 6), 2);
    when(teacherCourseRepository.findByTeacherIdAndCourseIdAndPromotionId(
            teacher.getId(), exam.getCourse().getId(), exam.getPromotion().getId()))
        .thenReturn(Optional.of(JTeacherCourse.builder().id(UUID.randomUUID()).build()));

    service.requireTeacherForCourse(exam);
  }

  @Test
  void update_grade_writes_history_with_previous_value() {
    JExam exam = exam(course("PROG1", 1, 6), 2);
    JGrade grade =
        JGrade.builder()
            .id(UUID.randomUUID())
            .student(student)
            .exam(exam)
            .value(10)
            .createdAt(Instant.now())
            .build();
    when(gradeRepository.findById(grade.getId())).thenReturn(Optional.of(grade));
    when(gradeRepository.save(grade)).thenReturn(grade);

    authenticate(account(Role.ADMIN));
    var response =
        service.updateGrade(grade.getId(), GradeUpdateRequest.builder().value(14).build());

    assertEquals(14.0, response.value());
    verify(gradeHistoryRepository)
        .save(argThat(history -> history.getValue() == 10 && "u".equals(history.getAuthor())));
  }

  @Test
  void update_grade_rejects_value_outside_0_20() {
    assertThrows(
        UnprocessableEntityException.class,
        () ->
            service.updateGrade(UUID.randomUUID(), GradeUpdateRequest.builder().value(21).build()));
  }

  @Test
  void update_grade_throws_not_found_for_unknown_grade() {
    UUID unknown = UUID.randomUUID();
    when(gradeRepository.findById(unknown)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> service.updateGrade(unknown, GradeUpdateRequest.builder().value(12).build()));
  }
}
