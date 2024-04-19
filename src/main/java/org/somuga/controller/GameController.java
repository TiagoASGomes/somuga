package org.somuga.controller;

import jakarta.validation.Valid;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.service.interfaces.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<GamePublicDto>> getAll(Pageable page) {
        return new ResponseEntity<>(gameService.getAll(page), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GamePublicDto> getById(@PathVariable Long id) {
        return new ResponseEntity<>(gameService.getById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<GamePublicDto> create(@Valid @RequestBody GameCreateDto game) {
        return new ResponseEntity<>(gameService.create(game), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GamePublicDto> update(@PathVariable Long id, @Valid @RequestBody GameCreateDto game) {
        return new ResponseEntity<>(gameService.update(id, game), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gameService.delete(id);
        return ResponseEntity.ok().build();
    }


}
