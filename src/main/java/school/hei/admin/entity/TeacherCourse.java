package school.hei.admin.entity;

import java.util.UUID;
import lombok.Builder;

@Builder
public record TeacherCourse(
    UUID id, UUID teacherId, UUID courseId, UUID groupId, UUID promotionId) {}
