package org.somuga.converter;

import org.somuga.dto.developer.DeveloperCreateDto;
import org.somuga.dto.developer.DeveloperListDto;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.entity.Developer;

import java.util.ArrayList;
import java.util.List;

public class DeveloperConverter {

    private DeveloperConverter() {
    }

    public static DeveloperPublicDto fromEntityToPublicDto(Developer developer) {
        if (developer == null) return null;

        return new DeveloperPublicDto(
                developer.getId(),
                developer.getDeveloperName()
        );
    }

    public static DeveloperListDto fromEntityListToPublicDtoList(List<Developer> developers, Long count) {
        if (developers == null) return new DeveloperListDto(new ArrayList<>(0), 0L);
        List<DeveloperPublicDto> developerDtos = developers.stream()
                .map(DeveloperConverter::fromEntityToPublicDto)
                .toList();
        return new DeveloperListDto(developerDtos, count);
    }

    public static Developer fromCreateDtoToEntity(DeveloperCreateDto developerDto) {
        if (developerDto == null) return null;
        return Developer.builder()
                .developerName(developerDto.developerName())
                .build();
    }
}
