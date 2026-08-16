package school.hei.admin.entity;

import java.util.UUID;
import lombok.Builder;

@Builder
public record Teacher(UUID id, UUID accountId, String name, String firstName, String email) {}
