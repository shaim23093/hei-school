package school.hei.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.admin.dto.response.AcademicYearResult;
import school.hei.admin.entity.enums.Path;
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
import school.hei.admin.repository.model.JCourse;
import school.hei.admin.repository.model.JCourseGroup;
import school.hei.admin.repository.model.JExam;
import school.hei.admin.repository.model.JGrade;
import school.hei.admin.repository.model.JGroup;
import school.hei.admin.repository.model.JPromotion;
import school.hei.admin.repository.model.JStudent;
import school.hei.admin.repository.model.JStudentGroup;

class AcademicYearServiceTest {
  private final StudentGroupRepository studentGroupRepository = mock(StudentGroupRepository.class);
  private final CourseGroupRepository courseGroupRepository = mock(CourseGroupRepository.class);
  private final CourseRepository courseRepository = mock(CourseRepository.class);
  private final ExamRepository examRepository = mock(ExamRepository.class);
  private final GradeRepository gradeRepository = mock(GradeRepository.class);

  private final GradeService gradeService =
      new GradeService(
          mock(AccountRepository.class),
          mock(StudentRepository.class),
          mock(TeacherRepository.class),
          mock(TeacherCourseRepository.class),
          studentGroupRepository,
          courseGroupRepository,
          courseRepository,
          examRepository,
          gradeRepository,
          mock(GradeHistoryRepository.class));

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
  }

  private JCourse course(String code, int semester, int academicYear, int credits) {
    return JCourse.builder()
        .id(UUID.randomUUID())
        .code(code)
        .name(code)
        .semester(semester)
        .academicYear(academicYear)
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
        .coefficient(coefficient)
        .build();
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

  private void linkCourses(List<JCourse> courses) {
    List<JCourseGroup> courseGroups =
        courses.stream()
            .map(
                c ->
                    JCourseGroup.builder()
                        .id(UUID.randomUUID())
                        .course(c)
                        .group(group)
                        .promotion(promotion)
                        .build())
            .toList();
    when(courseGroupRepository.findByPromotionIdAndGroupIdIn(
            promotion.getId(), List.of(group.getId())))
        .thenReturn(courseGroups);
  }

  @Test
  void groups_courses_from_semester_1_and_2_into_academic_year_1() {
    JCourse sem1 = course("PROG1", 1, 1, 6);
    JCourse sem2 = course("WEB1", 2, 1, 5);
    when(studentGroupRepository.findByStudentId(student.getId()))
        .thenReturn(
            List.of(
                JStudentGroup.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .group(group)
                    .semester(1)
                    .build()));
    linkCourses(List.of(sem1, sem2));
    when(courseRepository.findAllById(List.of(sem1.getId(), sem2.getId())))
        .thenReturn(List.of(sem1, sem2));

    JExam exam1 = exam(sem1, 1);
    JExam exam2 = exam(sem2, 1);
    when(examRepository.findByCourseIdInAndPromotionId(
            List.of(sem1.getId(), sem2.getId()), promotion.getId()))
        .thenReturn(List.of(exam1, exam2));
    when(gradeRepository.findByStudentId(student.getId()))
        .thenReturn(
            List.of(
                JGrade.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .exam(exam1)
                    .value(12)
                    .build(),
                JGrade.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .exam(exam2)
                    .value(14)
                    .build()));

    AcademicYearResult result = gradeService.getAcademicYearResults(student.getId(), 1);

    assertEquals(1, result.academicYear());
    assertEquals(2, result.results().size());
    assertTrue(result.results().stream().allMatch(r -> r.academicYear() == 1));
  }

  @Test
  void computes_weighted_average_across_both_semesters() {
    JCourse sem1 = course("PROG1", 1, 1, 6);
    JCourse sem2 = course("WEB1", 2, 1, 4);
    when(studentGroupRepository.findByStudentId(student.getId()))
        .thenReturn(
            List.of(
                JStudentGroup.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .group(group)
                    .semester(1)
                    .build()));
    linkCourses(List.of(sem1, sem2));
    when(courseRepository.findAllById(List.of(sem1.getId(), sem2.getId())))
        .thenReturn(List.of(sem1, sem2));

    JExam exam1 = exam(sem1, 1);
    JExam exam2 = exam(sem2, 1);
    when(examRepository.findByCourseIdInAndPromotionId(
            List.of(sem1.getId(), sem2.getId()), promotion.getId()))
        .thenReturn(List.of(exam1, exam2));
    when(gradeRepository.findByStudentId(student.getId()))
        .thenReturn(
            List.of(
                JGrade.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .exam(exam1)
                    .value(12)
                    .build(),
                JGrade.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .exam(exam2)
                    .value(16)
                    .build()));

    AcademicYearResult result = gradeService.getAcademicYearResults(student.getId(), 1);

    double expectedAverage = (12.0 * 6 + 16.0 * 4) / (6 + 4);
    assertEquals(expectedAverage, result.average(), 0.001);
    assertEquals(10, result.totalCredits());
    assertEquals("COMPLET", result.status());
  }

  @Test
  void counts_validated_credits_only_for_courses_above_10() {
    JCourse sem1 = course("PROG1", 1, 1, 6);
    JCourse sem2 = course("WEB1", 2, 1, 4);
    when(studentGroupRepository.findByStudentId(student.getId()))
        .thenReturn(
            List.of(
                JStudentGroup.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .group(group)
                    .semester(1)
                    .build()));
    linkCourses(List.of(sem1, sem2));
    when(courseRepository.findAllById(List.of(sem1.getId(), sem2.getId())))
        .thenReturn(List.of(sem1, sem2));

    JExam exam1 = exam(sem1, 1);
    JExam exam2 = exam(sem2, 1);
    when(examRepository.findByCourseIdInAndPromotionId(
            List.of(sem1.getId(), sem2.getId()), promotion.getId()))
        .thenReturn(List.of(exam1, exam2));
    when(gradeRepository.findByStudentId(student.getId()))
        .thenReturn(
            List.of(
                JGrade.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .exam(exam1)
                    .value(12)
                    .build(),
                JGrade.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .exam(exam2)
                    .value(8)
                    .build()));

    AcademicYearResult result = gradeService.getAcademicYearResults(student.getId(), 1);

    assertEquals(6, result.validatedCredits());
    assertEquals(10, result.totalCredits());
    assertEquals("PROVISOIRE", result.status());
  }

  @Test
  void filters_only_courses_for_requested_academic_year() {
    JCourse year1Sem1 = course("PROG1", 1, 1, 6);
    JCourse year2Sem3 = course("PROG4", 3, 2, 5);
    when(studentGroupRepository.findByStudentId(student.getId()))
        .thenReturn(
            List.of(
                JStudentGroup.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .group(group)
                    .semester(1)
                    .build()));
    linkCourses(List.of(year1Sem1, year2Sem3));
    when(courseRepository.findAllById(List.of(year1Sem1.getId(), year2Sem3.getId())))
        .thenReturn(List.of(year1Sem1, year2Sem3));

    JExam exam1 = exam(year1Sem1, 1);
    JExam exam2 = exam(year2Sem3, 1);
    when(examRepository.findByCourseIdInAndPromotionId(
            List.of(year1Sem1.getId(), year2Sem3.getId()), promotion.getId()))
        .thenReturn(List.of(exam1, exam2));
    when(gradeRepository.findByStudentId(student.getId()))
        .thenReturn(
            List.of(
                JGrade.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .exam(exam1)
                    .value(12)
                    .build(),
                JGrade.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .exam(exam2)
                    .value(15)
                    .build()));

    AcademicYearResult year1 = gradeService.getAcademicYearResults(student.getId(), 1);
    AcademicYearResult year2 = gradeService.getAcademicYearResults(student.getId(), 2);

    assertEquals(1, year1.results().size());
    assertEquals("PROG1", year1.results().get(0).code());
    assertEquals(1, year2.results().size());
    assertEquals("PROG4", year2.results().get(0).code());
  }

  @Test
  void returns_empty_results_when_no_courses_for_year() {
    JCourse year1Sem1 = course("PROG1", 1, 1, 6);
    when(studentGroupRepository.findByStudentId(student.getId()))
        .thenReturn(
            List.of(
                JStudentGroup.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .group(group)
                    .semester(1)
                    .build()));
    linkCourses(List.of(year1Sem1));
    when(courseRepository.findAllById(List.of(year1Sem1.getId()))).thenReturn(List.of(year1Sem1));
    when(examRepository.findByCourseIdInAndPromotionId(
            List.of(year1Sem1.getId()), promotion.getId()))
        .thenReturn(List.of());
    when(gradeRepository.findByStudentId(student.getId())).thenReturn(List.of());

    AcademicYearResult result = gradeService.getAcademicYearResults(student.getId(), 3);

    assertEquals(3, result.academicYear());
    assertTrue(result.results().isEmpty());
    assertNull(result.average());
    assertEquals("NON_EVALUE", result.status());
  }
}
