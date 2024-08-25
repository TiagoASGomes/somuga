package org.somuga.dto.developer;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record DeveloperListDto(
        @Schema(description = "List of developers")
        List<DeveloperPublicDto> developers,
        @Schema(description = "Total number of developers fitting the search criteria", example = "10")
        Long count
) {
}
