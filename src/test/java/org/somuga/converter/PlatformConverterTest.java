package org.somuga.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.somuga.dto.platform.PlatformCreateDto;
import org.somuga.dto.platform.PlatformPublicDto;
import org.somuga.entity.Platform;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
class PlatformConverterTest {

    @Test
    @DisplayName("Test fromEntityToPublicDto should convert entity to public dto")
    void fromEntityToPublicDto() {
        Platform platform = Platform.builder()
                .id(1L)
                .platformName("Test Platform")
                .build();

        PlatformPublicDto platformPublicDto = PlatformConverter.fromEntityToPublicDto(platform);

        assertEquals(platform.getId(), platformPublicDto.id());
        assertEquals(platform.getPlatformName(), platformPublicDto.platformName());
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto should return null when entity is null")
    void fromEntityToPublicDtoShouldReturnNull() {
        assertNull(PlatformConverter.fromEntityToPublicDto(null));
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should convert entity list to public dto list")
    void fromEntityListToPublicDtoList() {
        List<Platform> platforms = List.of(
                Platform.builder()
                        .id(1L)
                        .platformName("Test Platform 1")
                        .build(),
                Platform.builder()
                        .id(2L)
                        .platformName("Test Platform 2")
                        .build()
        );

        List<PlatformPublicDto> platformPublicDtos = PlatformConverter.fromEntityListToPublicDtoList(platforms);

        assertEquals(platforms.size(), platformPublicDtos.size());
        for (int i = 0; i < platforms.size(); i++) {
            assertEquals(platforms.get(i).getId(), platformPublicDtos.get(i).id());
            assertEquals(platforms.get(i).getPlatformName(), platformPublicDtos.get(i).platformName());
        }
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when entity list is null")
    void fromEntityListToPublicDtoListShouldReturnEmptyList() {
        List<PlatformPublicDto> platformPublicDtos = PlatformConverter.fromEntityListToPublicDtoList(null);

        assertEquals(0, platformPublicDtos.size());
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when entity list is empty")
    void fromEntityListToPublicDtoListShouldReturnEmptyListWhenEntityListIsEmpty() {
        List<PlatformPublicDto> platformPublicDtos = PlatformConverter.fromEntityListToPublicDtoList(List.of());

        assertEquals(0, platformPublicDtos.size());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should convert create dto to entity")
    void fromCreateDtoToEntity() {
        PlatformCreateDto platformCreateDto = new PlatformCreateDto("Test Platform");

        Platform platform = PlatformConverter.fromCreateDtoToEntity(platformCreateDto);

        assertNull(platform.getId());
        assertEquals(platformCreateDto.platformName(), platform.getPlatformName());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should return null when create dto is null")
    void fromCreateDtoToEntityShouldReturnNull() {
        assertNull(PlatformConverter.fromCreateDtoToEntity(null));
    }
}