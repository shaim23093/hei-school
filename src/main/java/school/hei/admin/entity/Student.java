package school.hei.admin.entity;

import java.util.UUID;
import lombok.Builder;

@Builder
public record Student(
    UUID id,
    UUID accountId,
    UUID promotionId,
    String std,
    String name,
    String firstName,
    String email) {}
