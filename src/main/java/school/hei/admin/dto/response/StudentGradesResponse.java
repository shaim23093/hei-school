package school.hei.admin.dto.response;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record StudentGradesResponse(
    UUID studentId,
    String std,
    String name,
    String firstName,
    String promotionName,
    Double average,
    int validatedCredits,
    int totalCredits,
    String status,
    List<CourseGradeResult> results) {}
