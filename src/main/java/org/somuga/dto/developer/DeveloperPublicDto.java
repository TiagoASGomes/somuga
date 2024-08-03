package org.somuga.dto.developer;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Developer public data transfer object")
public record DeveloperPublicDto(
        @Schema(description = "The developer id", example = "1")
        Long id,
        @Schema(description = "The developer name", example = "Mojang")
        String developerName,
        @Schema(description = "A list of social media links", example = "[\"https://twitter.com/mojang\",\"https://www.facebook.com/mojang\"]")
        List<String> socials
) {
}
