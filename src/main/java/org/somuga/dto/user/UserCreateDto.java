package org.somuga.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static org.somuga.util.message.Messages.*;

@Schema(description = "DTO for creating a user")
public record UserCreateDto(
        @Schema(description = "Username of the user", example = "user123")
        @NotBlank(message = NON_EMPTY_USERNAME)
        @Pattern(regexp = "\\w{4,20}", message = INVALID_USERNAME)
        String userName,
        @Schema(description = "The user's email", example = "john@example.com")
        @NotBlank(message = INVALID_EMAIL)
        @Pattern(regexp = "^[\\w-]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = INVALID_EMAIL)
        String email
) {
}
