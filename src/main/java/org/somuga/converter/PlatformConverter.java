package org.somuga.converter;

import org.somuga.dto.platform.PlatformPublicDto;
import org.somuga.entity.Platform;

import java.util.List;

public class PlatformConverter {

    public static PlatformPublicDto fromEntityToPublicDto(Platform platform) {
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

}
