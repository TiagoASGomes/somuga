package org.somuga.controller;

import jakarta.validation.Valid;
import org.somuga.dto.platform.PlatformCreateDto;
import org.somuga.dto.platform.PlatformPublicDto;
import org.somuga.exception.platform.PlatformAlreadyExistsException;
import org.somuga.exception.platform.PlatformNotFoundException;
import org.somuga.service.interfaces.IPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/game/platform")
public class PlatformController {

    private final IPlatformService platformService;

    @Autowired
    public PlatformController(IPlatformService platformService) {
        this.platformService = platformService;
    }

    @GetMapping("/public")
    public ResponseEntity<List<PlatformPublicDto>> getAll(@RequestParam(required = false) String name) {
        return new ResponseEntity<>(platformService.getAll(name), HttpStatus.OK);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<PlatformPublicDto> getById(@PathVariable Long id) throws PlatformNotFoundException {
        return new ResponseEntity<>(platformService.getById(id), HttpStatus.OK);
    }

    @PostMapping("/private")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PlatformPublicDto> create(@Valid @RequestBody PlatformCreateDto platformDto) throws PlatformAlreadyExistsException {
        return new ResponseEntity<>(platformService.create(platformDto), HttpStatus.CREATED);
    }

    @PutMapping("/private/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PlatformPublicDto> update(@PathVariable Long id, @Valid @RequestBody PlatformCreateDto platformDto) throws PlatformNotFoundException, PlatformAlreadyExistsException {
        return new ResponseEntity<>(platformService.update(id, platformDto), HttpStatus.OK);
    }

    @DeleteMapping("/private/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws PlatformNotFoundException {
        platformService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
