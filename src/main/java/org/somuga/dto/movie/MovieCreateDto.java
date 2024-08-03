package org.somuga.dto.movie;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.somuga.dto.crew_role.MovieRoleCreateDto;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

import static org.somuga.util.message.Messages.*;

@Schema(description = "DTO for creating a movie")
public record MovieCreateDto(
        @Schema(description = "Title of the movie", example = "The Matrix")
        @NotBlank(message = INVALID_TITLE)
        @Size(max = 255, message = MAX_TITLE_CHARACTERS)
        String title,
        @Schema(description = "Release date of the movie", example = "1999-03-31")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Past(message = INVALID_RELEASE_DATE)
        @NotNull(message = INVALID_RELEASE_DATE)
        Date releaseDate,
        @Schema(description = "Description of the movie", example = "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.")
        @NotBlank(message = INVALID_DESCRIPTION)
        @Size(max = 1000, message = MAX_DESCRIPTION_CHARACTERS)
        String description,
        @Schema(description = "Duration of the movie in minutes", example = "136")
        @Min(value = 1, message = INVALID_DURATION)
        @Max(value = 1440, message = INVALID_DURATION)
        @NotNull(message = INVALID_DURATION)
        Integer duration,
        @Schema(description = "Cast of the movie")
        @NotNull(message = INVALID_CREW_ROLE)
        @Size(min = 1, message = INVALID_CREW_ROLE)
        List<MovieRoleCreateDto> crew,
        @Schema(description = "URL to imdb page of the movie", example = "https://www.imdb.com/title/tt0133093/")
        @NotBlank(message = INVALID_MEDIA_URL)
        String mediaUrl,
        @Schema(description = "URL to image of the movie", example = "https://www.matrix.com/image.jpg")
        String imageUrl
) {
}
