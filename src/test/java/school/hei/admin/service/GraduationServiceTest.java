package school.hei.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.admin.dto.response.CourseGradeResult;
import school.hei.admin.dto.response.GraduatedStudentResponse;
import school.hei.admin.entity.enums.Path;
import school.hei.admin.repository.AccountRepository;
import school.hei.admin.repository.CourseGroupRepository;
import school.hei.admin.repository.CourseRepository;
import school.hei.admin.repository.ExamRepository;
import school.hei.admin.repository.GradeHistoryRepository;
import school.hei.admin.repository.GradeRepository;
import school.hei.admin.repository.PromotionRepository;
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

class GraduationServiceTest {
  private final PromotionRepository promotionRepository = mock(PromotionRepository.class);
  private final StudentRepository studentRepository = mock(StudentRepository.class);
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

  private final GraduationService service =
      new GraduationService(
          promotionRepository, studentRepository, studentGroupRepository, gradeService);

  private UUID promotionId;
  private JPromotion promotion;

  @BeforeEach
  void setUp() {
    promotionId = UUID.randomUUID();
    promotion = JPromotion.builder().id(promotionId).name("Promo 2024").entryYear(2024).build();
    when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));
  }

  @Test
  void diplomas_keeps_only_students_with_average_at_least_10_ranked_desc() {
    JStudent first = student("STD24001");
    JStudent second = student("STD24002");
    JStudent failed = student("STD24003");
    JStudent noGrade = student("STD24004");
    when(studentRepository.findByPromotionId(promotionId))
        .thenReturn(List.of(first, second, failed, noGrade));

    stubEvaluation(first, courseResult(14.0, Path.EL));
    stubEvaluation(second, courseResult(16.0, Path.TN));
    stubEvaluation(failed, courseResult(8.0, Path.EL));
    stubNoEvaluation(noGrade);

    List<GraduatedStudentResponse> diplomas = service.diplomas(promotionId);

    assertEquals(2, diplomas.size());
    assertEquals("STD24002", diplomas.get(0).std());
    assertEquals(1, diplomas.get(0).rank());
    assertEquals(Path.TN, diplomas.get(0).path());
    assertEquals(16.0, diplomas.get(0).average());
    assertEquals("STD24001", diplomas.get(1).std());
    assertEquals(2, diplomas.get(1).rank());
    assertEquals(Path.EL, diplomas.get(1).path());
  }

  @Test
  void results_returns_all_students_with_their_summary() {
    JStudent graduated = student("STD24001");
    JStudent noGrade = student("STD24002");
    when(studentRepository.findByPromotionId(promotionId)).thenReturn(List.of(graduated, noGrade));

    stubEvaluation(graduated, courseResult(12.0, Path.EL));
    stubNoEvaluation(noGrade);

    var response = service.results(promotionId);

    assertEquals("Promo 2024", response.promotionName());
    assertEquals(2, response.results().size());
    assertEquals(12.0, response.results().get(0).average());
    assertEquals("COMPLET", response.results().get(0).status());
    assertEquals("NON_EVALUE", response.results().get(1).status());
  }

  private void stubEvaluation(JStudent student, CourseGradeResult result) {
    JGroup group = JGroup.builder().id(UUID.randomUUID()).name("Group").path(result.path()).build();
    when(studentGroupRepository.findByStudentId(student.getId()))
        .thenReturn(
            List.of(
                JStudentGroup.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .group(group)
                    .semester(1)
                    .build()));

    JCourse course =
        JCourse.builder()
            .id(UUID.randomUUID())
            .code(result.code())
            .credits(result.credits())
            .semester(1)
            .path(result.path())
            .build();
    when(courseGroupRepository.findByPromotionIdAndGroupIdIn(promotionId, List.of(group.getId())))
        .thenReturn(
            List.of(
                JCourseGroup.builder()
                    .id(UUID.randomUUID())
                    .course(course)
                    .group(group)
                    .promotion(promotion)
                    .build()));
    when(courseRepository.findAllById(List.of(course.getId()))).thenReturn(List.of(course));

    JExam exam =
        JExam.builder()
            .id(UUID.randomUUID())
            .course(course)
            .promotion(promotion)
            .coefficient(1)
            .build();
    when(examRepository.findByCourseIdInAndPromotionId(List.of(course.getId()), promotionId))
        .thenReturn(List.of(exam));
    when(gradeRepository.findByStudentId(student.getId()))
        .thenReturn(
            List.of(
                JGrade.builder()
                    .id(UUID.randomUUID())
                    .student(student)
                    .exam(exam)
                    .value(result.average())
                    .build()));
  }

  private void stubNoEvaluation(JStudent student) {
    when(studentGroupRepository.findByStudentId(student.getId())).thenReturn(List.of());
  }

  private JStudent student(String std) {
    return JStudent.builder()
        .id(UUID.randomUUID())
        .std(std)
        .name("Student")
        .firstName(std)
        .promotion(promotion)
        .build();
  }

  private CourseGradeResult courseResult(double average, Path path) {
    return CourseGradeResult.builder()
        .courseId(UUID.randomUUID())
        .code("PROG1")
        .credits(6)
        .average(average)
        .complete(true)
        .validated(average >= 10)
        .path(path)
        .build();
  }
}
