package org.somuga.dto.platform;

import jakarta.validation.constraints.NotBlank;

import static org.somuga.message.Messages.INVALID_PLATFORM_NAME;

public record PlatformCreateDto(
        @NotBlank(message = INVALID_PLATFORM_NAME)
        String platformName
) {
}
