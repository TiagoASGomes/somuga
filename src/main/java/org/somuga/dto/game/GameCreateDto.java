package org.somuga.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

import static org.somuga.util.message.Messages.*;

@Schema(description = "Game create data transfer object")
public record GameCreateDto(
        @Schema(description = "The game title", example = "Minecraft")
        @NotBlank(message = INVALID_TITLE)
        @Size(max = 255, message = MAX_TITLE_CHARACTERS)
        String title,
        @Schema(description = "The game release date", example = "2011-11-18")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = INVALID_RELEASE_DATE)
        Date releaseDate,
        @Schema(description = "The game developer id", example = "1")
        @NotNull(message = INVALID_DEVELOPER)
        Long developerId,
        @Schema(description = "A list of the game genre ids", example = "[1,2]")
        @NotNull(message = INVALID_GENRES)
        @Size(min = 1, message = INVALID_GENRES)
        List<Long> genreIds,
        @Schema(description = "A list of the game platform ids", example = "[1,2]")
        @NotNull(message = INVALID_PLATFORMS)
        @Size(min = 1, message = INVALID_PLATFORMS)
        List<Long> platformsIds,
        @Schema(description = "The game price", example = "20.00")
        @Min(value = 0, message = INVALID_PRICE)
        @Max(value = 1000, message = INVALID_PRICE)
        @NotNull(message = INVALID_PRICE)
        Double price,
        @Schema(description = "A description of the game", example = "Minecraft is a sandbox video game developed by Mojang Studios.")
        @NotBlank(message = INVALID_DESCRIPTION)
        @Size(max = 1000, message = MAX_DESCRIPTION_CHARACTERS)
        String description,
        @Schema(description = "A link the game official website", example = "https://www.minecraft.net")
        @NotBlank(message = INVALID_MEDIA_URL)
        String mediaUrl,
        @Schema(description = "A link to the game image", example = "https://www.minecraft.net/image.jpg")
        String imageUrl
) {
}
