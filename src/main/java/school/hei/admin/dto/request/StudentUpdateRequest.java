package school.hei.admin.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Builder;

@Builder
public record StudentUpdateRequest(
    UUID promotionId,
    @Pattern(regexp = "^STD\\d{5}$", message = "must match STDxxxxx format") String std,
    @Size(max = 100) String name,
    @Size(max = 100) String firstName,
    @Email @Size(max = 150) String email) {}
