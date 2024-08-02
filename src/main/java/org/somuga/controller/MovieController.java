package org.somuga.controller;

import jakarta.validation.Valid;
import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MovieLikePublicDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.movie.InvalidCrewRoleException;
import org.somuga.exception.movie.MovieNotFoundException;
import org.somuga.exception.movie_crew.MovieCrewNotFoundException;
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

    @GetMapping("/public")
    public ResponseEntity<List<MoviePublicDto>> getAll(Pageable page,
                                                       @RequestParam(required = false) String title,
                                                       @RequestParam(required = false) List<Long> crewId) {

        return new ResponseEntity<>(movieService.getAll(page, title, crewId), HttpStatus.OK);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<MovieLikePublicDto> getById(@PathVariable Long id) throws MovieNotFoundException {
        return new ResponseEntity<>(movieService.getById(id), HttpStatus.OK);
    }

    @PostMapping("/private")
    public ResponseEntity<MoviePublicDto> create(@Valid @RequestBody MovieCreateDto movie) throws MovieCrewNotFoundException, InvalidCrewRoleException {
        return new ResponseEntity<>(movieService.create(movie), HttpStatus.CREATED);
    }

    @PutMapping("/private/{id}")
    public ResponseEntity<MoviePublicDto> update(@PathVariable Long id, @Valid @RequestBody MovieCreateDto movie) throws MovieNotFoundException, MovieCrewNotFoundException, InvalidCrewRoleException, InvalidPermissionException {
        return new ResponseEntity<>(movieService.update(id, movie), HttpStatus.OK);
    }

    @DeleteMapping("/private/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws MovieNotFoundException, InvalidPermissionException {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> adminDelete(@PathVariable Long id) throws MovieNotFoundException {
        movieService.adminDelete(id);
        return ResponseEntity.noContent().build();
    }
}
