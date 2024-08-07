package org.somuga.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.somuga.aspect.ErrorDto;
import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MovieLikePublicDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.movie.InvalidCrewRoleException;
import org.somuga.exception.movie.MovieNotFoundException;
import org.somuga.exception.movie_crew.MovieCrewNotFoundException;
import org.somuga.service.interfaces.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
@Tag(name = "Movie", description = "The movie API")
public class MovieController {

    private final IMovieService movieService;

    @Autowired
    public MovieController(IMovieService movieService) {
        this.movieService = movieService;
    }

    @Operation(summary = "Get all movies",
            description = "Returns a list of movies. Can be filtered by title and crew.")
    @ApiResponse(responseCode = "200",
            description = "List of movies",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = MoviePublicDto.class)))})
    @Parameter(name = "title", description = "The movie title to search for", example = "The Godfather")
    @Parameter(name = "crewIds", description = "A list of crew members ids to search for", example = "1,2,3")
    @Parameter(name = "size", description = "The number of elements to return", example = "10")
    @Parameter(name = "page", description = "The page number to return", example = "0", schema = @Schema(type = "integer"))
    @GetMapping("/public")
    public ResponseEntity<List<MoviePublicDto>> getAll(Pageable page,
                                                       @RequestParam(required = false) String title,
                                                       @RequestParam(required = false) List<Long> crewIds) {
        return new ResponseEntity<>(movieService.getAll(page, title, crewIds), HttpStatus.OK);
    }

    @Operation(summary = "Get movie by id",
            description = "Returns a movie by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Movie found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieLikePublicDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Movie not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
    })
    @Parameter(name = "id", description = "The movie id to search for", example = "1", required = true)
    @GetMapping("/public/{id}")
    public ResponseEntity<MovieLikePublicDto> getById(@PathVariable Long id) throws MovieNotFoundException {
        return new ResponseEntity<>(movieService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Create a movie",
            description = "Creates a movie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "The movie",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MoviePublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Crew member not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})
    })
    @PostMapping("/private")
    public ResponseEntity<MoviePublicDto> create(@Valid @RequestBody MovieCreateDto movie) throws MovieCrewNotFoundException, InvalidCrewRoleException {
        return new ResponseEntity<>(movieService.create(movie), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a movie",
            description = "Updates a movie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The movie",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MoviePublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Invalid permission, movie does not belong to user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Movie or crew member not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})
    })
    @Parameter(name = "id", description = "The movie id to update", example = "1", required = true)
    @PutMapping("/private/{id}")
    public ResponseEntity<MoviePublicDto> update(@PathVariable Long id, @Valid @RequestBody MovieCreateDto movie) throws MovieNotFoundException, MovieCrewNotFoundException, InvalidCrewRoleException, InvalidPermissionException {
        return new ResponseEntity<>(movieService.update(id, movie), HttpStatus.OK);
    }

    @Operation(summary = "Delete a movie",
            description = "Deletes a movie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "The movie was deleted",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Invalid permission, movie does not belong to user",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Movie not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})
    })
    @Parameter(name = "id", description = "The movie id to delete", example = "1", required = true)
    @DeleteMapping("/private/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws MovieNotFoundException, InvalidPermissionException {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
