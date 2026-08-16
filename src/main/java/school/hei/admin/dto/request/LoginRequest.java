package school.hei.admin.dto.request;

import lombok.Builder;

@Builder
public record LoginRequest(String username, String password) {}
