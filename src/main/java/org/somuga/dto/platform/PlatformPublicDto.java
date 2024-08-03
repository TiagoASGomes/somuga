package org.somuga.dto.platform;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for a platform")
public record PlatformPublicDto(
        @Schema(description = "Unique identifier of the platform", example = "1")
        Long id,
        @Schema(description = "Name of the platform", example = "PlayStation 5")
        String platformName
) {
}
