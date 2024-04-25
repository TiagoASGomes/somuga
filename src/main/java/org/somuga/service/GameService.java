package org.somuga.service;

import org.somuga.converter.GameConverter;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.entity.Developer;
import org.somuga.entity.Game;
import org.somuga.entity.GameGenre;
import org.somuga.entity.Platform;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.game.GameNotFoundException;
import org.somuga.exception.game_genre.GenreNotFoundException;
import org.somuga.exception.platform.PlatformNotFoundException;
import org.somuga.repository.GameRepository;
import org.somuga.service.interfaces.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.somuga.message.Messages.GAME_NOT_FOUND;

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
    public List<GamePublicDto> getAll(Pageable page) {
        return GameConverter.fromEntityListToPublicDtoList(gameRepo.findAll(page).toList());
    }

    @Override
    public List<GamePublicDto> getByPlatform(String platformName, Pageable page) {
        try {
            platformService.findByPlatformName(platformName);
        } catch (Exception e) {
            return List.of();
        }
        return GameConverter.fromEntityListToPublicDtoList(gameRepo.findByPlatform(platformName.toLowerCase(), page).toList());
    }

    @Override
    public List<GamePublicDto> getByGenre(String genreName, Pageable page) {
        try {
            genreService.findByGenre(genreName);
        } catch (Exception e) {
            return List.of();
        }
        return GameConverter.fromEntityListToPublicDtoList(gameRepo.findByGenre(genreName.toLowerCase(), page).toList());
    }

    @Override
    public List<GamePublicDto> getByDeveloper(String developerName, Pageable page) {
        try {
            developerService.findByDeveloperName(developerName);
        } catch (Exception e) {
            return List.of();
        }
        return GameConverter.fromEntityListToPublicDtoList(gameRepo.findByDeveloper(developerName.toLowerCase(), page).toList());
    }

    @Override
    public List<GamePublicDto> searchByName(String name, Pageable page) {
        return GameConverter.fromEntityListToPublicDtoList(gameRepo.findByTitleContainingIgnoreCase(name, page).toList());
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
        return GameConverter.fromEntityToPublicDto(gameRepo.save(game));
    }

    @Override
    public GamePublicDto update(Long id, GameCreateDto gameDto) throws GameNotFoundException, DeveloperNotFoundException, PlatformNotFoundException, GenreNotFoundException {
        Game game = findById(id);
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
        game.setTitle(gameDto.title());
        game.setDescription(gameDto.description());
        game.setReleaseDate(gameDto.releaseDate());
        game.setPrice(gameDto.price());
        return GameConverter.fromEntityToPublicDto(gameRepo.save(game));
    }

    @Override
    public void delete(Long id) throws GameNotFoundException {
        findById(id);
        gameRepo.deleteById(id);
    }

    @Override
    public Game findById(Long id) throws GameNotFoundException {
        return gameRepo.findById(id).orElseThrow(() -> new GameNotFoundException(GAME_NOT_FOUND));
    }
}
