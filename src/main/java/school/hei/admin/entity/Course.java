package school.hei.admin.entity;

import java.util.UUID;
import lombok.Builder;
import school.hei.admin.entity.enums.Path;

@Builder
public record Course(
    UUID id, String code, String name, int credits, Path path, int semester, int academicYear) {}
