package org.somuga.converter;

import org.somuga.dto.platform.PlatformCreateDto;
import org.somuga.dto.platform.PlatformPublicDto;
import org.somuga.entity.Platform;

import java.util.List;

public class PlatformConverter {

    public static PlatformPublicDto fromEntityToPublicDto(Platform platform) {
        if (platform == null) return null;
        return new PlatformPublicDto(
                platform.getId(),
                platform.getPlatformName()
        );
    }

    public static List<PlatformPublicDto> fromEntityListToPublicDtoList(List<Platform> platforms) {
        return platforms.stream()
                .map(PlatformConverter::fromEntityToPublicDto)
                .toList();
    }

    public static Platform fromCreateDtoToEntity(PlatformCreateDto platformDto) {
        return Platform.builder()
                .platformName(platformDto.platformName())
                .build();
    }
}
