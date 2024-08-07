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
import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GameLikePublicDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.game.GameNotFoundException;
import org.somuga.exception.game_genre.GenreNotFoundException;
import org.somuga.exception.platform.PlatformNotFoundException;
import org.somuga.service.interfaces.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/game")
@Tag(name = "Game", description = "The game API")
public class GameController {

    private final IGameService gameService;

    @Autowired
    public GameController(IGameService gameService) {
        this.gameService = gameService;
    }


    @Operation(summary = "Get all games",
            description = "Returns a list of games. Can be filtered by title, platform, genre and developer.")
    @ApiResponse(responseCode = "200",
            description = "List of games",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = GamePublicDto.class)))})
    @Parameter(name = "title", description = "The game title to search for", example = "Minecraft")
    @Parameter(name = "platform", description = "The platform to search for", example = "PC")
    @Parameter(name = "genre", description = "The genre to search for", example = "Sandbox")
    @Parameter(name = "developer", description = "The developer to search for", example = "Mojang")
    @Parameter(name = "size", description = "The number of elements to return", example = "10")
    @Parameter(name = "page", description = "The page number to return", example = "0")
    @GetMapping("/public")
    public ResponseEntity<List<GamePublicDto>> getAll(Pageable page,
                                                      @RequestParam(required = false) String title,
                                                      @RequestParam(required = false) List<String> platform,
                                                      @RequestParam(required = false) List<String> genre,
                                                      @RequestParam(required = false) String developer) {
        return new ResponseEntity<>(gameService.getAll(page, title, platform, genre, developer), HttpStatus.OK);
    }

    @Operation(summary = "Get a game by id",
            description = "Returns a game by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The game",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameLikePublicDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Game not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))
            )})
    @Parameter(name = "id", description = "The game id", example = "1", required = true)
    @GetMapping("/public/{id}")
    public ResponseEntity<GameLikePublicDto> getById(@PathVariable Long id) throws GameNotFoundException {
        return new ResponseEntity<>(gameService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Create a game",
            description = "Create a game with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "The created game",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GamePublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid data",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Developer, genre or platform not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
    })
    @PostMapping("/private")
    public ResponseEntity<GamePublicDto> create(@Valid @RequestBody GameCreateDto game) throws DeveloperNotFoundException, GenreNotFoundException, PlatformNotFoundException {
        return new ResponseEntity<>(gameService.create(game), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a game",
            description = "Update a game with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The updated game",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GamePublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid data",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Game was not created by the user",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Game, developer, genre or platform not found",
                    content = @Content)
    })
    @Parameter(name = "id", description = "The game id", example = "1", required = true)
    @PutMapping("/private/{id}")
    public ResponseEntity<GamePublicDto> update(@PathVariable Long id, @Valid @RequestBody GameCreateDto game) throws GameNotFoundException, DeveloperNotFoundException, PlatformNotFoundException, GenreNotFoundException, InvalidPermissionException {
        return new ResponseEntity<>(gameService.update(id, game), HttpStatus.OK);
    }

    @Operation(summary = "Delete a game",
            description = "Delete a game by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Game deleted",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Game was not created by the user",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Game not found",
                    content = @Content)
    })
    @Parameter(name = "id", description = "The game id", example = "1", required = true)
    @DeleteMapping("/private/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws GameNotFoundException, InvalidPermissionException {
        gameService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Admin delete a game",
            description = "Delete a game by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Game deleted",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Game not found",
                    content = @Content)
    })
    @Parameter(name = "id", description = "The game id", example = "1", required = true)
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> adminDelete(@PathVariable Long id) throws GameNotFoundException {
        gameService.adminDelete(id);
        return ResponseEntity.noContent().build();
    }

}
