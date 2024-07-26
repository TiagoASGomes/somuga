package org.somuga.service;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.somuga.converter.DeveloperConverter;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.entity.Developer;
import org.somuga.repository.DeveloperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class DeveloperServiceTest {

    private static MockedStatic<DeveloperConverter> developerConverterMockedStatic;
    @Autowired
    private DeveloperService developerService;
    @MockBean
    private DeveloperRepository developerRepository;

    @BeforeAll
    static void setUp() {
        developerConverterMockedStatic = Mockito.mockStatic(DeveloperConverter.class);
    }

    @AfterAll
    static void tearDownAll() {
        developerConverterMockedStatic.close();
    }

    @AfterEach
    void init() {
        developerConverterMockedStatic.reset();
    }

    @Test
    @DisplayName("Test getAll method without name parameter and expect to return a list of DeveloperPublicDto")
    void getAll() {
        Developer developer = Developer.builder()
                .id(1L)
                .developerName("Test Developer")
                .socials(List.of("Test Socials"))
                .build();

        DeveloperPublicDto developerPublicDto = new DeveloperPublicDto(1L, "Test Developer", List.of("Test Socials"));

        List<Developer> developers = List.of(developer);

        Mockito.when(developerRepository.findAll()).thenReturn(developers);
        developerConverterMockedStatic.when(() -> DeveloperConverter.fromEntityListToPublicDtoList(developers)).thenReturn(List.of(developerPublicDto));

        List<DeveloperPublicDto> developerPublicDtos = developerService.getAll(null);

        assertNotNull(developerPublicDtos);
        assertEquals(developer.getId(), developerPublicDtos.get(0).id());
        assertEquals(developer.getDeveloperName(), developerPublicDtos.get(0).developerName());
        assertEquals(developer.getSocials(), developerPublicDtos.get(0).socials());

        Mockito.verify(developerRepository).findAll();
        developerConverterMockedStatic.verify(() -> DeveloperConverter.fromEntityListToPublicDtoList(developers));
        Mockito.verifyNoMoreInteractions(developerRepository);
    }

    @Test
    void getById() {
    }

    @Test
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void findByDeveloperName() {
    }

    @Test
    void findById() {
    }

}