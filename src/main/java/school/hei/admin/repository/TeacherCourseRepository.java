package school.hei.admin.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.admin.repository.model.JTeacherCourse;

@Repository
public interface TeacherCourseRepository extends JpaRepository<JTeacherCourse, UUID> {
  Optional<JTeacherCourse> findByTeacherIdAndCourseIdAndPromotionId(
      UUID teacherId, UUID courseId, UUID promotionId);
}
