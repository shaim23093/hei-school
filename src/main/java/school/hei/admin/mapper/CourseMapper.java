package school.hei.admin.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import school.hei.admin.entity.Course;
import school.hei.admin.repository.model.JCourse;

@Component
public class CourseMapper {
  public Course toModel(JCourse entity) {
    return Course.builder()
        .id(entity.getId())
        .code(entity.getCode())
        .name(entity.getName())
        .credits(entity.getCredits())
        .path(entity.getPath())
        .semester(entity.getSemester())
        .build();
  }

  public List<Course> toModel(List<JCourse> entities) {
    return entities.stream().map(this::toModel).toList();
  }

  public JCourse toEntity(Course model) {
    return JCourse.builder()
        .id(model.id())
        .code(model.code())
        .name(model.name())
        .credits(model.credits())
        .path(model.path())
        .semester(model.semester())
        .build();
  }

  public List<JCourse> toEntity(List<Course> models) {
    return models.stream().map(this::toEntity).toList();
  }
}
