package school.hei.admin.dto.response;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PromotionResultsResponse(
    UUID promotionId, String promotionName, List<StudentResultResponse> results) {}
