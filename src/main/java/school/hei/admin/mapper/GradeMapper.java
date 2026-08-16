package school.hei.admin.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import school.hei.admin.entity.Grade;
import school.hei.admin.repository.model.JExam;
import school.hei.admin.repository.model.JGrade;
import school.hei.admin.repository.model.JStudent;

@Component
public class GradeMapper {
  public Grade toModel(JGrade entity) {
    return Grade.builder()
        .id(entity.getId())
        .studentId(entity.getStudent() == null ? null : entity.getStudent().getId())
        .examId(entity.getExam() == null ? null : entity.getExam().getId())
        .value(entity.getValue())
        .createdAt(entity.getCreatedAt())
        .build();
  }

  public List<Grade> toModel(List<JGrade> entities) {
    return entities.stream().map(this::toModel).toList();
  }

  public JGrade toEntity(Grade model) {
    return JGrade.builder()
        .id(model.id())
        .student(JStudent.builder().id(model.studentId()).build())
        .exam(JExam.builder().id(model.examId()).build())
        .value(model.value())
        .createdAt(model.createdAt())
        .build();
  }

  public List<JGrade> toEntity(List<Grade> models) {
    return models.stream().map(this::toEntity).toList();
  }
}
