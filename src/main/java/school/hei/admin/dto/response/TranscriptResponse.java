package school.hei.admin.dto.response;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TranscriptResponse(
    UUID studentId,
    String std,
    String name,
    String firstName,
    String promotionName,
    Integer semester,
    Double average,
    int validatedCredits,
    int totalCredits,
    String status,
    List<CourseGradeResult> results) {}
