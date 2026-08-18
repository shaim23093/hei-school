package school.hei.admin.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record AcademicYearResult(
    int academicYear,
    Double average,
    int validatedCredits,
    int totalCredits,
    String status,
    List<CourseGradeResult> results) {}
