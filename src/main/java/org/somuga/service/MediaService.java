package org.somuga.service;

import org.somuga.entity.Media;
import org.somuga.service.interfaces.IGameService;
import org.somuga.service.interfaces.IMediaService;
import org.somuga.service.interfaces.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MediaService implements IMediaService {

    private final IGameService gameService;
    private final IMovieService movieService;

    @Autowired
    public MediaService(IGameService gameService, IMovieService movieService) {
        this.gameService = gameService;
        this.movieService = movieService;
    }

    @Override
    public Media findById(Long id) {
        try {
            return movieService.findById(id);
        } catch (Exception ignored) {
        }
        try {
            return gameService.findById(id);
        } catch (Exception ignored) {
        }
        throw new RuntimeException("");
    }
}
