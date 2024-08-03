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
import org.somuga.dto.movie_crew.MovieCrewCreateDto;
import org.somuga.dto.movie_crew.MovieCrewPublicDto;
import org.somuga.exception.movie_crew.MovieCrewNotFoundException;
import org.somuga.service.interfaces.IMovieCrewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movie/crew")
@Tag(name = "Movie Crew", description = "The movie crew API")
public class MovieCrewController {

    private final IMovieCrewService movieCrewService;

    @Autowired
    public MovieCrewController(IMovieCrewService movieCrewService) {
        this.movieCrewService = movieCrewService;
    }

    @Operation(summary = "Get all movie crew",
            description = "Returns a list of movie crew. Can be filtered by name.")
    @ApiResponse(responseCode = "200",
            description = "List of movie crew",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = MovieCrewPublicDto.class)))})
    @Parameter(name = "name", description = "Name of the crew member", example = "John Doe")
    @Parameter(name = "page", description = "Page number", example = "0")
    @Parameter(name = "size", description = "Number of elements per page", example = "10")
    @GetMapping("/public")
    public ResponseEntity<List<MovieCrewPublicDto>> getAll(Pageable page, @RequestParam(required = false) String name) {
        return new ResponseEntity<>(movieCrewService.getAll(page, name), HttpStatus.OK);
    }

    @Operation(summary = "Get movie crew by ID",
            description = "Returns a single movie crew by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Movie crew found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieCrewPublicDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Movie crew not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @Parameter(name = "id", description = "ID of the crew member", example = "1")
    @GetMapping("/public/{id}")
    public ResponseEntity<MovieCrewPublicDto> getById(@PathVariable Long id) throws MovieCrewNotFoundException {
        return new ResponseEntity<>(movieCrewService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Create movie crew",
            description = "Creates a new movie crew")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Movie crew created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieCrewPublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not enough permissions",
                    content = @Content)})
    @PostMapping("/admin")
    public ResponseEntity<MovieCrewPublicDto> create(@Valid @RequestBody MovieCrewCreateDto movieCrew) {
        return new ResponseEntity<>(movieCrewService.create(movieCrew), HttpStatus.CREATED);
    }

    @Operation(summary = "Update movie crew",
            description = "Updates an existing movie crew")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Movie crew updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieCrewPublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not enough permissions",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Movie crew not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @Parameter(name = "id", description = "ID of the crew member", example = "1")
    @PutMapping("/admin/{id}")
    public ResponseEntity<MovieCrewPublicDto> update(@PathVariable Long id, @Valid @RequestBody MovieCrewCreateDto movieCrew) throws MovieCrewNotFoundException {
        return new ResponseEntity<>(movieCrewService.update(id, movieCrew), HttpStatus.OK);
    }

    @Operation(summary = "Delete movie crew",
            description = "Deletes an existing movie crew")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Movie crew deleted"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not enough permissions",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Movie crew not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @Parameter(name = "id", description = "ID of the crew member", example = "1")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws MovieCrewNotFoundException {
        movieCrewService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
