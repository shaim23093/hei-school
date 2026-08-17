package school.hei.admin.conf;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Configuration
public class JwtConfig {

  @Bean
  JwtAuthenticationConverter jwtAuthenticationConverter() {
    var converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(
        jwt -> {
          String role = jwt.getClaimAsString("role");
          if (role == null || role.isBlank()) {
            return List.of();
          }
          return List.of(new SimpleGrantedAuthority("ROLE_" + role));
        });
    converter.setPrincipalClaimName("sub");
    return converter;
  }
}
