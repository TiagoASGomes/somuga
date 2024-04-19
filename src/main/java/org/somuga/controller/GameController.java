package org.somuga.controller;

import jakarta.validation.Valid;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.entity.Game;
import org.somuga.service.interfaces.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/game")
public class GameController {

    private final IGameService gameService;

    @Autowired
    public GameController(IGameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getById(@PathVariable Long id) {
        return new ResponseEntity<>(gameService.getById(id), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Game> create(@Valid @RequestBody GameCreateDto game) {
        return new ResponseEntity<>(gameService.create(game), HttpStatus.CREATED);
    }

}
