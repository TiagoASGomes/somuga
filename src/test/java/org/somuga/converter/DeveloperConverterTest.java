package org.somuga.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.somuga.dto.developer.DeveloperCreateDto;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.entity.Developer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DeveloperConverterTest {

    @Test
    @DisplayName("fromEntityToPublicDto method should convert Developer entity to DeveloperPublicDto")
    void fromEntityToPublicDto() {
        Developer developer = Developer.builder()
                .id(1L)
                .developerName("Developer")
                .socials(List.of("social1", "social2"))
                .build();

        DeveloperPublicDto developerPublicDto = DeveloperConverter.fromEntityToPublicDto(developer);

        assertEquals(developer.getId(), developerPublicDto.id());
        assertEquals(developer.getDeveloperName(), developerPublicDto.developerName());
        assertEquals(developer.getSocials(), developerPublicDto.socials());
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto method with null entity should return null")
    void fromEntityToPublicDtoWithNullEntity() {
        Developer developer = null;
        DeveloperPublicDto developerPublicDto = DeveloperConverter.fromEntityToPublicDto(developer);
        assertNull(developerPublicDto);
    }

    @Test
    @DisplayName("fromEntityListToPublicDtoList method should convert List<Developer> to List<DeveloperPublicDto>")
    void fromEntityListToPublicDtoList() {
        Developer developer1 = Developer.builder()
                .id(1L)
                .developerName("Developer1")
                .socials(List.of("social1", "social2"))
                .build();

        Developer developer2 = Developer.builder()
                .id(2L)
                .developerName("Developer2")
                .socials(List.of("social3", "social4"))
                .build();

        List<Developer> developers = List.of(developer1, developer2);
        List<DeveloperPublicDto> developerPublicDtos = DeveloperConverter.fromEntityListToPublicDtoList(developers);

        assertEquals(developers.size(), developerPublicDtos.size());
        assertEquals(developers.get(0).getId(), developerPublicDtos.get(0).id());
        assertEquals(developers.get(0).getDeveloperName(), developerPublicDtos.get(0).developerName());
        assertEquals(developers.get(0).getSocials(), developerPublicDtos.get(0).socials());
        assertEquals(developers.get(1).getId(), developerPublicDtos.get(1).id());
        assertEquals(developers.get(1).getDeveloperName(), developerPublicDtos.get(1).developerName());
        assertEquals(developers.get(1).getSocials(), developerPublicDtos.get(1).socials());
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList method with empty list should return empty list")
    void fromEntityListToPublicDtoListWithEmptyList() {
        List<Developer> developers = List.of();
        List<DeveloperPublicDto> developerPublicDtos = DeveloperConverter.fromEntityListToPublicDtoList(developers);
        assertEquals(developers.size(), developerPublicDtos.size());
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList method with null list should return empty list")
    void fromEntityListToPublicDtoListWithNullList() {
        List<Developer> developers = null;
        List<DeveloperPublicDto> developerPublicDtos = DeveloperConverter.fromEntityListToPublicDtoList(developers);
        assertNull(developerPublicDtos);
    }

    @Test
    @DisplayName("fromCreateDtoToEntity method should convert DeveloperCreateDto to Developer entity")
    void fromCreateDtoToEntity() {
        DeveloperCreateDto developerDto = new DeveloperCreateDto("Developer", List.of("social1", "social2"));

        Developer developer = DeveloperConverter.fromCreateDtoToEntity(developerDto);

        assertEquals(developerDto.developerName(), developer.getDeveloperName());
        assertEquals(developerDto.socials(), developer.getSocials());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity method with null DeveloperCreateDto should return empty Developer entity")
    void fromCreateDtoToEntityWithNullDto() {
        DeveloperCreateDto developerDto = null;
        Developer developer = DeveloperConverter.fromCreateDtoToEntity(developerDto);
        assertNotNull(developer);
        assertNull(developer.getId());
        assertNull(developer.getDeveloperName());
        assertNull(developer.getSocials());
    }
}