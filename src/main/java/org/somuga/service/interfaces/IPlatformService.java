package org.somuga.service.interfaces;

import org.somuga.dto.platform.PlatformCreateDto;
import org.somuga.dto.platform.PlatformPublicDto;
import org.somuga.entity.Platform;
import org.somuga.exception.platform.PlatformAlreadyExistsException;
import org.somuga.exception.platform.PlatformNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPlatformService {
    List<PlatformPublicDto> getAll(Pageable page);

    PlatformPublicDto getById(Long id) throws PlatformNotFoundException;

    List<PlatformPublicDto> searchByName(String name, Pageable page);

    PlatformPublicDto create(PlatformCreateDto platformDto) throws PlatformAlreadyExistsException;

    Platform findByPlatformName(String platformName) throws PlatformNotFoundException;
}
