package school.hei.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import school.hei.admin.entity.enums.Path;

@Builder
public record GroupCreateRequest(
    @NotBlank @jakarta.validation.constraints.Size(min = 1, max = 100) String name,
    @NotNull Path path) {}
