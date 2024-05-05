package org.somuga.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static org.somuga.util.message.Messages.*;

public record UserCreateDto(
        @NotBlank(message = NON_EMPTY_USERNAME)
        @Pattern(regexp = "[\\w]{4,20}", message = INVALID_USERNAME)
        String userName,
        @NotBlank(message = NON_EMPTY_EMAIL)
        @Pattern(regexp = "^([a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$", message = INVALID_EMAIL)
        String email
) {
}
