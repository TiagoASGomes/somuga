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
import org.somuga.dto.platform.PlatformCreateDto;
import org.somuga.dto.platform.PlatformPublicDto;
import org.somuga.exception.platform.PlatformAlreadyExistsException;
import org.somuga.exception.platform.PlatformNotFoundException;
import org.somuga.service.interfaces.IPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/game/platform")
@Tag(name = "Platform", description = "Platform API")
public class PlatformController {

    private final IPlatformService platformService;

    @Autowired
    public PlatformController(IPlatformService platformService) {
        this.platformService = platformService;
    }

    @Operation(summary = "Get all platforms",
            description = "Returns a list of platforms. Can be filtered by name.")
    @ApiResponse(responseCode = "200",
            description = "List of platforms",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PlatformPublicDto.class)))})
    @Parameter(name = "name", description = "Name of the platform", example = "PlayStation")
    @GetMapping("/public")
    public ResponseEntity<List<PlatformPublicDto>> getAll(@RequestParam(required = false) String name) {
        return new ResponseEntity<>(platformService.getAll(name), HttpStatus.OK);
    }

    @Operation(summary = "Get platform by ID",
            description = "Returns a single platform by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Platform found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlatformPublicDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Platform not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @Parameter(name = "id", description = "Platform ID", example = "1", required = true)
    @GetMapping("/public/{id}")
    public ResponseEntity<PlatformPublicDto> getById(@PathVariable Long id) throws PlatformNotFoundException {
        return new ResponseEntity<>(platformService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Create a new platform",
            description = "Creates a new platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Platform created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlatformPublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Platform with the same name already exists or invalid data",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not enough permissions",
                    content = @Content)})
    @PostMapping("/admin")
    public ResponseEntity<PlatformPublicDto> create(@Valid @RequestBody PlatformCreateDto platformDto) throws PlatformAlreadyExistsException {
        return new ResponseEntity<>(platformService.create(platformDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update platform",
            description = "Updates an existing platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Platform updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlatformPublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Platform with the same name already exists or invalid data",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not enough permissions",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Platform not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @Parameter(name = "id", description = "Platform ID", example = "1", required = true)
    @PutMapping("/admin/{id}")
    public ResponseEntity<PlatformPublicDto> update(@PathVariable Long id, @Valid @RequestBody PlatformCreateDto platformDto) throws PlatformNotFoundException, PlatformAlreadyExistsException {
        return new ResponseEntity<>(platformService.update(id, platformDto), HttpStatus.OK);
    }

    @Operation(summary = "Delete platform",
            description = "Deletes an existing platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Platform deleted",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not enough permissions",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Platform not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @Parameter(name = "id", description = "Platform ID", example = "1", required = true)
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws PlatformNotFoundException {
        platformService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
