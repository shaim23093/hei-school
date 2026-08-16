package school.hei.admin.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import school.hei.admin.entity.TeacherCourse;
import school.hei.admin.repository.model.JCourse;
import school.hei.admin.repository.model.JGroup;
import school.hei.admin.repository.model.JPromotion;
import school.hei.admin.repository.model.JTeacher;
import school.hei.admin.repository.model.JTeacherCourse;

@Component
public class TeacherCourseMapper {
  public TeacherCourse toModel(JTeacherCourse entity) {
    return TeacherCourse.builder()
        .id(entity.getId())
        .teacherId(entity.getTeacher() == null ? null : entity.getTeacher().getId())
        .courseId(entity.getCourse() == null ? null : entity.getCourse().getId())
        .groupId(entity.getGroup() == null ? null : entity.getGroup().getId())
        .promotionId(entity.getPromotion() == null ? null : entity.getPromotion().getId())
        .build();
  }

  public List<TeacherCourse> toModel(List<JTeacherCourse> entities) {
    return entities.stream().map(this::toModel).toList();
  }

  public JTeacherCourse toEntity(TeacherCourse model) {
    return JTeacherCourse.builder()
        .id(model.id())
        .teacher(JTeacher.builder().id(model.teacherId()).build())
        .course(JCourse.builder().id(model.courseId()).build())
        .group(JGroup.builder().id(model.groupId()).build())
        .promotion(JPromotion.builder().id(model.promotionId()).build())
        .build();
  }

  public List<JTeacherCourse> toEntity(List<TeacherCourse> models) {
    return models.stream().map(this::toEntity).toList();
  }
}
