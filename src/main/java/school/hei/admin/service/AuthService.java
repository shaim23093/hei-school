package school.hei.admin.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import school.hei.admin.conf.JwtTokenProvider;
import school.hei.admin.dto.request.LoginRequest;
import school.hei.admin.dto.response.AuthResponse;
import school.hei.admin.entity.Account;
import school.hei.admin.exception.ApiException;
import school.hei.admin.mapper.AccountMapper;
import school.hei.admin.repository.AccountRepository;
import school.hei.admin.repository.model.JAccount;

@Service
@AllArgsConstructor
public class AuthService {
  private final AccountRepository accountRepository;
  private final AccountMapper accountMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;

  public AuthResponse login(LoginRequest request) {
    JAccount entity =
        accountRepository
            .findByUsername(request.username())
            .orElseThrow(() -> new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED));
    if (!passwordEncoder.matches(request.password(), entity.getPassword())) {
      throw new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }
    Account account = accountMapper.toModel(entity);
    return AuthResponse.builder()
        .token(jwtTokenProvider.generateToken(account))
        .accountId(account.id())
        .username(account.username())
        .role(account.role())
        .build();
  }
}
