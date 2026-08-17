package school.hei.admin.conf;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.time.Instant;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;
import school.hei.admin.entity.Account;

@Component
public class JwtTokenProvider {

  @Value("${security.jwt.secret}")
  private String secret;

  @Value("${security.jwt.expiration-ms}")
  private long expirationMs;

  @Bean
  JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withSecretKey(secretKey()).macAlgorithm(MacAlgorithm.HS256).build();
  }

  public String generateToken(Account account) {
    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .subject(account.id().toString())
            .claim("username", account.username())
            .claim("role", account.role().name())
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusMillis(expirationMs))
            .build();

    return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey()))
        .encode(JwtEncoderParameters.from(header, claims))
        .getTokenValue();
  }

  private SecretKey secretKey() {
    return new SecretKeySpec(secret.getBytes(UTF_8), "HmacSHA256");
  }
}
