package school.hei.admin.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
  private final ObjectMapper objectMapper;

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/error", "/ping", "/v3/api-docs/**", "/swagger-ui/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/students")
                    .hasAnyRole("ADMIN", "TEACHER")
                    .requestMatchers(HttpMethod.GET, "/students/*")
                    .hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                    .requestMatchers(HttpMethod.POST, "/students")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/students/*")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/students/*")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/students/*/grades")
                    .hasAnyRole("STUDENT", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/students/*/transcript")
                    .hasAnyRole("STUDENT", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/students/*/academic-year")
                    .hasAnyRole("STUDENT", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/teachers")
                    .hasAnyRole("ADMIN", "TEACHER")
                    .requestMatchers(HttpMethod.GET, "/teachers/*")
                    .hasAnyRole("ADMIN", "TEACHER")
                    .requestMatchers(HttpMethod.POST, "/teachers")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/teachers/*")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/teachers/*")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/grades/*")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/grades/*/history")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/promotions/*/diplomes*")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/promotions/*/results")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/promotions")
                    .hasAnyRole("ADMIN", "TEACHER")
                    .requestMatchers(HttpMethod.GET, "/promotions/*")
                    .hasAnyRole("ADMIN", "TEACHER")
                    .requestMatchers(HttpMethod.POST, "/promotions")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/promotions/*")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/promotions/*")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/groups")
                    .hasAnyRole("ADMIN", "TEACHER")
                    .requestMatchers(HttpMethod.GET, "/groups/*")
                    .hasAnyRole("ADMIN", "TEACHER")
                    .requestMatchers(HttpMethod.POST, "/groups")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/groups/*")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/groups/*")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                        (request, response, e) -> writeJsonError(response, 401, "Unauthorized"))
                    .accessDeniedHandler(
                        (request, response, e) -> writeJsonError(response, 403, "Forbidden")));
    return http.build();
  }

  private void writeJsonError(HttpServletResponse response, int status, String message)
      throws IOException {
    response.setStatus(status);
    response.setContentType("application/json");
    response
        .getWriter()
        .write(
            objectMapper.writeValueAsString(
                Map.of("status", status, "error", message, "timestamp", Instant.now().toString())));
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
