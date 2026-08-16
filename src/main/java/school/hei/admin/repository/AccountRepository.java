package school.hei.admin.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.admin.repository.model.JAccount;

@Repository
public interface AccountRepository extends JpaRepository<JAccount, UUID> {
  Optional<JAccount> findByUsername(String username);
}
