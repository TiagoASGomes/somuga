package org.somuga.dto.platform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static org.somuga.message.Messages.INVALID_PLATFORM_NAME;

public record PlatformCreateDto(
        @NotBlank(message = INVALID_PLATFORM_NAME)
        @Pattern(regexp = "^[a-zA-Z0-9 ]{3,50}$", message = INVALID_PLATFORM_NAME)
        String platformName
) {
}
