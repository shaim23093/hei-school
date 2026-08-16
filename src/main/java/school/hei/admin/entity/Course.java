package school.hei.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import school.hei.admin.enums.Path;

@Entity
@Getter
@Setter
@Table(name = "course")
public class Course {
  @Id private String id;

  @Column(name = "code")
  private String code;

  @Column(name = "name")
  private String name;

  @Column(name = "credits")
  private int credits;

  @Enumerated(EnumType.STRING)
  @Column(name = "path", nullable = true)
  private Path path;

  @Column(name = "semester")
  private int semester;
}
