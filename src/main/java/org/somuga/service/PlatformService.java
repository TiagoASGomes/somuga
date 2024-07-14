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

import static org.somuga.util.message.Messages.*;

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
        return PlatformConverter.fromEntityListToPublicDtoList(platformRepo.findByPlatformNameContainingIgnoreCase(name, page).toList());
    }

    @Override
    public PlatformPublicDto create(PlatformCreateDto platformDto) throws PlatformAlreadyExistsException {
        if (checkDuplicatePlatform(platformDto.platformName())) {
            throw new PlatformAlreadyExistsException(PLATFORM_ALREADY_EXISTS + platformDto.platformName());
        }
        Platform platform = new Platform(platformDto.platformName());
        return PlatformConverter.fromEntityToPublicDto(platformRepo.save(platform));
    }

    @Override
    public Platform findByPlatformName(String platformName) throws PlatformNotFoundException {
        return platformRepo.findByPlatformNameIgnoreCase(platformName).orElseThrow(() -> new PlatformNotFoundException(PLATFORM_NOT_FOUND_NAME + platformName));
    }

    private boolean checkDuplicatePlatform(String platformName) {
        try {
            findByPlatformName(platformName);
            return true;
        } catch (PlatformNotFoundException ignored) {
            return false;
        }
    }

    private Platform findById(Long id) throws PlatformNotFoundException {
        return platformRepo.findById(id).orElseThrow(() -> new PlatformNotFoundException(PLATFORM_NOT_FOUND + id));
    }
}
