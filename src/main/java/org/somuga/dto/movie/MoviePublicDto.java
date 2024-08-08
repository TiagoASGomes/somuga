package org.somuga.dto.movie;

import io.swagger.v3.oas.annotations.media.Schema;
import org.somuga.dto.crew_role.MovieRolePublicDto;
import org.somuga.dto.media.MediaPublicDto;

import java.util.Date;
import java.util.List;

@Schema(description = "Movie public DTO")
public record MoviePublicDto(
        @Schema(description = "Movie ID", example = "1")
        Long id,
        @Schema(description = "Movie title", example = "The Matrix")
        String title,
        @Schema(description = "Movie release date", example = "1999-03-31")
        Date releaseDate,
        @Schema(description = "Movie description", example = "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.")
        String description,
        @Schema(description = "Movie duration in minutes", example = "136")
        Integer duration,
        @Schema(description = "The movie cast")
        List<MovieRolePublicDto> crew,
        @Schema(description = "URL to imdb page of the movie", example = "https://www.imdb.com/title/tt0133093/")
        String mediaUrl,
        @Schema(description = "URL to image of the movie", example = "https://www.matrix.com/image.jpg")
        String imageUrl,
        @Schema(description = "Amount of movie likes", example = "100")
        int likes,
        @Schema(description = "Movie average rating", example = "8")
        int averageRating
) implements MediaPublicDto {
}
