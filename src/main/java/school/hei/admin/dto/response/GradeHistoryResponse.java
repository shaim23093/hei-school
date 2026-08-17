package school.hei.admin.dto.response;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record GradeHistoryResponse(
    UUID id,
    UUID gradeId,
    UUID examId,
    String courseCode,
    double value,
    Instant modifiedAt,
    String author) {}
