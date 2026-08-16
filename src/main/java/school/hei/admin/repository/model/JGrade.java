package school.hei.admin.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(
    name = "grade",
    uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "exam_id"}))
public class JGrade {
  @Id private UUID id;

  @ManyToOne
  @JoinColumn(name = "student_id")
  private JStudent student;

  @ManyToOne
  @JoinColumn(name = "exam_id")
  private JExam exam;

  @Column(name = "value")
  private double value;

  @Column(name = "created_at")
  private Instant createdAt;
}
