package school.hei.admin.entity;

import java.util.UUID;
import lombok.Builder;

@Builder
public record Promotion(UUID id, String name, int entryYear) {}
