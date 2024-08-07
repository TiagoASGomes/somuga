package org.somuga.dto.developer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

import static org.somuga.util.message.Messages.INVALID_DEVELOPER_NAME;

@Schema(description = "Developer create data transfer object")
public record DeveloperCreateDto(
        @NotBlank(message = INVALID_DEVELOPER_NAME)
        @Pattern(regexp = "^[a-zA-Z0-9 ]{1,255}$", message = INVALID_DEVELOPER_NAME)
        @Schema(description = "The developer name", example = "Mojang")
        String developerName,
        @Schema(description = "A list of social media links", example = "[\"https://twitter.com/mojang\",\"https://www.facebook.com/mojang\"]")
        List<String> socials
) {
}
