package org.somuga.controller;

import jakarta.validation.Valid;
import org.somuga.dto.developer.DeveloperCreateDto;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.user.DuplicateFieldException;
import org.somuga.service.interfaces.IDeveloperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/developer")
public class DeveloperController {

    private final IDeveloperService developerService;

    @Autowired
    public DeveloperController(IDeveloperService developerService) {
        this.developerService = developerService;
    }

    @GetMapping
    public ResponseEntity<List<DeveloperPublicDto>> getAll(Pageable page) {
        return new ResponseEntity<>(developerService.getAll(page), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeveloperPublicDto> getById(@PathVariable Long id) throws DeveloperNotFoundException {
        return new ResponseEntity<>(developerService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/search/{fullName}")
    public ResponseEntity<List<DeveloperPublicDto>> searchByName(@PathVariable String name, Pageable page) {
        return new ResponseEntity<>(developerService.searchByName(name, page), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DeveloperPublicDto> create(@Valid @RequestBody DeveloperCreateDto developerDto) throws DuplicateFieldException {
        return new ResponseEntity<>(developerService.create(developerDto), HttpStatus.CREATED);
    }

}
