package org.somuga.controller;

import jakarta.validation.Valid;
import org.somuga.dto.platform.PlatformCreateDto;
import org.somuga.dto.platform.PlatformPublicDto;
import org.somuga.exception.platform.PlatformAlreadyExistsException;
import org.somuga.exception.platform.PlatformNotFoundException;
import org.somuga.service.interfaces.IPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/platform")
public class PlatformController {

    private final IPlatformService platformService;

    @Autowired
    public PlatformController(IPlatformService platformService) {
        this.platformService = platformService;
    }

    @GetMapping("/public")
    public ResponseEntity<List<PlatformPublicDto>> getAll(Pageable page) {
        return new ResponseEntity<>(platformService.getAll(page), HttpStatus.OK);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<PlatformPublicDto> getById(@PathVariable Long id) throws PlatformNotFoundException {
        return new ResponseEntity<>(platformService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/public/search/{name}")
    public ResponseEntity<List<PlatformPublicDto>> searchByName(@PathVariable String name, Pageable page) {
        return new ResponseEntity<>(platformService.searchByName(name, page), HttpStatus.OK);
    }

    @PostMapping("/private")
    public ResponseEntity<PlatformPublicDto> create(@Valid @RequestBody PlatformCreateDto platformDto) throws PlatformAlreadyExistsException {
//         TODO change to admin
        return new ResponseEntity<>(platformService.create(platformDto), HttpStatus.CREATED);
    }

}
