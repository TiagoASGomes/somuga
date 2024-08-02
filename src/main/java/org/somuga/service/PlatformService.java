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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.somuga.util.message.Messages.PLATFORM_ALREADY_EXISTS;
import static org.somuga.util.message.Messages.PLATFORM_NOT_FOUND;

@Service
public class PlatformService implements IPlatformService {

    private final PlatformRepository platformRepo;

    @Autowired
    public PlatformService(PlatformRepository platformRepo) {
        this.platformRepo = platformRepo;
    }

    @Override
    public List<PlatformPublicDto> getAll(String name) {
        if (name != null) {
            return PlatformConverter.fromEntityListToPublicDtoList(platformRepo.findByPlatformNameContainingIgnoreCase(name));
        }
        return PlatformConverter.fromEntityListToPublicDtoList(platformRepo.findAll());
    }

    @Override
    public PlatformPublicDto getById(Long id) throws PlatformNotFoundException {
        return PlatformConverter.fromEntityToPublicDto(findById(id));
    }

    @Override
    public PlatformPublicDto create(PlatformCreateDto platformDto) throws PlatformAlreadyExistsException {
        Optional<Platform> duplicatePlatform = findByPlatformName(platformDto.platformName());
        if (duplicatePlatform.isPresent()) {
            throw new PlatformAlreadyExistsException(PLATFORM_ALREADY_EXISTS + platformDto.platformName());
        }
        Platform platform = PlatformConverter.fromCreateDtoToEntity(platformDto);
        return PlatformConverter.fromEntityToPublicDto(platformRepo.save(platform));
    }


    @Override
    public PlatformPublicDto update(Long id, PlatformCreateDto platformDto) throws PlatformAlreadyExistsException, PlatformNotFoundException {
        Optional<Platform> duplicatePlatform = findByPlatformName(platformDto.platformName());
        if (duplicatePlatform.isPresent() && !duplicatePlatform.get().getId().equals(id)) {
            throw new PlatformAlreadyExistsException(PLATFORM_ALREADY_EXISTS + platformDto.platformName());
        }
        Platform platform = findById(id);
        platform.setPlatformName(platformDto.platformName());
        return PlatformConverter.fromEntityToPublicDto(platformRepo.save(platform));
    }

    @Override
    public void delete(Long id) throws PlatformNotFoundException {
        findById(id);
        platformRepo.deleteById(id);
    }

    @Override
    public Platform findById(Long id) throws PlatformNotFoundException {
        return platformRepo.findById(id).orElseThrow(() -> new PlatformNotFoundException(PLATFORM_NOT_FOUND + id));
    }

    private Optional<Platform> findByPlatformName(String platformName) {
        return platformRepo.findByPlatformNameIgnoreCase(platformName);
    }
}
