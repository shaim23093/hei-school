package school.hei.admin.dto.response;

import java.util.UUID;
import lombok.Builder;

@Builder
public record StudentResultResponse(
    UUID studentId,
    String std,
    String name,
    String firstName,
    Double average,
    int validatedCredits,
    int totalCredits,
    String status) {}
