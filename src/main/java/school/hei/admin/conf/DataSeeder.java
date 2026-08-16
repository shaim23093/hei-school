package school.hei.admin.conf;

import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import school.hei.admin.entity.Account;
import school.hei.admin.entity.enums.Role;
import school.hei.admin.mapper.AccountMapper;
import school.hei.admin.repository.AccountRepository;

@Component
@AllArgsConstructor
public class DataSeeder implements CommandLineRunner {
  private final AccountRepository accountRepository;
  private final AccountMapper accountMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    if (accountRepository.findByUsername("admin").isPresent()) {
      return;
    }
    Account admin =
        Account.builder()
            .id(UUID.randomUUID())
            .username("admin")
            .password(passwordEncoder.encode("admin123"))
            .role(Role.ADMIN)
            .build();
    accountRepository.save(accountMapper.toEntity(admin));
  }
}
