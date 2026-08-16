package school.hei.admin.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import school.hei.admin.entity.CourseGroup;
import school.hei.admin.repository.model.JCourse;
import school.hei.admin.repository.model.JCourseGroup;
import school.hei.admin.repository.model.JGroup;
import school.hei.admin.repository.model.JPromotion;

@Component
public class CourseGroupMapper {
  public CourseGroup toModel(JCourseGroup entity) {
    return CourseGroup.builder()
        .id(entity.getId())
        .courseId(entity.getCourse() == null ? null : entity.getCourse().getId())
        .groupId(entity.getGroup() == null ? null : entity.getGroup().getId())
        .promotionId(entity.getPromotion() == null ? null : entity.getPromotion().getId())
        .build();
  }

  public List<CourseGroup> toModel(List<JCourseGroup> entities) {
    return entities.stream().map(this::toModel).toList();
  }

  public JCourseGroup toEntity(CourseGroup model) {
    return JCourseGroup.builder()
        .id(model.id())
        .course(JCourse.builder().id(model.courseId()).build())
        .group(JGroup.builder().id(model.groupId()).build())
        .promotion(JPromotion.builder().id(model.promotionId()).build())
        .build();
  }

  public List<JCourseGroup> toEntity(List<CourseGroup> models) {
    return models.stream().map(this::toEntity).toList();
  }
}
