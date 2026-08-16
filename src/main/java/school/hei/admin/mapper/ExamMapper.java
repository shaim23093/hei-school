package school.hei.admin.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import school.hei.admin.entity.Exam;
import school.hei.admin.repository.model.JCourse;
import school.hei.admin.repository.model.JExam;
import school.hei.admin.repository.model.JPromotion;

@Component
public class ExamMapper {
  public Exam toModel(JExam entity) {
    return Exam.builder()
        .id(entity.getId())
        .courseId(entity.getCourse() == null ? null : entity.getCourse().getId())
        .promotionId(entity.getPromotion() == null ? null : entity.getPromotion().getId())
        .title(entity.getTitle())
        .dateTime(entity.getDateTime())
        .durationMinutes(entity.getDurationMinutes())
        .coefficient(entity.getCoefficient())
        .build();
  }

  public List<Exam> toModel(List<JExam> entities) {
    return entities.stream().map(this::toModel).toList();
  }

  public JExam toEntity(Exam model) {
    return JExam.builder()
        .id(model.id())
        .course(JCourse.builder().id(model.courseId()).build())
        .promotion(JPromotion.builder().id(model.promotionId()).build())
        .title(model.title())
        .dateTime(model.dateTime())
        .durationMinutes(model.durationMinutes())
        .coefficient(model.coefficient())
        .build();
  }

  public List<JExam> toEntity(List<Exam> models) {
    return models.stream().map(this::toEntity).toList();
  }
}
