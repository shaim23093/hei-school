package school.hei.admin.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "grade_history")
public class JGradeHistory {
  @Id private UUID id;

  @ManyToOne
  @JoinColumn(name = "grade_id")
  private JGrade grade;

  @ManyToOne
  @JoinColumn(name = "student_id")
  private JStudent student;

  @ManyToOne
  @JoinColumn(name = "exam_id")
  private JExam exam;

  @Column(name = "value")
  private double value;

  @Column(name = "modified_at")
  private Instant modifiedAt;

  @Column(name = "author")
  private String author;
}
