package org.somuga.service;

import org.somuga.entity.Game;
import org.somuga.entity.Media;
import org.somuga.entity.Movie;
import org.somuga.enums.MediaType;
import org.somuga.exception.media.MediaNotFoundException;
import org.somuga.service.interfaces.IGameService;
import org.somuga.service.interfaces.IMediaService;
import org.somuga.service.interfaces.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.somuga.util.message.Messages.MEDIA_NOT_FOUND;

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
    public Media findById(Long id) throws MediaNotFoundException {
        try {
            return movieService.findById(id);
        } catch (Exception ignored) {
        }
        try {
            return gameService.findById(id);
        } catch (Exception ignored) {
        }
        throw new MediaNotFoundException(MEDIA_NOT_FOUND + id);
    }

    @Override
    public void updateAverageRating(Media media, Integer rating) {
        int newRating = media.getAverageRating() + (rating - media.getAverageRating()) / media.getReviews().size();
        if (media.getMediaType().equals(MediaType.GAME)) {
            gameService.updateAverageRating((Game) media, newRating);
        } else {
            movieService.updateAverageRating((Movie) media, newRating);
        }
    }
}
