package org.somuga.controller;

import jakarta.validation.Valid;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.game.GameNotFoundException;
import org.somuga.exception.game_genre.GenreNotFoundException;
import org.somuga.exception.platform.PlatformNotFoundException;
import org.somuga.service.interfaces.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/game")
public class GameController {

    private final IGameService gameService;

    @Autowired
    public GameController(IGameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/private")
    public ResponseEntity<List<GamePublicDto>> getAll(Pageable page) {
        return new ResponseEntity<>(gameService.getAll(page), HttpStatus.OK);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<GamePublicDto> getById(@PathVariable Long id) throws GameNotFoundException {
        return new ResponseEntity<>(gameService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/public/platform/{platformName}")
    public ResponseEntity<List<GamePublicDto>> getByPlatform(@PathVariable String platformName, Pageable page) {
        return new ResponseEntity<>(gameService.getByPlatform(platformName, page), HttpStatus.OK);
    }

    @GetMapping("/public/genre/{genreName}")
    public ResponseEntity<List<GamePublicDto>> getByGenre(@PathVariable String genreName, Pageable page) {
        return new ResponseEntity<>(gameService.getByGenre(genreName, page), HttpStatus.OK);
    }

    @GetMapping("/public/developer/{developerName}")
    public ResponseEntity<List<GamePublicDto>> getByDeveloper(@PathVariable String developerName, Pageable page) {
        return new ResponseEntity<>(gameService.getByDeveloper(developerName, page), HttpStatus.OK);
    }

    @GetMapping("/public/search/{name}")
    public ResponseEntity<List<GamePublicDto>> searchByName(@PathVariable String name, Pageable page) {
        return new ResponseEntity<>(gameService.searchByName(name, page), HttpStatus.OK);
    }

    @PostMapping("/private")
    public ResponseEntity<GamePublicDto> create(@Valid @RequestBody GameCreateDto game) throws DeveloperNotFoundException, GenreNotFoundException, PlatformNotFoundException {
        return new ResponseEntity<>(gameService.create(game), HttpStatus.CREATED);
    }

    @PutMapping("/private/{id}")
    public ResponseEntity<GamePublicDto> update(@PathVariable Long id, @Valid @RequestBody GameCreateDto game) throws GameNotFoundException, DeveloperNotFoundException, PlatformNotFoundException, GenreNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<>(gameService.update(id, game), HttpStatus.OK);
    }

    @DeleteMapping("/private/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws GameNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        gameService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> adminDelete(@PathVariable Long id) throws GameNotFoundException {
        gameService.adminDelete(id);
        return ResponseEntity.noContent().build();
    }

}
