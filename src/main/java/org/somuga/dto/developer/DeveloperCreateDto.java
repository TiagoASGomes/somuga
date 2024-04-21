package org.somuga.dto.developer;

import jakarta.validation.constraints.NotBlank;

import static org.somuga.message.Messages.INVALID_DEVELOPER_NAME;

public record DeveloperCreateDto(
        @NotBlank(message = INVALID_DEVELOPER_NAME)
        String developerName
) {
}
