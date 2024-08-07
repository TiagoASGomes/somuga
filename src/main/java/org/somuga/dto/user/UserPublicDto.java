package org.somuga.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "DTO for a user")
public record UserPublicDto(
        @Schema(description = "Unique identifier of the user", example = "auth0|1234567890")
        String id,
        @Schema(description = "Username of the user", example = "user123")
        String userName,
        @Schema(description = "Date when the user joined", example = "2021-08-02T21:14:59.981Z")
        Date joinedDate
) {
}
