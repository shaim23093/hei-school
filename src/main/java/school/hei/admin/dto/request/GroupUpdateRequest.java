package school.hei.admin.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import school.hei.admin.entity.enums.Path;

@Builder
public record GroupUpdateRequest(@Size(min = 1, max = 100) String name, Path path) {}
