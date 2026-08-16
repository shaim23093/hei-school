package school.hei.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "promotion")
public class Promotion {
  @Id private String id;

  @Column(name = "name")
  private String name;

  @Column(name = "entry_year")
  private int entryYear;
}
