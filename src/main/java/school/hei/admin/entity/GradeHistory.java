package school.hei.admin.entity;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record GradeHistory(
    UUID id,
    UUID gradeId,
    UUID studentId,
    UUID examId,
    double value,
    Instant modifiedAt,
    String author) {}
