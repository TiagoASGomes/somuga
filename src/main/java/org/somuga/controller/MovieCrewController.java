package org.somuga.controller;

import jakarta.validation.Valid;
import org.somuga.dto.movie_crew.MovieCrewCreateDto;
import org.somuga.dto.movie_crew.MovieCrewPublicDto;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.movie_crew.MovieCrewNotFoundException;
import org.somuga.service.interfaces.IMovieCrewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movie_crew")
@CrossOrigin(origins = "*")
public class MovieCrewController {

    private final IMovieCrewService movieCrewService;

    @Autowired
    public MovieCrewController(IMovieCrewService movieCrewService) {
        this.movieCrewService = movieCrewService;
    }


    @GetMapping("/public")
    public ResponseEntity<List<MovieCrewPublicDto>> getAll(Pageable page) {
        return new ResponseEntity<>(movieCrewService.getAll(page), HttpStatus.OK);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<MovieCrewPublicDto> getById(@PathVariable Long id) throws MovieCrewNotFoundException {
        return new ResponseEntity<>(movieCrewService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/public/search/{name}")
    public ResponseEntity<List<MovieCrewPublicDto>> getByName(@PathVariable String name, Pageable page) {
        return new ResponseEntity<>(movieCrewService.getByName(name, page), HttpStatus.OK);
    }

    @PostMapping("/private")
    public ResponseEntity<MovieCrewPublicDto> create(@Valid @RequestBody MovieCrewCreateDto movieCrew) {
        return new ResponseEntity<>(movieCrewService.create(movieCrew), HttpStatus.CREATED);
    }

    @PutMapping("/private/{id}")
    public ResponseEntity<MovieCrewPublicDto> update(@PathVariable Long id, @Valid @RequestBody MovieCrewCreateDto movieCrew) throws MovieCrewNotFoundException, InvalidPermissionException {
        return new ResponseEntity<>(movieCrewService.update(id, movieCrew), HttpStatus.OK);
    }

    @DeleteMapping("/private/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws MovieCrewNotFoundException, InvalidPermissionException {
        movieCrewService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
