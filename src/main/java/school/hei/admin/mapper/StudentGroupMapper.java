package school.hei.admin.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import school.hei.admin.entity.StudentGroup;
import school.hei.admin.repository.model.JGroup;
import school.hei.admin.repository.model.JStudent;
import school.hei.admin.repository.model.JStudentGroup;

@Component
public class StudentGroupMapper {
  public StudentGroup toModel(JStudentGroup entity) {
    return StudentGroup.builder()
        .id(entity.getId())
        .studentId(entity.getStudent() == null ? null : entity.getStudent().getId())
        .groupId(entity.getGroup() == null ? null : entity.getGroup().getId())
        .semester(entity.getSemester())
        .build();
  }

  public List<StudentGroup> toModel(List<JStudentGroup> entities) {
    return entities.stream().map(this::toModel).toList();
  }

  public JStudentGroup toEntity(StudentGroup model) {
    return JStudentGroup.builder()
        .id(model.id())
        .student(JStudent.builder().id(model.studentId()).build())
        .group(JGroup.builder().id(model.groupId()).build())
        .semester(model.semester())
        .build();
  }

  public List<JStudentGroup> toEntity(List<StudentGroup> models) {
    return models.stream().map(this::toEntity).toList();
  }
}
