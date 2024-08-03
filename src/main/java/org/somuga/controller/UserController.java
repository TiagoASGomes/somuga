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
import org.somuga.dto.user.UserCreateDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.exception.user.DuplicateFieldException;
import org.somuga.exception.user.UserNotFoundException;
import org.somuga.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "User API")
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService service) {
        this.userService = service;
    }

    @Operation(summary = "Get all users",
            description = "Returns a list of users, optionally filtered by name")
    @ApiResponse(responseCode = "200",
            description = "Users found",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserPublicDto.class)))})
    @Parameter(name = "name", description = "User name", example = "John")
    @Parameter(name = "page", description = "Page number", example = "0")
    @Parameter(name = "size", description = "Page size", example = "10")
    @GetMapping("/public")
    public ResponseEntity<List<UserPublicDto>> getAll(Pageable page, @RequestParam(required = false) String name) {
        return new ResponseEntity<>(userService.getAll(page, name), HttpStatus.OK);
    }

    @Operation(summary = "Get a user by ID",
            description = "Returns a single user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserPublicDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @Parameter(name = "id", description = "User ID", example = "auth0|1234567890", required = true)
    @GetMapping("/public/{id}")
    public ResponseEntity<UserPublicDto> getById(@PathVariable String id) throws UserNotFoundException {
        return new ResponseEntity<>(userService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Create a user",
            description = "Creates a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "User created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserPublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid data or duplicate username",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content)})
    @PostMapping("/private")
    public ResponseEntity<UserPublicDto> create(@Valid @RequestBody UserCreateDto user) throws DuplicateFieldException {
        return new ResponseEntity<>(userService.create(user), HttpStatus.CREATED);
    }

    @Operation(summary = "Update user name",
            description = "Updates the user name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User name updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserPublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid data or duplicate username",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @PutMapping("/private")
    public ResponseEntity<UserPublicDto> updateUserName(@Valid @RequestBody UserCreateDto user) throws UserNotFoundException, DuplicateFieldException {
        return new ResponseEntity<>(userService.updateUserName(user), HttpStatus.OK);
    }

    @Operation(summary = "Delete user",
            description = "Deletes the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "User deleted",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @DeleteMapping("/private")
    public ResponseEntity<Void> delete() throws UserNotFoundException {
        userService.delete();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Admin delete user",
            description = "Deletes the user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "User deleted",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not enough permissions",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> adminDelete(@PathVariable String id) throws UserNotFoundException {
        userService.adminDelete(id);
        return ResponseEntity.noContent().build();
    }
}
