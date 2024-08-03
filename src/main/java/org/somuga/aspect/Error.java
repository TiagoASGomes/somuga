package org.somuga.aspect;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response")
public record Error(
        @Schema(description = "The error message", example = "Error message")
        String message
) {

}
