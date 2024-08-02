package org.somuga.service;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.somuga.converter.DeveloperConverter;
import org.somuga.dto.developer.DeveloperCreateDto;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.entity.Developer;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.user.DuplicateFieldException;
import org.somuga.repository.DeveloperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.somuga.util.message.Messages.DEVELOPER_ALREADY_EXISTS;
import static org.somuga.util.message.Messages.DEVELOPER_NOT_FOUND;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class DeveloperServiceTest {

    private static MockedStatic<DeveloperConverter> developerConverterMockedStatic;
    private final Developer developer = Developer.builder()
            .id(1L)
            .developerName("Test Developer")
            .socials(List.of("Test Socials"))
            .build();

    private final DeveloperPublicDto responseDto = new DeveloperPublicDto(1L, "Test Developer", List.of("Test Socials"));
    @Autowired
    private DeveloperService developerService;
    @MockBean
    private DeveloperRepository developerRepository;

    @BeforeAll
    static void setUp() {
        developerConverterMockedStatic = Mockito.mockStatic(DeveloperConverter.class);
    }

    @AfterAll
    static void tearDown() {
        developerConverterMockedStatic.close();
    }

    @AfterEach
    void reset() {
        developerConverterMockedStatic.reset();
    }

    @Test
    @DisplayName("Test getAll method without name parameter and expect to return a list of DeveloperPublicDto")
    void getAll() {
        List<Developer> developers = List.of(developer);

        Mockito.when(developerRepository.findAll()).thenReturn(developers);
        developerConverterMockedStatic.when(() -> DeveloperConverter.fromEntityListToPublicDtoList(developers)).thenReturn(List.of(responseDto));

        List<DeveloperPublicDto> developerPublicDtos = developerService.getAll(null);

        assertNotNull(developerPublicDtos);
        assertEquals(developer.getId(), developerPublicDtos.get(0).id());
        assertEquals(developer.getDeveloperName(), developerPublicDtos.get(0).developerName());
        assertEquals(developer.getSocials(), developerPublicDtos.get(0).socials());

        Mockito.verify(developerRepository).findAll();
        developerConverterMockedStatic.verify(() -> DeveloperConverter.fromEntityListToPublicDtoList(developers));
        Mockito.verifyNoMoreInteractions(developerRepository);
        developerConverterMockedStatic.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test getAll method with name parameter and expect to return a list of DeveloperPublicDto")
    void getAllWithName() {
        List<Developer> developers = List.of(developer);

        Mockito.when(developerRepository.findAllByDeveloperNameContainingIgnoreCase("Test Developer")).thenReturn(developers);
        developerConverterMockedStatic.when(() -> DeveloperConverter.fromEntityListToPublicDtoList(developers)).thenReturn(List.of(responseDto));

        List<DeveloperPublicDto> developerPublicDtos = developerService.getAll("Test Developer");

        assertNotNull(developerPublicDtos);
        assertEquals(developer.getId(), developerPublicDtos.get(0).id());
        assertEquals(developer.getDeveloperName(), developerPublicDtos.get(0).developerName());
        assertEquals(developer.getSocials(), developerPublicDtos.get(0).socials());

        Mockito.verify(developerRepository).findAllByDeveloperNameContainingIgnoreCase("Test Developer");
        developerConverterMockedStatic.verify(() -> DeveloperConverter.fromEntityListToPublicDtoList(developers));
        Mockito.verifyNoMoreInteractions(developerRepository);
        developerConverterMockedStatic.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test getAll method with name parameter, no matching developer found and expect to return an empty list")
    void getAllWithNameNoMatch() {
        Mockito.when(developerRepository.findAllByDeveloperNameContainingIgnoreCase("Test Developer")).thenReturn(List.of());
        developerConverterMockedStatic.when(() -> DeveloperConverter.fromEntityListToPublicDtoList(List.of())).thenReturn(List.of());

        List<DeveloperPublicDto> developerPublicDtos = developerService.getAll("Test Developer");

        assertNotNull(developerPublicDtos);
        assertEquals(0, developerPublicDtos.size());

        Mockito.verify(developerRepository).findAllByDeveloperNameContainingIgnoreCase("Test Developer");
        developerConverterMockedStatic.verify(() -> DeveloperConverter.fromEntityListToPublicDtoList(List.of()));
        Mockito.verifyNoMoreInteractions(developerRepository);
        developerConverterMockedStatic.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test getById method and expect to return a DeveloperPublicDto")
    void getById() throws Exception {
        Mockito.when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));
        developerConverterMockedStatic.when(() -> DeveloperConverter.fromEntityToPublicDto(developer)).thenReturn(responseDto);

        DeveloperPublicDto developerPublicDto = developerService.getById(1L);

        assertEquals(developer.getId(), developerPublicDto.id());
        assertEquals(developer.getDeveloperName(), developerPublicDto.developerName());
        assertEquals(developer.getSocials(), developerPublicDto.socials());

        Mockito.verify(developerRepository).findById(1L);
        developerConverterMockedStatic.verify(() -> DeveloperConverter.fromEntityToPublicDto(developer));
        Mockito.verifyNoMoreInteractions(developerRepository);
        developerConverterMockedStatic.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test getById method with non-existing developer id and expect to throw DeveloperNotFoundException")
    void getByIdNonExisting() {
        Mockito.when(developerRepository.findById(1L)).thenReturn(Optional.empty());

        String errorMessage = assertThrows(DeveloperNotFoundException.class, () -> developerService.getById(1L)).getMessage();

        assertEquals(DEVELOPER_NOT_FOUND + 1, errorMessage);

        Mockito.verify(developerRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(developerRepository);
        developerConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test create method and expect to return a DeveloperPublicDto")
    void create() throws Exception {
        DeveloperCreateDto developerCreateDto = new DeveloperCreateDto("Test Developer", List.of("Test Socials"));

        Mockito.when(developerRepository.save(developer)).thenReturn(developer);
        Mockito.when(developerRepository.findByDeveloperNameIgnoreCase("Test Developer")).thenReturn(Optional.empty());
        developerConverterMockedStatic.when(() -> DeveloperConverter.fromEntityToPublicDto(developer)).thenReturn(responseDto);
        developerConverterMockedStatic.when(() -> DeveloperConverter.fromCreateDtoToEntity(developerCreateDto)).thenReturn(developer);

        DeveloperPublicDto developerPublicDto = developerService.create(developerCreateDto);

        assertEquals(developer.getId(), developerPublicDto.id());
        assertEquals(developer.getDeveloperName(), developerPublicDto.developerName());
        assertEquals(developer.getSocials(), developerPublicDto.socials());

        Mockito.verify(developerRepository).save(developer);
        Mockito.verify(developerRepository).findByDeveloperNameIgnoreCase("Test Developer");
        developerConverterMockedStatic.verify(() -> DeveloperConverter.fromEntityToPublicDto(developer));
        developerConverterMockedStatic.verify(() -> DeveloperConverter.fromCreateDtoToEntity(developerCreateDto));
        Mockito.verifyNoMoreInteractions(developerRepository);
        developerConverterMockedStatic.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test create method with existing developer name and expect to throw DuplicateFieldException")
    void createExistingDeveloperName() {
        DeveloperCreateDto developerCreateDto = new DeveloperCreateDto("Test Developer", List.of("Test Socials"));

        Mockito.when(developerRepository.findByDeveloperNameIgnoreCase("Test Developer")).thenReturn(Optional.of(developer));

        String errorMessage = assertThrows(DuplicateFieldException.class, () -> developerService.create(developerCreateDto)).getMessage();

        assertEquals(DEVELOPER_ALREADY_EXISTS + developer.getDeveloperName(), errorMessage);

        Mockito.verify(developerRepository).findByDeveloperNameIgnoreCase("Test Developer");
        Mockito.verifyNoMoreInteractions(developerRepository);
        developerConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test update method and expect to return a DeveloperPublicDto")
    void update() throws Exception {
        DeveloperCreateDto developerCreateDto = new DeveloperCreateDto("Test Developer2", List.of("Test Socials2"));
        DeveloperPublicDto updatedResponseDto = new DeveloperPublicDto(1L, "Test Developer2", List.of("Test Socials2"));

        Mockito.when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));
        Mockito.when(developerRepository.save(developer)).thenReturn(developer);
        Mockito.when(developerRepository.findByDeveloperNameIgnoreCase("Test Developer2")).thenReturn(Optional.empty());
        developerConverterMockedStatic.when(() -> DeveloperConverter.fromEntityToPublicDto(developer)).thenReturn(updatedResponseDto);

        DeveloperPublicDto developerPublicDto = developerService.update(1L, developerCreateDto);

        assertEquals(developer.getId(), developerPublicDto.id());
        assertEquals(developerCreateDto.developerName(), developerPublicDto.developerName());
        assertEquals(developerCreateDto.socials(), developerPublicDto.socials());

        Mockito.verify(developerRepository).findById(1L);
        Mockito.verify(developerRepository).save(developer);
        developerConverterMockedStatic.verify(() -> DeveloperConverter.fromEntityToPublicDto(developer));
        Mockito.verify(developerRepository).findByDeveloperNameIgnoreCase("Test Developer2");
        Mockito.verifyNoMoreInteractions(developerRepository);
        developerConverterMockedStatic.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test update method with non-existing developer id and expect to throw DeveloperNotFoundException")
    void updateNonExisting() {
        DeveloperCreateDto developerCreateDto = new DeveloperCreateDto("Test Developer2", List.of("Test Socials2"));

        Mockito.when(developerRepository.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(developerRepository.findByDeveloperNameIgnoreCase("Test Developer2")).thenReturn(Optional.empty());

        String errorMessage = assertThrows(DeveloperNotFoundException.class, () -> developerService.update(1L, developerCreateDto)).getMessage();

        assertEquals(DEVELOPER_NOT_FOUND + 1, errorMessage);

        Mockito.verify(developerRepository).findById(1L);
        Mockito.verify(developerRepository).findByDeveloperNameIgnoreCase("Test Developer2");
        Mockito.verifyNoMoreInteractions(developerRepository);
        developerConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test update method with existing developer name and expect to throw DuplicateFieldException")
    void updateExistingDeveloperName() {
        DeveloperCreateDto developerCreateDto = new DeveloperCreateDto("Test Developer", List.of("Test Socials"));

        Developer duplicateDeveloper = Developer.builder()
                .id(2L)
                .developerName("Test Developer")
                .socials(List.of("Test Socials"))
                .build();
        Mockito.when(developerRepository.findByDeveloperNameIgnoreCase("Test Developer")).thenReturn(Optional.of(duplicateDeveloper));

        String errorMessage = assertThrows(DuplicateFieldException.class, () -> developerService.update(1L, developerCreateDto)).getMessage();

        assertEquals(DEVELOPER_ALREADY_EXISTS + developer.getDeveloperName(), errorMessage);

        Mockito.verify(developerRepository).findByDeveloperNameIgnoreCase("Test Developer");
        Mockito.verifyNoMoreInteractions(developerRepository);
        developerConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test delete method")
    void delete() {
        Mockito.when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));

        assertDoesNotThrow(() -> developerService.delete(1L));

        Mockito.verify(developerRepository).findById(1L);
        Mockito.verify(developerRepository).deleteById(1L);
        Mockito.verifyNoMoreInteractions(developerRepository);
        developerConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test delete method with non-existing developer id and expect to throw DeveloperNotFoundException")
    void deleteNonExisting() {
        Mockito.when(developerRepository.findById(1L)).thenReturn(Optional.empty());

        String errorMessage = assertThrows(DeveloperNotFoundException.class, () -> developerService.delete(1L)).getMessage();

        assertEquals(DEVELOPER_NOT_FOUND + 1, errorMessage);

        Mockito.verify(developerRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(developerRepository);
        developerConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test findById method and expect to return a Developer")
    void findById() throws Exception {
        Mockito.when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));

        Developer foundDeveloper = developerService.findById(1L);

        assertEquals(developer.getId(), foundDeveloper.getId());
        assertEquals(developer.getDeveloperName(), foundDeveloper.getDeveloperName());
        assertEquals(developer.getSocials(), foundDeveloper.getSocials());

        Mockito.verify(developerRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(developerRepository);
        developerConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test findById method with non-existing developer id and expect to throw DeveloperNotFoundException")
    void findByIdNonExisting() {
        Mockito.when(developerRepository.findById(1L)).thenReturn(Optional.empty());

        String errorMessage = assertThrows(DeveloperNotFoundException.class, () -> developerService.findById(1L)).getMessage();

        assertEquals(DEVELOPER_NOT_FOUND + 1, errorMessage);

        Mockito.verify(developerRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(developerRepository);
        developerConverterMockedStatic.verifyNoInteractions();
    }

}