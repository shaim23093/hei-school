package school.hei.admin.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import school.hei.admin.entity.Teacher;
import school.hei.admin.repository.model.JAccount;
import school.hei.admin.repository.model.JTeacher;

@Component
public class TeacherMapper {
  public Teacher toModel(JTeacher entity) {
    return Teacher.builder()
        .id(entity.getId())
        .accountId(entity.getAccount() == null ? null : entity.getAccount().getId())
        .name(entity.getName())
        .firstName(entity.getFirstName())
        .email(entity.getEmail())
        .build();
  }

  public List<Teacher> toModel(List<JTeacher> entities) {
    return entities.stream().map(this::toModel).toList();
  }

  public JTeacher toEntity(Teacher model) {
    return JTeacher.builder()
        .id(model.id())
        .account(JAccount.builder().id(model.accountId()).build())
        .name(model.name())
        .firstName(model.firstName())
        .email(model.email())
        .build();
  }

  public List<JTeacher> toEntity(List<Teacher> models) {
    return models.stream().map(this::toEntity).toList();
  }
}
