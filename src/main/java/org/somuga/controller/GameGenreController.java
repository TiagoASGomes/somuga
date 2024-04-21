package org.somuga.controller;

import jakarta.validation.Valid;
import org.somuga.dto.game_genre.GameGenreCreateDto;
import org.somuga.dto.game_genre.GameGenrePublicDto;
import org.somuga.exception.game_genre.GenreAlreadyExistsException;
import org.somuga.exception.game_genre.GenreNotFoundException;
import org.somuga.service.interfaces.IGameGenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/game_genre")
public class GameGenreController {

    private final IGameGenreService gameGenreService;

    @Autowired
    public GameGenreController(IGameGenreService gameGenreService) {
        this.gameGenreService = gameGenreService;
    }

    @GetMapping
    public ResponseEntity<List<GameGenrePublicDto>> getAll(Pageable page) {
        return new ResponseEntity<>(gameGenreService.getAll(page), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameGenrePublicDto> getById(@PathVariable Long id) throws GenreNotFoundException {
        return new ResponseEntity<>(gameGenreService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<GameGenrePublicDto>> searchByName(@PathVariable String name, Pageable page) {
        return new ResponseEntity<>(gameGenreService.searchByName(name, page), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<GameGenrePublicDto> create(@Valid @RequestBody GameGenreCreateDto genreDto) throws GenreAlreadyExistsException {
        return new ResponseEntity<>(gameGenreService.create(genreDto), HttpStatus.CREATED);
    }
}
