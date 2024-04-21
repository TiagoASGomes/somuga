package org.somuga.converter;

import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.entity.Developer;

import java.util.List;

public class DeveloperConverter {

    public static DeveloperPublicDto fromEntityToPublicDto(Developer developer) {
        return new DeveloperPublicDto(
                developer.getId(),
                developer.getDeveloperName()
        );
    }

    public static List<DeveloperPublicDto> fromEntityListToPublicDtoList(List<Developer> developers) {
        return developers.stream()
                .map(DeveloperConverter::fromEntityToPublicDto)
                .toList();
    }
}
