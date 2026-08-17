package school.hei.admin.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record TeacherUpdateRequest(
    @Size(max = 100) String name,
    @Size(max = 100) String firstName,
    @Email @Size(max = 150) String email) {}
