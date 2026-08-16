package school.hei.admin.dto.response;

import java.util.UUID;
import lombok.Builder;

@Builder
public record AuthResponse(String token, UUID accountId, String username, school.hei.admin.enums.Role role) {}
