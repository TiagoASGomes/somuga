package org.somuga.controller;

import jakarta.validation.Valid;
import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.service.interfaces.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {

    private final IMovieService movieService;

    @Autowired
    public MovieController(IMovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<List<MoviePublicDto>> getAll(Pageable page) {
        return new ResponseEntity<>(movieService.getAll(page), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoviePublicDto> getById(@PathVariable Long id) {
        return new ResponseEntity<>(movieService.getById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MoviePublicDto> create(@Valid @RequestBody MovieCreateDto game) {
        return new ResponseEntity<>(movieService.create(game), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MoviePublicDto> update(@PathVariable Long id, @Valid @RequestBody MovieCreateDto game) {
        return new ResponseEntity<>(movieService.update(id, game), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movieService.delete(id);
        return ResponseEntity.ok().build();
    }
}
