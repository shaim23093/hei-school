package school.hei.admin.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import school.hei.admin.entity.Account;
import school.hei.admin.repository.model.JAccount;

@Component
public class AccountMapper {
  public Account toModel(JAccount entity) {
    return Account.builder()
        .id(entity.getId())
        .username(entity.getUsername())
        .password(entity.getPassword())
        .role(entity.getRole())
        .build();
  }

  public List<Account> toModel(List<JAccount> entities) {
    return entities.stream().map(this::toModel).toList();
  }

  public JAccount toEntity(Account model) {
    return JAccount.builder()
        .id(model.id())
        .username(model.username())
        .password(model.password())
        .role(model.role())
        .build();
  }

  public List<JAccount> toEntity(List<Account> models) {
    return models.stream().map(this::toEntity).toList();
  }
}
