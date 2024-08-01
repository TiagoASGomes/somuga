package org.somuga.service;

import org.somuga.converter.GameConverter;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GameLikePublicDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.entity.Developer;
import org.somuga.entity.Game;
import org.somuga.entity.GameGenre;
import org.somuga.entity.Platform;
import org.somuga.enums.MediaType;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.game.GameNotFoundException;
import org.somuga.exception.game_genre.GenreNotFoundException;
import org.somuga.exception.platform.PlatformNotFoundException;
import org.somuga.filters.SearchCriteria;
import org.somuga.filters.game.GameSpecificationBuilder;
import org.somuga.repository.GameRepository;
import org.somuga.service.interfaces.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.somuga.util.message.Messages.*;

@Service
public class GameService implements IGameService {

    private final GameRepository gameRepo;
    private final DeveloperService developerService;
    private final PlatformService platformService;
    private final GameGenreService genreService;

    @Autowired
    public GameService(GameRepository gameRepo, DeveloperService developerService, PlatformService platformService, GameGenreService genreService) {
        this.gameRepo = gameRepo;
        this.developerService = developerService;
        this.platformService = platformService;
        this.genreService = genreService;
    }


    @Override
    public List<GamePublicDto> getAll(Pageable page, String title, List<String> platform, List<String> genre, String developer) {
        List<SearchCriteria> params = createSearchCriteria(title, platform, genre, developer);
        GameSpecificationBuilder builder = new GameSpecificationBuilder();
        params.forEach(builder::with);

        List<Game> games = gameRepo.findAll(builder.build(), page).toList();

        return GameConverter.fromEntityListToPublicDtoList(games);
    }

    private List<SearchCriteria> createSearchCriteria(String name, List<String> platform, List<String> genre, String developer) {
        List<SearchCriteria> params = new ArrayList<>();
        if (name != null) {
            params.add(new SearchCriteria("title", name));
        }
        if (platform != null) {
            for (String platformName : platform) {
                params.add(new SearchCriteria("platform", platformName));
            }
        }
        if (genre != null) {
            for (String genreName : genre) {
                params.add(new SearchCriteria("genre", genreName));
            }
        }
        if (developer != null) {
            params.add(new SearchCriteria("developer", developer));
        }
        return params;
    }

    @Override
    public GameLikePublicDto getById(Long id) throws GameNotFoundException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        Game game = findById(id);
        if (user.equals("anonymousUser")) {
            return GameConverter.fromEntityToPublicLikeDto(game, false);
        }
        boolean isLiked = game.getLikes()
                .stream()
                .anyMatch(like -> like.getUser().getId().equals(user));
        return GameConverter.fromEntityToPublicLikeDto(game, isLiked);
    }

    @Override
    public GamePublicDto create(GameCreateDto gameDto) throws GenreNotFoundException, DeveloperNotFoundException, PlatformNotFoundException {
        Game game = GameConverter.fromCreateDtoToEntity(gameDto);
        Developer developer = developerService.findById(gameDto.developerId());

        List<GameGenre> genres = new ArrayList<>();
        List<Platform> platforms = new ArrayList<>();

        for (Long platformId : gameDto.platformsIds()) {
            platforms.add(platformService.findById(platformId));
        }
        for (Long genreId : gameDto.genreIds()) {
            genres.add(genreService.findById(genreId));
        }
        genres.forEach(game::addGenre);
        platforms.forEach(game::addPlatform);
        game.setDeveloper(developer);
        game.setMediaType(MediaType.GAME);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        game.setMediaCreatorId(auth.getName());
        return GameConverter.fromEntityToPublicDto(gameRepo.save(game));
    }

    @Override
    public GamePublicDto update(Long id, GameCreateDto gameDto) throws GameNotFoundException, DeveloperNotFoundException, PlatformNotFoundException, GenreNotFoundException, InvalidPermissionException {
        Game game = findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!game.getMediaCreatorId().equals(auth.getName())) {
            throw new InvalidPermissionException(UNAUTHORIZED_UPDATE);
        }
        Developer developer = developerService.findById(gameDto.developerId());
        List<GameGenre> genres = new ArrayList<>();
        List<Platform> platforms = new ArrayList<>();
        for (Long platformId : gameDto.platformsIds()) {
            platforms.add(platformService.findById(platformId));
        }
        for (Long genreId : gameDto.genreIds()) {
            genres.add(genreService.findById(genreId));
        }
        removePlatformsAndGenres(game);
        game.setDeveloper(developer);
        genres.forEach(game::addGenre);
        platforms.forEach(game::addPlatform);
        game.setTitle(gameDto.title());
        game.setDescription(gameDto.description());
        game.setReleaseDate(gameDto.releaseDate());
        game.setPrice(gameDto.price());
        game.setImageUrl(gameDto.imageUrl());
        game.setMediaUrl(gameDto.mediaUrl());
        return GameConverter.fromEntityToPublicDto(gameRepo.save(game));
    }

    @Override
    public void delete(Long id) throws GameNotFoundException, InvalidPermissionException {
        Game game = findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!game.getMediaCreatorId().equals(auth.getName())) {
            throw new InvalidPermissionException(UNAUTHORIZED_DELETE);
        }
        removePlatformsAndGenres(game);
        gameRepo.delete(game);
    }

    private void removePlatformsAndGenres(Game game) {
        List<Platform> platforms = List.copyOf(game.getPlatforms());
        List<GameGenre> genres = List.copyOf(game.getGenres());
        for (Platform platform : platforms) {
            game.removePlatform(platform);
        }
        for (GameGenre genre : genres) {
            game.removeGenre(genre);
        }
    }

    @Override
    public Game findById(Long id) throws GameNotFoundException {
        return gameRepo.findById(id).orElseThrow(() -> new GameNotFoundException(GAME_NOT_FOUND + id));
    }

    @Override
    public void adminDelete(Long id) throws GameNotFoundException {
        Game game = findById(id);
        removePlatformsAndGenres(game);
        gameRepo.deleteById(id);
    }
}
