package org.somuga.converter;

import org.somuga.dto.developer.DeveloperCreateDto;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.entity.Developer;

import java.util.List;

public class DeveloperConverter {

    private DeveloperConverter() {
    }

    public static DeveloperPublicDto fromEntityToPublicDto(Developer developer) {
        if (developer == null) return null;
        if (developer.getSocials() == null) developer.setSocials(List.of());

        return new DeveloperPublicDto(
                developer.getId(),
                developer.getDeveloperName(),
                developer.getSocials()
        );
    }

    public static List<DeveloperPublicDto> fromEntityListToPublicDtoList(List<Developer> developers) {
        if (developers == null) return null;
        return developers.stream()
                .map(DeveloperConverter::fromEntityToPublicDto)
                .toList();
    }

    public static Developer fromCreateDtoToEntity(DeveloperCreateDto developerDto) {
        if (developerDto == null) return null;
        return Developer.builder()
                .developerName(developerDto.developerName())
                .socials(developerDto.socials())
                .build();
    }
}
