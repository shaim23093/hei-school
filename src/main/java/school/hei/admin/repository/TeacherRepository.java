package school.hei.admin.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.admin.repository.model.JTeacher;

@Repository
public interface TeacherRepository extends JpaRepository<JTeacher, UUID> {
  Optional<JTeacher> findByAccountId(UUID accountId);
}
