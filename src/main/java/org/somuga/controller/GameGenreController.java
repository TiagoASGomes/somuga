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
import org.somuga.dto.game_genre.GameGenreCreateDto;
import org.somuga.dto.game_genre.GameGenrePublicDto;
import org.somuga.exception.game_genre.GenreAlreadyExistsException;
import org.somuga.exception.game_genre.GenreNotFoundException;
import org.somuga.service.interfaces.IGameGenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/game/genre")
@Tag(name = "Game Genre", description = "The game genre API")
public class GameGenreController {

    private final IGameGenreService gameGenreService;

    @Autowired
    public GameGenreController(IGameGenreService gameGenreService) {
        this.gameGenreService = gameGenreService;
    }

    @Operation(summary = "Get all genres",
            description = "Returns a list of genres. If the name parameter is provided, it will return genres containing the name.")
    @ApiResponse(responseCode = "200",
            description = "List of genres",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = GameGenrePublicDto.class)))})
    @Parameter(name = "name", description = "The genre name to search for", example = "Sandbox")
    @GetMapping("/public")
    public ResponseEntity<List<GameGenrePublicDto>> getAll(@RequestParam(required = false) String name) {
        return new ResponseEntity<>(gameGenreService.getAll(name), HttpStatus.OK);
    }

    @Operation(summary = "Get a genre by id",
            description = "Returns a genre by its id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The genre",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameGenrePublicDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Genre not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))
            )})
    @Parameter(name = "id", description = "The genre id to search for", example = "1", required = true)
    @GetMapping("/public/{id}")
    public ResponseEntity<GameGenrePublicDto> getById(@PathVariable Long id) throws GenreNotFoundException {
        return new ResponseEntity<>(gameGenreService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Create a genre",
            description = "Creates a new genre.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "The created genre",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameGenrePublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Validation error or genre already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
                    content = @Content)})
    @PostMapping("/admin")
    public ResponseEntity<GameGenrePublicDto> create(@Valid @RequestBody GameGenreCreateDto genreDto) throws GenreAlreadyExistsException {
        return new ResponseEntity<>(gameGenreService.create(genreDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a genre",
            description = "Updates a genre.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The updated genre",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameGenrePublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Validation error or genre already exists",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Genre not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @Parameter(name = "id", description = "The genre id to update", example = "1", required = true)
    @PutMapping("/admin/{id}")
    public ResponseEntity<GameGenrePublicDto> update(@PathVariable Long id, @Valid @RequestBody GameGenreCreateDto genreDto) throws GenreNotFoundException, GenreAlreadyExistsException {
        return new ResponseEntity<>(gameGenreService.update(id, genreDto), HttpStatus.OK);
    }

    @Operation(summary = "Delete a genre",
            description = "Deletes a genre.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Genre deleted",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Genre not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @Parameter(name = "id", description = "The genre id to delete", example = "1", required = true)
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws GenreNotFoundException {
        gameGenreService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
