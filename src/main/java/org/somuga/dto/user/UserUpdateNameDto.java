package org.somuga.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static org.somuga.util.message.Messages.INVALID_USERNAME;
import static org.somuga.util.message.Messages.NON_EMPTY_USERNAME;

public record UserUpdateNameDto(
        @NotBlank(message = NON_EMPTY_USERNAME)
        @Pattern(regexp = "[\\w]{4,20}", message = INVALID_USERNAME)
        String userName
) {
}
