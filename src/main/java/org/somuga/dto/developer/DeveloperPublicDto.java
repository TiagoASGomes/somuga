package org.somuga.dto.developer;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Developer public data transfer object")
public record DeveloperPublicDto(
        @Schema(description = "The developer id", example = "1")
        Long id,
        @Schema(description = "The developer name", example = "Mojang")
        String developerName
) {
}
