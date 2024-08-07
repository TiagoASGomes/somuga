package org.somuga.dto.platform;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static org.somuga.util.message.Messages.INVALID_PLATFORM_NAME;

@Schema(description = "DTO for creating a platform")
public record PlatformCreateDto(
        @Schema(description = "Name of the platform", example = "PlayStation 5")
        @NotBlank(message = INVALID_PLATFORM_NAME)
        @Pattern(regexp = "^[a-zA-Z0-9 ]{1,50}$", message = INVALID_PLATFORM_NAME)
        String platformName
) {
}
