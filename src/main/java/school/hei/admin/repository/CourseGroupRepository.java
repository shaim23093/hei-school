package school.hei.admin.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.admin.repository.model.JCourseGroup;

@Repository
public interface CourseGroupRepository extends JpaRepository<JCourseGroup, UUID> {
  List<JCourseGroup> findByPromotionIdAndGroupIdIn(UUID promotionId, Collection<UUID> groupIds);
}
