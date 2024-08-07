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
import org.somuga.dto.developer.DeveloperCreateDto;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.user.DuplicateFieldException;
import org.somuga.service.interfaces.IDeveloperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/game/developer")
@Tag(name = "Developer", description = "The developer API")
public class DeveloperController {

    private final IDeveloperService developerService;

    @Autowired
    public DeveloperController(IDeveloperService developerService) {
        this.developerService = developerService;
    }

    @Operation(summary = "Get all developers",
            description = "Returns a list of developers. If the name parameter is provided, it will return developers containing the name.")
    @ApiResponse(responseCode = "200",
            description = "List of developers",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = DeveloperPublicDto.class)))})
    @Parameter(name = "name", description = "The developer name to search for", example = "Mojang")
    @GetMapping("/public")
    public ResponseEntity<List<DeveloperPublicDto>> getAll(@RequestParam(required = false) String name) {
        return new ResponseEntity<>(developerService.getAll(name), HttpStatus.OK);
    }

    @Operation(summary = "Get a developer by id",
            description = "Returns a developer by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The developer",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeveloperPublicDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Developer not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))
            )})
    @Parameter(name = "id", description = "The developer id", example = "1", required = true)
    @GetMapping("/public/{id}")
    public ResponseEntity<DeveloperPublicDto> getById(@PathVariable Long id) throws DeveloperNotFoundException {
        return new ResponseEntity<>(developerService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Create a developer",
            description = "Create a developer with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "The created developer",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeveloperPublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid data or developer name already exists",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
                    content = @Content),
    })
    @PostMapping("/admin")
    public ResponseEntity<DeveloperPublicDto> create(@Valid @RequestBody DeveloperCreateDto developerDto) throws DuplicateFieldException {
        return new ResponseEntity<>(developerService.create(developerDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a developer",
            description = "Update a developer with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The updated developer",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeveloperPublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid data or developer name already exists",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Developer to update not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
                    content = @Content),
    })
    @Parameter(name = "id", description = "The developer id", example = "1", required = true)
    @PutMapping("/admin/{id}")
    public ResponseEntity<DeveloperPublicDto> update(@PathVariable Long id, @Valid @RequestBody DeveloperCreateDto developerDto) throws DeveloperNotFoundException, DuplicateFieldException {
        return new ResponseEntity<>(developerService.update(id, developerDto), HttpStatus.OK);
    }

    @Operation(summary = "Delete a developer",
            description = "Delete a developer by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Developer deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Developer not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
                    content = @Content),
    })
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws DeveloperNotFoundException {
        developerService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
