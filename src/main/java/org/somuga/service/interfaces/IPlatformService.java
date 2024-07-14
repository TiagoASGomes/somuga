package org.somuga.service.interfaces;

import org.somuga.dto.platform.PlatformCreateDto;
import org.somuga.dto.platform.PlatformPublicDto;
import org.somuga.entity.Platform;
import org.somuga.exception.platform.PlatformAlreadyExistsException;
import org.somuga.exception.platform.PlatformNotFoundException;

import java.util.List;

public interface IPlatformService {
    List<PlatformPublicDto> getAll(String name);

    PlatformPublicDto getById(Long id) throws PlatformNotFoundException;

    PlatformPublicDto create(PlatformCreateDto platformDto) throws PlatformAlreadyExistsException;

    Platform findByPlatformName(String platformName) throws PlatformNotFoundException;
}
