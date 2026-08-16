package school.hei.admin.entity;

import java.util.UUID;
import lombok.Builder;

@Builder
public record StudentGroup(UUID id, UUID studentId, UUID groupId, int semester) {}
