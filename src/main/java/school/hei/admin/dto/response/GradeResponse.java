package school.hei.admin.dto.response;

import java.util.UUID;
import lombok.Builder;

@Builder
public record GradeResponse(UUID id, UUID studentId, UUID examId, double value) {}
