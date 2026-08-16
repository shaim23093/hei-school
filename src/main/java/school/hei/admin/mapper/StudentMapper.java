package school.hei.admin.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import school.hei.admin.entity.Student;
import school.hei.admin.repository.model.JAccount;
import school.hei.admin.repository.model.JPromotion;
import school.hei.admin.repository.model.JStudent;

@Component
public class StudentMapper {
  public Student toModel(JStudent entity) {
    return Student.builder()
        .id(entity.getId())
        .accountId(entity.getAccount() == null ? null : entity.getAccount().getId())
        .promotionId(entity.getPromotion() == null ? null : entity.getPromotion().getId())
        .std(entity.getStd())
        .name(entity.getName())
        .firstName(entity.getFirstName())
        .email(entity.getEmail())
        .build();
  }

  public List<Student> toModel(List<JStudent> entities) {
    return entities.stream().map(this::toModel).toList();
  }

  public JStudent toEntity(Student model) {
    return JStudent.builder()
        .id(model.id())
        .account(JAccount.builder().id(model.accountId()).build())
        .promotion(JPromotion.builder().id(model.promotionId()).build())
        .std(model.std())
        .name(model.name())
        .firstName(model.firstName())
        .email(model.email())
        .build();
  }

  public List<JStudent> toEntity(List<Student> models) {
    return models.stream().map(this::toEntity).toList();
  }
}
