package org.somuga.service;

import org.somuga.converter.GameConverter;
import org.somuga.dto.game.GameCreateDto;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        List<SearchCriteria> params = createSearchCriteria(title, platform, genre, developer);
        GameSpecificationBuilder builder = new GameSpecificationBuilder();
        params.forEach(builder::with);

        List<Game> games = gameRepo.findAll(builder.build(), page).toList();
        List<GamePublicDto> gamePublicDtos = GameConverter.fromEntityListToPublicDtoList(games);

        if (!user.equals("anonymousUser")) {
            addUserLikes(gamePublicDtos, user);
        }
        return gamePublicDtos;
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

    private void addUserLikes(List<GamePublicDto> games, String name) {

    }

    @Override
    public GamePublicDto getById(Long id) throws GameNotFoundException {
        return GameConverter.fromEntityToPublicDto(findById(id));
    }

    @Override
    public GamePublicDto create(GameCreateDto gameDto) throws GenreNotFoundException, DeveloperNotFoundException, PlatformNotFoundException {
        Game game = GameConverter.fromCreateDtoToEntity(gameDto);
        Developer developer = developerService.findByDeveloperName(gameDto.developerName());
        Set<GameGenre> genres = new HashSet<>();
        Set<Platform> platforms = new HashSet<>();
        for (String platformName : gameDto.platformsNames()) {
            platforms.add(platformService.findByPlatformName(platformName));
        }
        for (String genreName : gameDto.genres()) {
            genres.add(genreService.findByGenre(genreName));
        }
        game.setDeveloper(developer);
        game.setGenres(genres);
        game.setPlatforms(platforms);
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
        Developer developer = developerService.findByDeveloperName(gameDto.developerName());
        Set<GameGenre> genres = new HashSet<>();
        Set<Platform> platforms = new HashSet<>();
        for (String platformName : gameDto.platformsNames()) {
            platforms.add(platformService.findByPlatformName(platformName));
        }
        for (String genreName : gameDto.genres()) {
            genres.add(genreService.findByGenre(genreName));
        }
        removePlatformAndGenre(game);
        game.setDeveloper(developer);
        game.setGenres(genres);
        game.setPlatforms(platforms);
        game.setTitle(gameDto.title());
        game.setDescription(gameDto.description());
        game.setReleaseDate(gameDto.releaseDate());
        game.setPrice(gameDto.price());
        return GameConverter.fromEntityToPublicDto(gameRepo.save(game));
    }

    @Override
    public void delete(Long id) throws GameNotFoundException, InvalidPermissionException {
        Game game = findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!game.getMediaCreatorId().equals(auth.getName())) {
            throw new InvalidPermissionException(UNAUTHORIZED_DELETE);
        }
        removePlatformAndGenre(game);
        gameRepo.delete(game);
    }

    private void removePlatformAndGenre(Game game) {
        Set<Platform> platforms = new HashSet<>(game.getPlatforms());
        Set<GameGenre> genres = new HashSet<>(game.getGenres());
        for (Platform platform : platforms) {
            platform.removeGame(game);
        }
        for (GameGenre genre : genres) {
            genre.removeGame(game);
        }
        gameRepo.save(game);
    }

    @Override
    public Game findById(Long id) throws GameNotFoundException {
        return gameRepo.findById(id).orElseThrow(() -> new GameNotFoundException(GAME_NOT_FOUND + id));
    }

    @Override
    public void adminDelete(Long id) throws GameNotFoundException {
        Game game = findById(id);
        removePlatformAndGenre(game);
        gameRepo.deleteById(id);
    }
}
