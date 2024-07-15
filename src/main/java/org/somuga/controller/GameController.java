package org.somuga.controller;

import jakarta.validation.Valid;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.game.GameNotFoundException;
import org.somuga.exception.game_genre.GenreNotFoundException;
import org.somuga.exception.platform.PlatformNotFoundException;
import org.somuga.service.interfaces.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/public")
    public ResponseEntity<List<GamePublicDto>> getAll(Pageable page,
                                                      @RequestParam(required = false) String title,
                                                      @RequestParam(required = false) List<String> platform,
                                                      @RequestParam(required = false) List<String> genre,
                                                      @RequestParam(required = false) String developer) {
        return new ResponseEntity<>(gameService.getAll(page, title, platform, genre, developer), HttpStatus.OK);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<GamePublicDto> getById(@PathVariable Long id) throws GameNotFoundException {
        return new ResponseEntity<>(gameService.getById(id), HttpStatus.OK);
    }

    @PostMapping("/private")
    public ResponseEntity<GamePublicDto> create(@Valid @RequestBody GameCreateDto game) throws DeveloperNotFoundException, GenreNotFoundException, PlatformNotFoundException {
        return new ResponseEntity<>(gameService.create(game), HttpStatus.CREATED);
    }

    @PutMapping("/private/{id}")
    public ResponseEntity<GamePublicDto> update(@PathVariable Long id, @Valid @RequestBody GameCreateDto game) throws GameNotFoundException, DeveloperNotFoundException, PlatformNotFoundException, GenreNotFoundException, InvalidPermissionException {
        return new ResponseEntity<>(gameService.update(id, game), HttpStatus.OK);
    }

    @DeleteMapping("/private/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws GameNotFoundException, InvalidPermissionException {
        gameService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> adminDelete(@PathVariable Long id) throws GameNotFoundException {
        gameService.adminDelete(id);
        return ResponseEntity.noContent().build();
    }

}
