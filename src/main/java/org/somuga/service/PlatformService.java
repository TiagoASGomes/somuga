package org.somuga.service;

import org.somuga.converter.PlatformConverter;
import org.somuga.dto.platform.PlatformCreateDto;
import org.somuga.dto.platform.PlatformPublicDto;
import org.somuga.entity.Platform;
import org.somuga.exception.platform.PlatformAlreadyExistsException;
import org.somuga.exception.platform.PlatformNotFoundException;
import org.somuga.repository.PlatformRepository;
import org.somuga.service.interfaces.IPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.somuga.message.Messages.*;

@Service
public class PlatformService implements IPlatformService {

    private final PlatformRepository platformRepo;

    @Autowired
    public PlatformService(PlatformRepository platformRepo) {
        this.platformRepo = platformRepo;
    }

    @Override
    public List<PlatformPublicDto> getAll(Pageable page) {
        List<Platform> platforms = platformRepo.findAll(page).toList();
        return PlatformConverter.fromEntityListToPublicDtoList(platformRepo.findAll(page).toList());
    }

    @Override
    public PlatformPublicDto getById(Long id) throws PlatformNotFoundException {
        return PlatformConverter.fromEntityToPublicDto(findById(id));
    }

    @Override
    public List<PlatformPublicDto> searchByName(String name, Pageable page) {
        return PlatformConverter.fromEntityListToPublicDtoList(platformRepo.findByPlatformNameContaining(name.toLowerCase(), page).toList());
    }

    @Override
    public PlatformPublicDto create(PlatformCreateDto platformDto) throws PlatformAlreadyExistsException {
        checkDuplicatePlatform(platformDto.platformName());
        Platform platform = new Platform(platformDto.platformName().toLowerCase());
        return PlatformConverter.fromEntityToPublicDto(platformRepo.save(platform));
    }

    @Override
    public Platform findByPlatformName(String platformName) throws PlatformNotFoundException {
        return platformRepo.findByPlatformName(platformName.toLowerCase()).orElseThrow(() -> new PlatformNotFoundException(PLATFORM_NOT_FOUND_NAME + platformName));
    }

    private void checkDuplicatePlatform(String platformName) throws PlatformAlreadyExistsException {
        try {
            findByPlatformName(platformName);
            throw new PlatformAlreadyExistsException(PLATFORM_ALREADY_EXISTS + platformName);
        } catch (PlatformNotFoundException ignored) {
        }
    }

    private Platform findById(Long id) throws PlatformNotFoundException {
        return platformRepo.findById(id).orElseThrow(() -> new PlatformNotFoundException(PLATFORM_NOT_FOUND + id));
    }
}
