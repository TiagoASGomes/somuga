package org.somuga.controller;

import jakarta.validation.Valid;
import org.somuga.dto.user.UserCreateDto;
import org.somuga.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService service){
        this.userService = service;
    }

    @GetMapping("/")
    public ResponseEntity<String> getAll(Pageable page){
        return new ResponseEntity<>("GET", HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<String> create(@Valid @RequestBody UserCreateDto user){
        return new ResponseEntity<>("POST", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id ,@Valid @RequestBody UserCreateDto user){
        return new ResponseEntity<>("PUT", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return new ResponseEntity<>("DELETE", HttpStatus.OK);
    }
}
