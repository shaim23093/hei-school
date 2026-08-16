package school.hei.admin.entity;

import java.util.UUID;
import lombok.Builder;

@Builder
public record CourseGroup(UUID id, UUID courseId, UUID groupId, UUID promotionId) {}
