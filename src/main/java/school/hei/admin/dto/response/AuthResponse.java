package school.hei.admin.dto.response;

import java.util.UUID;
import lombok.Builder;
import school.hei.admin.entity.enums.Role;

@Builder
public record AuthResponse(
    String token, UUID accountId, String username, Role role) {}
