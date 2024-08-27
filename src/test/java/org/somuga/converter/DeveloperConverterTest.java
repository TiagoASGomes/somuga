package org.somuga.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.somuga.dto.developer.DeveloperCreateDto;
import org.somuga.dto.developer.DeveloperListDto;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.entity.Developer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class DeveloperConverterTest {

    @Test
    @DisplayName("Test fromEntityToPublicDto method should convert Developer to DeveloperPublicDto")
    void fromEntityToPublicDto() {
        Developer developer = Developer.builder()
                .id(1L)
                .developerName("Developer")
                .build();

        DeveloperPublicDto developerPublicDto = DeveloperConverter.fromEntityToPublicDto(developer);

        assertEquals(developer.getId(), developerPublicDto.id());
        assertEquals(developer.getDeveloperName(), developerPublicDto.developerName());
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto method with null entity should return null")
    void fromEntityToPublicDtoWithNullEntity() {
        DeveloperPublicDto developerPublicDto = DeveloperConverter.fromEntityToPublicDto(null);
        assertNull(developerPublicDto);
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList method should convert list of Developer to list of DeveloperPublicDto")
    void fromEntityListToPublicDtoList() {
        Developer developer1 = Developer.builder()
                .id(1L)
                .developerName("Developer1")
                .build();

        Developer developer2 = Developer.builder()
                .id(2L)
                .developerName("Developer2")
                .build();

        List<Developer> developers = List.of(developer1, developer2);
        DeveloperListDto developerPublicDtos = DeveloperConverter.fromEntityListToPublicDtoList(developers, 2L);

        assertEquals(developers.size(), developerPublicDtos.developers().size());
        assertEquals(developers.get(0).getId(), developerPublicDtos.developers().get(0).id());
        assertEquals(developers.get(0).getDeveloperName(), developerPublicDtos.developers().get(0).developerName());
        assertEquals(developers.get(1).getId(), developerPublicDtos.developers().get(1).id());
        assertEquals(developers.get(1).getDeveloperName(), developerPublicDtos.developers().get(1).developerName());
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList method with empty list should return empty list")
    void fromEntityListToPublicDtoListWithEmptyList() {
        List<Developer> developers = List.of();
        DeveloperListDto developerPublicDtos = DeveloperConverter.fromEntityListToPublicDtoList(developers, 0L);
        assertEquals(developers.size(), developerPublicDtos.developers().size());
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList method with null list should return empty list")
    void fromEntityListToPublicDtoListWithNullList() {
        DeveloperListDto developerPublicDtos = DeveloperConverter.fromEntityListToPublicDtoList(null, 0L);
        assertEquals(0, developerPublicDtos.developers().size());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity method should convert DeveloperCreateDto to Developer entity")
    void fromCreateDtoToEntity() {
        DeveloperCreateDto developerDto = new DeveloperCreateDto("Developer");

        Developer developer = DeveloperConverter.fromCreateDtoToEntity(developerDto);

        assertEquals(developerDto.developerName(), developer.getDeveloperName());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity method with null DeveloperCreateDto should return null")
    void fromCreateDtoToEntityWithNullDto() {
        Developer developer = DeveloperConverter.fromCreateDtoToEntity(null);
        assertNull(developer);

    }
}