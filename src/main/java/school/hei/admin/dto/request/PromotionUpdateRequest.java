package school.hei.admin.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record PromotionUpdateRequest(
    @jakarta.validation.constraints.Size(min = 1, max = 100) String name,
    @Min(2000) @Max(2100) Integer entryYear) {}
