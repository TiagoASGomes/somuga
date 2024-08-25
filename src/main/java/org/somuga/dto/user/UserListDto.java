package org.somuga.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record UserListDto(
        @Schema(description = "List of users")
        List<UserPublicDto> users,
        @Schema(description = "Total number of users fitting the criteria", example = "10")
        Long count
) {
}
