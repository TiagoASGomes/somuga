package org.somuga.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.dto.game_genre.GameGenrePublicDto;
import org.somuga.dto.media.MediaPublicDto;
import org.somuga.dto.platform.PlatformPublicDto;

import java.util.Date;
import java.util.List;

@Schema(description = "Game public DTO")
public record GamePublicDto(
        @Schema(description = "Game ID", example = "1")
        Long id,
        @Schema(description = "Game title", example = "Minecraft")
        String title,
        @Schema(description = "Game release date", example = "2011-11-18")
        Date releaseDate,
        @Schema(description = "Game developer")
        DeveloperPublicDto developer,
        @Schema(description = "Game genres")
        List<GameGenrePublicDto> genres,
        @Schema(description = "Game platforms")
        List<PlatformPublicDto> platforms,
        @Schema(description = "Game price", example = "20.00")
        Double price,
        @Schema(description = "Game description", example = "Minecraft is a sandbox video game developed by Mojang Studios.")
        String description,
        @Schema(description = "Number of game reviews", example = "100")
        int reviews,
        @Schema(description = "Number of game likes", example = "1000")
        int likes,
        @Schema(description = "A link the game official website", example = "https://www.minecraft.net")
        String mediaUrl,
        @Schema(description = "A link to the game image", example = "https://www.minecraft.net/image.jpg")
        String imageUrl
) implements MediaPublicDto {
}
