package school.hei.admin.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "student_group")
public class JStudentGroup {
  @Id private UUID id;

  @ManyToOne
  @JoinColumn(name = "student_id")
  private JStudent student;

  @ManyToOne
  @JoinColumn(name = "group_id")
  private JGroup group;

  @Column(name = "semester")
  private int semester;
}
