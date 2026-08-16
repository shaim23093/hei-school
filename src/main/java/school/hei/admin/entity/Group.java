package school.hei.admin.entity;

import java.util.UUID;
import lombok.Builder;
import school.hei.admin.entity.enums.Path;

@Builder
public record Group(UUID id, String name, Path path) {}
