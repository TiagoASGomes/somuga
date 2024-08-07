package org.somuga.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static org.somuga.util.message.Messages.INVALID_USERNAME;
import static org.somuga.util.message.Messages.NON_EMPTY_USERNAME;

@Schema(description = "DTO for creating a user")
public record UserCreateDto(
        @Schema(description = "Username of the user", example = "user123")
        @NotBlank(message = NON_EMPTY_USERNAME)
        @Pattern(regexp = "\\w{4,20}", message = INVALID_USERNAME)
        String userName
) {
}
