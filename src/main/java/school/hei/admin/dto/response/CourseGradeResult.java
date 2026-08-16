package school.hei.admin.dto.response;

import java.util.UUID;
import lombok.Builder;
import school.hei.admin.entity.enums.Path;

@Builder
public record CourseGradeResult(
    UUID courseId,
    String code,
    String name,
    int credits,
    int semester,
    Path path,
    Double average,
    boolean complete,
    boolean validated) {}
