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
@Table(name = "exam")
public class JExam {
  @Id private UUID id;

  @ManyToOne
  @JoinColumn(name = "course_id")
  private JCourse course;

  @ManyToOne
  @JoinColumn(name = "promotion_id")
  private JPromotion promotion;

  @Column(name = "title")
  private String title;

  @Column(name = "date_time")
  private Instant dateTime;

  @Column(name = "duration_minutes")
  private int durationMinutes;

  @Column(name = "coefficient")
  private double coefficient;
}
