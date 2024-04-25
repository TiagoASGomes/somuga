package org.somuga.platforms;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.somuga.aspect.Error;
import org.somuga.dto.platform.PlatformCreateDto;
import org.somuga.dto.platform.PlatformPublicDto;
import org.somuga.repository.PlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.somuga.message.Messages.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlatformsControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String API_PATH = "/api/v1/platform";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PlatformRepository platformRepository;

    @AfterEach
    public void cleanUp() {
        platformRepository.deleteAll();
    }

    public PlatformPublicDto createPlatform(String name) throws Exception {
        PlatformCreateDto platform = new PlatformCreateDto(name);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platform)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return mapper.readValue(response, PlatformPublicDto.class);
    }

    @Test
    @DisplayName("Test create platform and expect 201 and platform")
    void testCreatePlatform() throws Exception {
        PlatformCreateDto platform = new PlatformCreateDto("Test Platform");

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platform)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        PlatformPublicDto platformResponse = mapper.readValue(response, PlatformPublicDto.class);

        assertNotNull(platformResponse.id());
        assertEquals(platform.platformName().toLowerCase(), platformResponse.platformName());
    }

    @Test
    @DisplayName("Test create platform with empty name and expect 400")
    void testCreatePlatformWithEmptyName() throws Exception {
        PlatformCreateDto platform = new PlatformCreateDto("");

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platform)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_PLATFORM_NAME));
        assertEquals(400, error.getStatus());
        assertEquals(API_PATH, error.getPath());
        assertEquals("POST", error.getMethod());
    }

    @Test
    @DisplayName("Test create platform with duplicate name and expect 400")
    void testCreatePlatformWithDuplicateName() throws Exception {
        createPlatform("Test Platform");

        PlatformCreateDto platform = new PlatformCreateDto("Test Platform");

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platform)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(PLATFORM_ALREADY_EXISTS + "Test Platform"));
        assertEquals(400, error.getStatus());
        assertEquals(API_PATH, error.getPath());
        assertEquals("POST", error.getMethod());
    }

    @Test
    @DisplayName("Test create platform with null name and expect 400")
    void testCreatePlatformWithNullName() throws Exception {
        PlatformCreateDto platform = new PlatformCreateDto(null);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platform)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_PLATFORM_NAME));
        assertEquals(400, error.getStatus());
        assertEquals(API_PATH, error.getPath());
        assertEquals("POST", error.getMethod());
    }

    @Test
    @DisplayName("Test create platform with name longer than 50 characters and expect 400")
    void testCreatePlatformWithLongName() throws Exception {
        PlatformCreateDto platform = new PlatformCreateDto("A".repeat(51));

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platform)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_PLATFORM_NAME));
        assertEquals(400, error.getStatus());
        assertEquals(API_PATH, error.getPath());
        assertEquals("POST", error.getMethod());
    }

    @Test
    @DisplayName("Test create platform with invalid characters and expect 400")
    void testCreatePlatformWithInvalidCharacters() throws Exception {
        PlatformCreateDto platform = new PlatformCreateDto("Test Platform!");

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(platform)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_PLATFORM_NAME));
        assertEquals(400, error.getStatus());
        assertEquals(API_PATH, error.getPath());
        assertEquals("POST", error.getMethod());
    }

    @Test
    @DisplayName("Test get all platforms and expect 200")
    void testGetAllPlatforms() throws Exception {
        createPlatform("Test Platform 1");
        createPlatform("Test Platform 2");

        String response = mockMvc.perform(get(API_PATH))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PlatformPublicDto[] platforms = mapper.readValue(response, PlatformPublicDto[].class);

        assertEquals(2, platforms.length);
    }

    @Test
    @DisplayName("Test get all platforms with pagination and expect 200")
    void testGetAllPlatformsWithPagination() throws Exception {
        createPlatform("Test Platform 1");
        createPlatform("Test Platform 2");
        createPlatform("Test Platform 3");

        String response = mockMvc.perform(get(API_PATH + "?page=0&size=2"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PlatformPublicDto[] platforms = mapper.readValue(response, PlatformPublicDto[].class);

        assertEquals(2, platforms.length);
    }

    @Test
    @DisplayName("Test get platform by id and expect 200")
    void testGetPlatformById() throws Exception {
        PlatformPublicDto platform = createPlatform("Test Platform");

        String response = mockMvc.perform(get(API_PATH + "/" + platform.id()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PlatformPublicDto platformResponse = mapper.readValue(response, PlatformPublicDto.class);

        assertEquals(platform.id(), platformResponse.id());
        assertEquals(platform.platformName(), platformResponse.platformName());
    }

    @Test
    @DisplayName("Test get platform by id and expect 404")
    void testGetPlatformByIdNotFound() throws Exception {
        String response = mockMvc.perform(get(API_PATH + "/9999999"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(404, error.getStatus());
        assertEquals(API_PATH + "/9999999", error.getPath());
        assertEquals("GET", error.getMethod());
        assertTrue(error.getMessage().contains(PLATFORM_NOT_FOUND + "9999999"));
    }

    @Test
    @DisplayName("Test search platform by name and expect 200")
    void testSearchPlatformByName() throws Exception {
        createPlatform("asgfsag Platform 1");
        createPlatform("Tesasgasgt Platform 2");
        createPlatform("Different text");

        String response = mockMvc.perform(get(API_PATH + "/search/Platform"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PlatformPublicDto[] platforms = mapper.readValue(response, PlatformPublicDto[].class);

        assertEquals(2, platforms.length);
    }

    @Test
    @DisplayName("Test search platform by name with pagination and expect 200")
    void testSearchPlatformByNameWithPagination() throws Exception {
        createPlatform("asgfsag Platform 1");
        createPlatform("Tesasgasgt Platform 2");
        createPlatform("Different Platform");

        String response = mockMvc.perform(get(API_PATH + "/search/Platform?page=0&size=2"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PlatformPublicDto[] platforms = mapper.readValue(response, PlatformPublicDto[].class);

        assertEquals(2, platforms.length);
    }


}
