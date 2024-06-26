package org.somuga.dto.developer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

import static org.somuga.util.message.Messages.INVALID_DEVELOPER_NAME;

public record DeveloperCreateDto(
        @NotBlank(message = INVALID_DEVELOPER_NAME)
        @Pattern(regexp = "^[a-zA-Z0-9 ]{1,255}$", message = INVALID_DEVELOPER_NAME)
        String developerName,
        List<String> socials
) {
}
