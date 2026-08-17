package school.hei.admin.dto.response;

import java.util.UUID;
import lombok.Builder;
import school.hei.admin.entity.enums.Path;

@Builder
public record GraduatedStudentResponse(
    UUID studentId,
    String std,
    String name,
    String firstName,
    Path path,
    double average,
    int rank) {}
