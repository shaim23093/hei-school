package school.hei.admin.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.admin.repository.model.JStudentGroup;

@Repository
public interface StudentGroupRepository extends JpaRepository<JStudentGroup, UUID> {
  List<JStudentGroup> findByStudentId(UUID studentId);
}
