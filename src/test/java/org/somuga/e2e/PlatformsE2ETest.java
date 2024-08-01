package org.somuga.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.somuga.aspect.Error;
import org.somuga.converter.PlatformConverter;
import org.somuga.dto.platform.PlatformCreateDto;
import org.somuga.dto.platform.PlatformPublicDto;
import org.somuga.entity.Platform;
import org.somuga.repository.PlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.somuga.util.message.Messages.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
class PlatformsE2ETest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String USER_ID = "google-auth2|1234567890";
    private final String PUBLIC_API_PATH = "/api/v1/game/platform/public";
    private final String ADMIN_API_PATH = "/api/v1/game/platform/admin";

    MockMvc mockMvc;
    @Autowired
    private PlatformRepository platformRepository;
    @Autowired
    private WebApplicationContext controller;
    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @AfterEach
    public void cleanUp() {
        platformRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(controller)
                .apply(springSecurity())
                .build();
    }

    public PlatformPublicDto createPlatform(String name) {
        Platform platform = Platform.builder().platformName(name).build();
        return PlatformConverter.fromEntityToPublicDto(platformRepository.save(platform));
    }

    private void assertErrors(Error error, int status, String path, String method) {
        assertEquals(status, error.getStatus());
        assertEquals(path, error.getPath());
        assertEquals(method, error.getMethod());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test create platform and expect 201")
    void testCreatePlatform() throws Exception {
        PlatformCreateDto platformCreateDto = new PlatformCreateDto("PlayStation");

        String response = mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platformCreateDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        PlatformPublicDto platformPublicDto = mapper.readValue(response, PlatformPublicDto.class);

        assertEquals(1, platformRepository.count());
        assertNotNull(platformPublicDto.id());
        Platform platform = platformRepository.findById(platformPublicDto.id()).orElse(null);
        assertNotNull(platform);
        assertEquals(platformCreateDto.platformName(), platform.getPlatformName());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create platform without authorization and expect 403")
    void testCreatePlatformWithoutAuthorization() throws Exception {
        PlatformCreateDto platformCreateDto = new PlatformCreateDto("PlayStation");

        mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platformCreateDto)))
                .andExpect(status().isForbidden());

        assertEquals(0, platformRepository.count());
    }

    @Test
    @DisplayName("Test create platform without authentication and expect 401")
    void testCreatePlatformWithoutAuthentication() throws Exception {
        PlatformCreateDto platformCreateDto = new PlatformCreateDto("PlayStation");

        mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platformCreateDto)))
                .andExpect(status().isUnauthorized());

        assertEquals(0, platformRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test create platform with empty name and expect 400")
    void testCreatePlatformWithInvalidData() throws Exception {
        PlatformCreateDto platformCreateDto = new PlatformCreateDto("");

        String response = mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platformCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);
        assertTrue(error.getMessage().contains(INVALID_PLATFORM_NAME));
        assertErrors(error, 400, ADMIN_API_PATH, "POST");

        assertEquals(0, platformRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test create platform with invalid name and expect 400")
    void testCreatePlatformWithInvalidName() throws Exception {
        PlatformCreateDto platformCreateDto = new PlatformCreateDto("PlayStation 5!");

        String response = mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platformCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);
        assertTrue(error.getMessage().contains(INVALID_PLATFORM_NAME));
        assertErrors(error, 400, ADMIN_API_PATH, "POST");

        assertEquals(0, platformRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test create platform with name exceeding 50 characters and expect 400")
    void testCreatePlatformWithLongName() throws Exception {
        PlatformCreateDto platformCreateDto = new PlatformCreateDto("ABCDEF".repeat(10));

        String response = mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platformCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);
        assertTrue(error.getMessage().contains(INVALID_PLATFORM_NAME));
        assertErrors(error, 400, ADMIN_API_PATH, "POST");

        assertEquals(0, platformRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test create platform with existing name and expect 400")
    void testCreatePlatformWithExistingName() throws Exception {
        createPlatform("PlayStation");

        PlatformCreateDto platformCreateDto = new PlatformCreateDto("PlayStation");

        String response = mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platformCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);
        assertEquals(PLATFORM_ALREADY_EXISTS + platformCreateDto.platformName(), error.getMessage());
        assertErrors(error, 400, ADMIN_API_PATH, "POST");

        assertEquals(1, platformRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test create platform with null name and expect 400")
    void testCreatePlatformWithNullName() throws Exception {
        PlatformCreateDto platformCreateDto = new PlatformCreateDto(null);

        String response = mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platformCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);
        assertTrue(error.getMessage().contains(INVALID_PLATFORM_NAME));
        assertErrors(error, 400, ADMIN_API_PATH, "POST");

        assertEquals(0, platformRepository.count());
    }

    @Test
    @DisplayName("Test get all platforms and expect 200")
    void testGetAllPlatforms() throws Exception {
        PlatformPublicDto platformPublicDto = createPlatform("PlayStation");

        String response = mockMvc.perform(get(PUBLIC_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PlatformPublicDto[] platformPublicDtos = mapper.readValue(response, PlatformPublicDto[].class);

        assertEquals(1, platformPublicDtos.length);
        assertEquals(platformPublicDto, platformPublicDtos[0]);
    }

    @Test
    @DisplayName("Test get all platforms with name and expect 200")
    void testGetAllPlatformsWithName() throws Exception {
        PlatformPublicDto platformPublicDto = createPlatform("PlayStation");
        createPlatform("Xbox");

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?name=PlayStation")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PlatformPublicDto[] platformPublicDtos = mapper.readValue(response, PlatformPublicDto[].class);

        assertEquals(1, platformPublicDtos.length);
        assertEquals(platformPublicDto, platformPublicDtos[0]);
    }

    @Test
    @DisplayName("Test get all platforms with name case insensitive and expect 200")
    void testGetAllPlatformsWithNameCaseInsensitive() throws Exception {
        PlatformPublicDto platformPublicDto = createPlatform("PlayStation");
        createPlatform("Xbox");

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?name=playstation")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PlatformPublicDto[] platformPublicDtos = mapper.readValue(response, PlatformPublicDto[].class);

        assertEquals(1, platformPublicDtos.length);
        assertEquals(platformPublicDto, platformPublicDtos[0]);
    }

    @Test
    @DisplayName("Test get all platforms with non-existing name and expect 200")
    void testGetAllPlatformsWithNonExistingName() throws Exception {
        createPlatform("PlayStation");
        createPlatform("Xbox");

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?name=Switch")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PlatformPublicDto[] platformPublicDtos = mapper.readValue(response, PlatformPublicDto[].class);

        assertEquals(0, platformPublicDtos.length);
    }

    @Test
    @DisplayName("Test get platform by id and expect 200")
    void testGetPlatformById() throws Exception {
        PlatformPublicDto platformPublicDto = createPlatform("PlayStation");

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + platformPublicDto.id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PlatformPublicDto platformPublicDtoResponse = mapper.readValue(response, PlatformPublicDto.class);

        assertEquals(platformPublicDto, platformPublicDtoResponse);
    }

    @Test
    @DisplayName("Test get platform by non-existing id and expect 404")
    void testGetPlatformByNonExistingId() throws Exception {
        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(PLATFORM_NOT_FOUND + 1, error.getMessage());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test update platform and expect 200")
    void testUpdatePlatform() throws Exception {
        PlatformPublicDto platformPublicDto = createPlatform("PlayStation");

        PlatformCreateDto platformCreateDto = new PlatformCreateDto("Xbox");

        String response = mockMvc.perform(put(ADMIN_API_PATH + "/" + platformPublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platformCreateDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PlatformPublicDto platformPublicDtoResponse = mapper.readValue(response, PlatformPublicDto.class);
        Platform platform = platformRepository.findById(platformPublicDto.id()).orElse(null);
        assertNotNull(platform);

        assertEquals(1, platformRepository.count());
        assertEquals(platformPublicDto.id(), platformPublicDtoResponse.id());
        assertEquals(platformCreateDto.platformName(), platformPublicDtoResponse.platformName(), platform.getPlatformName());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update platform without authorization and expect 403")
    void testUpdatePlatformWithoutAuthorization() throws Exception {
        PlatformPublicDto platformPublicDto = createPlatform("PlayStation");

        PlatformCreateDto platformCreateDto = new PlatformCreateDto("Xbox");

        mockMvc.perform(put(ADMIN_API_PATH + "/" + platformPublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platformCreateDto)))
                .andExpect(status().isForbidden());

        Platform platform = platformRepository.findById(platformPublicDto.id()).orElse(null);
        assertNotNull(platform);
        assertEquals(platformPublicDto.platformName(), platform.getPlatformName());
    }

    @Test
    @DisplayName("Test update platform without authentication and expect 401")
    void testUpdatePlatformWithoutAuthentication() throws Exception {
        PlatformPublicDto platformPublicDto = createPlatform("PlayStation");

        PlatformCreateDto platformCreateDto = new PlatformCreateDto("Xbox");

        mockMvc.perform(put(ADMIN_API_PATH + "/" + platformPublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platformCreateDto)))
                .andExpect(status().isUnauthorized());

        Platform platform = platformRepository.findById(platformPublicDto.id()).orElse(null);
        assertNotNull(platform);
        assertEquals(platformPublicDto.platformName(), platform.getPlatformName());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test update platform with invalid name and expect 400")
    void testUpdatePlatformWithInvalidName() throws Exception {
        PlatformPublicDto platformPublicDto = createPlatform("PlayStation");

        PlatformCreateDto platformCreateDto = new PlatformCreateDto("Xbox 5!");

        String response = mockMvc.perform(put(ADMIN_API_PATH + "/" + platformPublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platformCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);
        assertTrue(error.getMessage().contains(INVALID_PLATFORM_NAME));
        assertErrors(error, 400, ADMIN_API_PATH + "/" + platformPublicDto.id(), "PUT");

        Platform platform = platformRepository.findById(platformPublicDto.id()).orElse(null);
        assertNotNull(platform);
        assertEquals(platformPublicDto.platformName(), platform.getPlatformName());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test update platform with duplicate name and expect 400")
    void testUpdatePlatformWithDuplicateName() throws Exception {
        PlatformPublicDto platformPublicDto = createPlatform("PlayStation");
        createPlatform("Xbox");

        PlatformCreateDto platformCreateDto = new PlatformCreateDto("Xbox");

        String response = mockMvc.perform(put(ADMIN_API_PATH + "/" + platformPublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platformCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);
        assertEquals(PLATFORM_ALREADY_EXISTS + platformCreateDto.platformName(), error.getMessage());
        assertErrors(error, 400, ADMIN_API_PATH + "/" + platformPublicDto.id(), "PUT");

        Platform platform = platformRepository.findById(platformPublicDto.id()).orElse(null);
        assertNotNull(platform);
        assertEquals(platformPublicDto.platformName(), platform.getPlatformName());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test update platform not found and expect 404")
    void testUpdatePlatformNotFound() throws Exception {
        PlatformCreateDto platformCreateDto = new PlatformCreateDto("Xbox");

        String response = mockMvc.perform(put(ADMIN_API_PATH + "/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platformCreateDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);
        assertEquals(PLATFORM_NOT_FOUND + 1, error.getMessage());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test delete platform and expect 204")
    void testDeletePlatform() throws Exception {
        PlatformPublicDto platformPublicDto = createPlatform("PlayStation");

        mockMvc.perform(delete(ADMIN_API_PATH + "/" + platformPublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertEquals(0, platformRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete platform without authorization and expect 403")
    void testDeletePlatformWithoutAuthorization() throws Exception {
        PlatformPublicDto platformPublicDto = createPlatform("PlayStation");

        mockMvc.perform(delete(ADMIN_API_PATH + "/" + platformPublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        assertEquals(1, platformRepository.count());
    }

    @Test
    @DisplayName("Test delete platform without authentication and expect 401")
    void testDeletePlatformWithoutAuthentication() throws Exception {
        PlatformPublicDto platformPublicDto = createPlatform("PlayStation");

        mockMvc.perform(delete(ADMIN_API_PATH + "/" + platformPublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        assertEquals(1, platformRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test delete platform not found and expect 404")
    void testDeletePlatformNotFound() throws Exception {
        String response = mockMvc.perform(delete(ADMIN_API_PATH + "/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);
        assertEquals(PLATFORM_NOT_FOUND + 1, error.getMessage());
    }


}
