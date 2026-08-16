package school.hei.admin.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.admin.repository.model.JPromotion;

@Repository
public interface PromotionRepository extends JpaRepository<JPromotion, UUID> {}
