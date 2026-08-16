package school.hei.admin.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.admin.repository.model.JStudent;

@Repository
public interface StudentRepository extends JpaRepository<JStudent, UUID> {
  Optional<JStudent> findByAccountId(UUID accountId);

  List<JStudent> findByPromotionId(UUID promotionId);
}
