package school.hei.admin.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import school.hei.admin.entity.GradeHistory;
import school.hei.admin.repository.model.JExam;
import school.hei.admin.repository.model.JGrade;
import school.hei.admin.repository.model.JGradeHistory;
import school.hei.admin.repository.model.JStudent;

@Component
public class GradeHistoryMapper {
  public GradeHistory toModel(JGradeHistory entity) {
    return GradeHistory.builder()
        .id(entity.getId())
        .gradeId(entity.getGrade() == null ? null : entity.getGrade().getId())
        .studentId(entity.getStudent() == null ? null : entity.getStudent().getId())
        .examId(entity.getExam() == null ? null : entity.getExam().getId())
        .value(entity.getValue())
        .modifiedAt(entity.getModifiedAt())
        .author(entity.getAuthor())
        .build();
  }

  public List<GradeHistory> toModel(List<JGradeHistory> entities) {
    return entities.stream().map(this::toModel).toList();
  }

  public JGradeHistory toEntity(GradeHistory model) {
    return JGradeHistory.builder()
        .id(model.id())
        .grade(JGrade.builder().id(model.gradeId()).build())
        .student(JStudent.builder().id(model.studentId()).build())
        .exam(JExam.builder().id(model.examId()).build())
        .value(model.value())
        .modifiedAt(model.modifiedAt())
        .author(model.author())
        .build();
  }

  public List<JGradeHistory> toEntity(List<GradeHistory> models) {
    return models.stream().map(this::toEntity).toList();
  }
}
