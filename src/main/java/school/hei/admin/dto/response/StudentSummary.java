package school.hei.admin.dto.response;

import lombok.Builder;

@Builder
public record StudentSummary(
    Double average, int validatedCredits, int totalCredits, String status) {}
