package school.hei.admin.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.admin.repository.model.JGrade;

@Repository
public interface GradeRepository extends JpaRepository<JGrade, UUID> {
  List<JGrade> findByStudentId(UUID studentId);

  List<JGrade> findByStudentIdAndExamId(UUID studentId, UUID examId);
}
