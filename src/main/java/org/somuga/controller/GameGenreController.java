package org.somuga.controller;

import jakarta.validation.Valid;
import org.somuga.dto.game_genre.GameGenreCreateDto;
import org.somuga.dto.game_genre.GameGenrePublicDto;
import org.somuga.exception.game_genre.GenreAlreadyExistsException;
import org.somuga.exception.game_genre.GenreNotFoundException;
import org.somuga.service.interfaces.IGameGenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/game/genre")
public class GameGenreController {

    private final IGameGenreService gameGenreService;

    @Autowired
    public GameGenreController(IGameGenreService gameGenreService) {
        this.gameGenreService = gameGenreService;
    }

    @GetMapping("/public")
    public ResponseEntity<List<GameGenrePublicDto>> getAll(@RequestParam(required = false) String name) {
        return new ResponseEntity<>(gameGenreService.getAll(name), HttpStatus.OK);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<GameGenrePublicDto> getById(@PathVariable Long id) throws GenreNotFoundException {
        return new ResponseEntity<>(gameGenreService.getById(id), HttpStatus.OK);
    }

    @PostMapping("/private")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GameGenrePublicDto> create(@Valid @RequestBody GameGenreCreateDto genreDto) throws GenreAlreadyExistsException {
        return new ResponseEntity<>(gameGenreService.create(genreDto), HttpStatus.CREATED);
    }

    @PutMapping("/private/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GameGenrePublicDto> update(@PathVariable Long id, @Valid @RequestBody GameGenreCreateDto genreDto) throws GenreNotFoundException, GenreAlreadyExistsException {
        return new ResponseEntity<>(gameGenreService.update(id, genreDto), HttpStatus.OK);
    }

    @DeleteMapping("/private/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws GenreNotFoundException {
        gameGenreService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
