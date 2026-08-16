package school.hei.admin.entity;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record Exam(
    UUID id,
    UUID courseId,
    UUID promotionId,
    String title,
    Instant dateTime,
    int durationMinutes,
    double coefficient) {}
