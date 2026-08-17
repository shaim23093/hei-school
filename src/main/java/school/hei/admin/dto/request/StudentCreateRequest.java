package school.hei.admin.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Builder;

@Builder
public record StudentCreateRequest(
    @NotBlank @Size(min = 3, max = 50) String username,
    @NotBlank @Size(min = 8, max = 100) String password,
    @NotNull UUID promotionId,
    @NotBlank @Pattern(regexp = "^STD\\d{5}$", message = "must match STDxxxxx format") String std,
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Size(max = 100) String firstName,
    @NotBlank @Email @Size(max = 150) String email) {}
