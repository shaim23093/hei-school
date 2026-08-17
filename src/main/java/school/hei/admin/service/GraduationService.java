package school.hei.admin.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.admin.dto.response.GraduatedStudentResponse;
import school.hei.admin.dto.response.PromotionResultsResponse;
import school.hei.admin.dto.response.StudentResultResponse;
import school.hei.admin.dto.response.StudentSummary;
import school.hei.admin.entity.enums.Path;
import school.hei.admin.exception.NotFoundException;
import school.hei.admin.repository.PromotionRepository;
import school.hei.admin.repository.StudentGroupRepository;
import school.hei.admin.repository.StudentRepository;
import school.hei.admin.repository.model.JPromotion;
import school.hei.admin.repository.model.JStudent;

@Service
@AllArgsConstructor
public class GraduationService {
  private final PromotionRepository promotionRepository;
  private final StudentRepository studentRepository;
  private final StudentGroupRepository studentGroupRepository;
  private final GradeService gradeService;

  public List<GraduatedStudentResponse> diplomas(UUID promotionId) {
    getPromotion(promotionId);
    List<GraduatedStudentResponse> graduated = new ArrayList<>();
    for (JStudent student : studentRepository.findByPromotionId(promotionId)) {
      StudentSummary summary =
          gradeService.summary(gradeService.computeCourseResults(student, null));
      if (summary.average() != null && summary.average() >= 10) {
        graduated.add(
            GraduatedStudentResponse.builder()
                .studentId(student.getId())
                .std(student.getStd())
                .name(student.getName())
                .firstName(student.getFirstName())
                .path(studentPath(student))
                .average(summary.average())
                .build());
      }
    }
    graduated.sort(Comparator.comparingDouble(GraduatedStudentResponse::average).reversed());
    for (int i = 0; i < graduated.size(); i++) {
      GraduatedStudentResponse response = graduated.get(i);
      graduated.set(
          i,
          GraduatedStudentResponse.builder()
              .studentId(response.studentId())
              .std(response.std())
              .name(response.name())
              .firstName(response.firstName())
              .path(response.path())
              .average(response.average())
              .rank(i + 1)
              .build());
    }
    return graduated;
  }

  public PromotionResultsResponse results(UUID promotionId) {
    JPromotion promotion = getPromotion(promotionId);
    List<StudentResultResponse> results =
        studentRepository.findByPromotionId(promotionId).stream()
            .map(this::toResultResponse)
            .sorted(
                Comparator.comparing(
                    StudentResultResponse::average,
                    Comparator.nullsLast(Comparator.reverseOrder())))
            .toList();
    return PromotionResultsResponse.builder()
        .promotionId(promotionId)
        .promotionName(promotion.getName())
        .results(results)
        .build();
  }

  private StudentResultResponse toResultResponse(JStudent student) {
    StudentSummary summary = gradeService.summary(gradeService.computeCourseResults(student, null));
    return StudentResultResponse.builder()
        .studentId(student.getId())
        .std(student.getStd())
        .name(student.getName())
        .firstName(student.getFirstName())
        .average(summary.average())
        .validatedCredits(summary.validatedCredits())
        .totalCredits(summary.totalCredits())
        .status(summary.status())
        .build();
  }

  private JPromotion getPromotion(UUID promotionId) {
    return promotionRepository
        .findById(promotionId)
        .orElseThrow(() -> new NotFoundException("Promotion not found: " + promotionId));
  }

  private Path studentPath(JStudent student) {
    return studentGroupRepository.findByStudentId(student.getId()).stream()
        .map(studentGroup -> studentGroup.getGroup().getPath())
        .findFirst()
        .orElse(null);
  }
}
