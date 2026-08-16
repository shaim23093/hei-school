package school.hei.admin.entity;

import java.util.UUID;
import lombok.Builder;
import school.hei.admin.entity.enums.Role;

@Builder
public record Account(UUID id, String username, String password, Role role) {}
