package org.somuga.controller;

import jakarta.validation.Valid;
import org.somuga.dto.user.UserCreateDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.dto.user.UserUpdateNameDto;
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
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService service) {
        this.userService = service;
    }

    @GetMapping
    public ResponseEntity<List<UserPublicDto>> getAll(Pageable page) {
        return new ResponseEntity<>(userService.getAll(page), HttpStatus.OK);
    }

    @GetMapping("/fullName/{fullName}")
    public ResponseEntity<List<UserPublicDto>> getAllByName(Pageable page, @PathVariable String name) {
        return new ResponseEntity<>(userService.getAllByName(page, name), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserPublicDto> getById(@PathVariable Long id) throws UserNotFoundException {
        return new ResponseEntity<>(userService.getById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserPublicDto> create(@Valid @RequestBody UserCreateDto user) throws DuplicateFieldException {
        return new ResponseEntity<>(userService.create(user), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserPublicDto> updateUserName(@PathVariable Long id, @Valid @RequestBody UserUpdateNameDto user) throws UserNotFoundException, DuplicateFieldException {
        return new ResponseEntity<>(userService.updateUserName(id, user), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws UserNotFoundException {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
