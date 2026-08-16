package school.hei.admin.entity;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record Grade(UUID id, UUID studentId, UUID examId, double value, Instant createdAt) {}
