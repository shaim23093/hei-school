package school.hei.admin.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import school.hei.admin.dto.request.GradeUpdateRequest;
import school.hei.admin.dto.response.AcademicYearResult;
import school.hei.admin.dto.response.CourseGradeResult;
import school.hei.admin.dto.response.GradeHistoryResponse;
import school.hei.admin.dto.response.GradeResponse;
import school.hei.admin.dto.response.StudentGradesResponse;
import school.hei.admin.dto.response.StudentSummary;
import school.hei.admin.dto.response.TranscriptResponse;
import school.hei.admin.entity.enums.Role;
import school.hei.admin.exception.ApiException;
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
import school.hei.admin.repository.model.JExam;
import school.hei.admin.repository.model.JGrade;
import school.hei.admin.repository.model.JGradeHistory;
import school.hei.admin.repository.model.JStudent;
import school.hei.admin.repository.model.JTeacher;

@Service
@AllArgsConstructor
public class GradeService {
  private final AccountRepository accountRepository;
  private final StudentRepository studentRepository;
  private final TeacherRepository teacherRepository;
  private final TeacherCourseRepository teacherCourseRepository;
  private final StudentGroupRepository studentGroupRepository;
  private final CourseGroupRepository courseGroupRepository;
  private final CourseRepository courseRepository;
  private final ExamRepository examRepository;
  private final GradeRepository gradeRepository;
  private final GradeHistoryRepository gradeHistoryRepository;

  public StudentGradesResponse getGrades(UUID studentId, Integer semester) {
    JStudent student = requireStudentForCurrentUser(studentId);
    List<CourseGradeResult> results = computeCourseResults(student, semester);
    StudentSummary summary = summary(results);
    return StudentGradesResponse.builder()
        .studentId(student.getId())
        .std(student.getStd())
        .name(student.getName())
        .firstName(student.getFirstName())
        .promotionName(student.getPromotion().getName())
        .average(summary.average())
        .validatedCredits(summary.validatedCredits())
        .totalCredits(summary.totalCredits())
        .status(summary.status())
        .results(results)
        .build();
  }

  public TranscriptResponse getTranscript(UUID studentId, Integer semester) {
    JStudent student = requireStudentForCurrentUser(studentId);
    return buildTranscript(student, semester);
  }

  public TranscriptResponse getTranscriptInternal(UUID studentId, Integer semester) {
    JStudent student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));
    return buildTranscript(student, semester);
  }

  private TranscriptResponse buildTranscript(JStudent student, Integer semester) {
    List<CourseGradeResult> results = computeCourseResults(student, semester);
    StudentSummary summary = summary(results);
    return TranscriptResponse.builder()
        .studentId(student.getId())
        .std(student.getStd())
        .name(student.getName())
        .firstName(student.getFirstName())
        .promotionName(student.getPromotion().getName())
        .semester(semester)
        .average(summary.average())
        .validatedCredits(summary.validatedCredits())
        .totalCredits(summary.totalCredits())
        .status(summary.status())
        .results(results)
        .build();
  }

  public AcademicYearResult getAcademicYearResults(UUID studentId, int academicYear) {
    JStudent student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));
    List<CourseGradeResult> allResults = computeCourseResults(student, null);
    List<CourseGradeResult> yearResults =
        allResults.stream().filter(result -> result.academicYear() == academicYear).toList();
    StudentSummary yearSummary = summary(yearResults);
    return AcademicYearResult.builder()
        .academicYear(academicYear)
        .average(yearSummary.average())
        .validatedCredits(yearSummary.validatedCredits())
        .totalCredits(yearSummary.totalCredits())
        .status(yearSummary.status())
        .results(yearResults)
        .build();
  }

  public GradeResponse updateGrade(UUID gradeId, GradeUpdateRequest request) {
    if (request.value() < 0 || request.value() > 20) {
      throw new UnprocessableEntityException("Grade must be between 0 and 20");
    }
    JGrade grade =
        gradeRepository
            .findById(gradeId)
            .orElseThrow(() -> new NotFoundException("Grade not found: " + gradeId));
    requireTeacherForCourse(grade.getExam());

    double previousValue = grade.getValue();
    gradeHistoryRepository.save(
        JGradeHistory.builder()
            .id(UUID.randomUUID())
            .grade(grade)
            .student(grade.getStudent())
            .exam(grade.getExam())
            .value(previousValue)
            .modifiedAt(Instant.now())
            .author(getCurrentUsername())
            .build());

    grade.setValue(request.value());
    JGrade saved = gradeRepository.save(grade);
    return GradeResponse.builder()
        .id(saved.getId())
        .studentId(saved.getStudent().getId())
        .examId(saved.getExam().getId())
        .value(saved.getValue())
        .build();
  }

  public List<GradeHistoryResponse> getHistory(UUID gradeId) {
    JGrade grade =
        gradeRepository
            .findById(gradeId)
            .orElseThrow(() -> new NotFoundException("Grade not found: " + gradeId));
    requireTeacherForCourse(grade.getExam());
    return gradeHistoryRepository.findByGradeIdOrderByModifiedAtDesc(gradeId).stream()
        .map(
            history ->
                GradeHistoryResponse.builder()
                    .id(history.getId())
                    .gradeId(history.getGrade().getId())
                    .examId(history.getExam().getId())
                    .courseCode(history.getExam().getCourse().getCode())
                    .value(history.getValue())
                    .modifiedAt(history.getModifiedAt())
                    .author(history.getAuthor())
                    .build())
        .toList();
  }

  public List<CourseGradeResult> computeCourseResults(JStudent student, Integer semester) {
    UUID promotionId = student.getPromotion().getId();
    List<UUID> groupIds =
        studentGroupRepository.findByStudentId(student.getId()).stream()
            .map(studentGroup -> studentGroup.getGroup().getId())
            .distinct()
            .toList();
    if (groupIds.isEmpty()) {
      return List.of();
    }
    List<UUID> courseIds =
        courseGroupRepository.findByPromotionIdAndGroupIdIn(promotionId, groupIds).stream()
            .map(courseGroup -> courseGroup.getCourse().getId())
            .distinct()
            .toList();
    if (courseIds.isEmpty()) {
      return List.of();
    }
    List<JCourse> courses = courseRepository.findAllById(courseIds);
    if (semester != null) {
      courses = courses.stream().filter(course -> course.getSemester() == semester).toList();
    }
    if (courses.isEmpty()) {
      return List.of();
    }

    for (JCourse course : courses) {
      validateCoefficientSum(course.getId(), promotionId);
    }

    List<JExam> exams = examRepository.findByCourseIdInAndPromotionId(courseIds, promotionId);
    Map<UUID, List<JExam>> examsByCourse =
        exams.stream().collect(Collectors.groupingBy(exam -> exam.getCourse().getId()));
    Map<UUID, JGrade> gradeByExam =
        gradeRepository.findByStudentId(student.getId()).stream()
            .collect(Collectors.toMap(grade -> grade.getExam().getId(), grade -> grade));

    return courses.stream()
        .sorted(Comparator.comparingInt(JCourse::getSemester).thenComparing(JCourse::getCode))
        .map(
            course ->
                toResult(
                    course, examsByCourse.getOrDefault(course.getId(), List.of()), gradeByExam))
        .toList();
  }

  public StudentSummary summary(List<CourseGradeResult> results) {
    if (results.isEmpty()) {
      return new StudentSummary(null, 0, 0, "NON_EVALUE");
    }
    int totalCredits = results.stream().mapToInt(CourseGradeResult::credits).sum();
    int validatedCredits =
        results.stream()
            .filter(CourseGradeResult::validated)
            .mapToInt(CourseGradeResult::credits)
            .sum();
    List<CourseGradeResult> completed =
        results.stream().filter(CourseGradeResult::complete).toList();
    Double average = null;
    if (!completed.isEmpty()) {
      double weightedSum = 0;
      int creditsSum = 0;
      for (CourseGradeResult result : completed) {
        weightedSum += result.average() * result.credits();
        creditsSum += result.credits();
      }
      average = weightedSum / creditsSum;
    }
    boolean allValidated = results.stream().allMatch(CourseGradeResult::validated);
    return new StudentSummary(
        average, validatedCredits, totalCredits, allValidated ? "COMPLET" : "PROVISOIRE");
  }

  private CourseGradeResult toResult(
      JCourse course, List<JExam> exams, Map<UUID, JGrade> gradeByExam) {
    boolean allGraded =
        !exams.isEmpty() && exams.stream().allMatch(exam -> gradeByExam.containsKey(exam.getId()));
    Double average = null;
    if (allGraded) {
      double weightedSum = 0;
      double coefficientSum = 0;
      for (JExam exam : exams) {
        weightedSum += gradeByExam.get(exam.getId()).getValue() * exam.getCoefficient();
        coefficientSum += exam.getCoefficient();
      }
      if (coefficientSum > 0) {
        average = weightedSum / coefficientSum;
      }
    }
    boolean complete = allGraded && average != null;
    return CourseGradeResult.builder()
        .courseId(course.getId())
        .code(course.getCode())
        .name(course.getName())
        .credits(course.getCredits())
        .semester(course.getSemester())
        .academicYear(course.getAcademicYear())
        .path(course.getPath())
        .average(average)
        .complete(complete)
        .validated(complete && average >= 10)
        .build();
  }

  JStudent requireStudentForCurrentUser(UUID studentId) {
    JStudent target =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));
    if (getCurrentRole() == Role.STUDENT) {
      JStudent current =
          studentRepository
              .findByAccountId(getCurrentAccount().getId())
              .orElseThrow(() -> new ForbiddenException("No student profile linked to account"));
      if (!current.getId().equals(target.getId())) {
        throw new ForbiddenException("Students can only access their own grades");
      }
    }
    return target;
  }

  void requireTeacherForCourse(JExam exam) {
    if (getCurrentRole() == Role.ADMIN) {
      return;
    }
    if (getCurrentRole() != Role.TEACHER) {
      throw new ForbiddenException("Only teachers can manage grades");
    }
    JTeacher teacher =
        teacherRepository
            .findByAccountId(getCurrentAccount().getId())
            .orElseThrow(() -> new ForbiddenException("No teacher profile linked to account"));
    boolean assigned =
        teacherCourseRepository
            .findByTeacherIdAndCourseIdAndPromotionId(
                teacher.getId(), exam.getCourse().getId(), exam.getPromotion().getId())
            .isPresent();
    if (!assigned) {
      throw new ForbiddenException("Teacher is not assigned to this course");
    }
  }

  void validateCoefficientSum(UUID courseId, UUID promotionId) {
    List<JExam> exams =
        examRepository.findByCourseIdInAndPromotionId(List.of(courseId), promotionId);
    if (exams.isEmpty()) {
      return;
    }
    double sum = exams.stream().mapToDouble(JExam::getCoefficient).sum();
    if (Math.abs(sum - 1.0) > 0.001) {
      throw new UnprocessableEntityException(
          "Exam coefficients for course must sum to 1.0, but got: " + sum);
    }
  }

  private JAccount getCurrentAccount() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ApiException("Unauthenticated", HttpStatus.UNAUTHORIZED);
    }
    return accountRepository
        .findById(UUID.fromString(authentication.getName()))
        .orElseThrow(() -> new ApiException("Unauthenticated", HttpStatus.UNAUTHORIZED));
  }

  private Role getCurrentRole() {
    return getCurrentAccount().getRole();
  }

  private String getCurrentUsername() {
    return getCurrentAccount().getUsername();
  }
}
