package org.somuga.developer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.somuga.aspect.Error;
import org.somuga.dto.developer.DeveloperCreateDto;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.repository.DeveloperRepository;
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
class DeveloperControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String API_PATH = "/api/v1/developer";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DeveloperRepository developerRepository;

    @AfterEach
    public void cleanUp() {
        developerRepository.deleteAll();
    }

    public DeveloperPublicDto createDeveloper(String name) throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto(name);

        return mapper.readValue(mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(developer)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(), DeveloperPublicDto.class);
    }

    @Test
    @DisplayName("Test create developer and expect 201")
    void testCreateDeveloper() throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto("Developer");

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(developer)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        DeveloperPublicDto developerPublicDto = mapper.readValue(response, DeveloperPublicDto.class);

        assertEquals(developer.developerName().toLowerCase(), developerPublicDto.developerName());
        assertNotNull(developerPublicDto.id());
    }

    @Test
    @DisplayName("Test create developer with empty fullName and expect 400")
    void testCreateDeveloperEmptyName() throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto("");

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(developer)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_DEVELOPER_NAME));
        assertEquals(400, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertEquals(API_PATH, error.getPath());
    }

    @Test
    @DisplayName("Test create developer with null fullName and expect 400")
    void testCreateDeveloperNullName() throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto(null);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(developer)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_DEVELOPER_NAME));
        assertEquals(400, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertEquals(API_PATH, error.getPath());
    }

    @Test
    @DisplayName("Test create developer with fullName containing special characters and expect 400")
    void testCreateDeveloperSpecialCharacters() throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto("Developer!");

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(developer)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_DEVELOPER_NAME));
        assertEquals(400, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertEquals(API_PATH, error.getPath());
    }

    @Test
    @DisplayName("Test create duplicate developer and expect 400")
    void testCreateDuplicateDeveloper() throws Exception {
        createDeveloper("Developer");

        DeveloperCreateDto developer = new DeveloperCreateDto("Developer");

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(developer)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(DEVELOPER_ALREADY_EXISTS + developer.developerName().toLowerCase()));
        assertEquals(400, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertEquals(API_PATH, error.getPath());
    }

    @Test
    @DisplayName("Test get all developers and expect 200")
    void testGetAllDevelopers() throws Exception {
        createDeveloper("Developer1");
        createDeveloper("Developer2");

        String response = mockMvc.perform(get(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DeveloperPublicDto[] developers = mapper.readValue(response, DeveloperPublicDto[].class);

        assertEquals(2, developers.length);
    }

    @Test
    @DisplayName("Test get all developers with pagination and expect 200")
    void testGetAllDevelopersWithPagination() throws Exception {
        createDeveloper("Developer1");
        createDeveloper("Developer2");
        createDeveloper("Developer3");

        String response = mockMvc.perform(get(API_PATH + "?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DeveloperPublicDto[] developers = mapper.readValue(response, DeveloperPublicDto[].class);

        assertEquals(2, developers.length);
    }

    @Test
    @DisplayName("Test get developer by id and expect 200")
    void testGetDeveloperById() throws Exception {
        DeveloperPublicDto developer = createDeveloper("Developer");

        String response = mockMvc.perform(get(API_PATH + "/" + developer.id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DeveloperPublicDto developerPublicDto = mapper.readValue(response, DeveloperPublicDto.class);

        assertEquals(developer.developerName().toLowerCase(), developerPublicDto.developerName());
        assertEquals(developer.id(), developerPublicDto.id());
    }

    @Test
    @DisplayName("Test get developer by id that does not exist and expect 404")
    void testGetDeveloperByIdNotFound() throws Exception {
        String response = mockMvc.perform(get(API_PATH + "/9999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(DEVELOPER_NOT_FOUND + 9999999));
        assertEquals(404, error.getStatus());
        assertEquals("GET", error.getMethod());
        assertEquals(API_PATH + "/9999999", error.getPath());
    }

    @Test
    @DisplayName("Test get developer by fullName and expect 200")
    void testGetDeveloperByName() throws Exception {
        createDeveloper("Developer1");
        createDeveloper("Developer2");
        createDeveloper("Developer3");

        String response = mockMvc.perform(get(API_PATH + "/search/Developer")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DeveloperPublicDto[] developers = mapper.readValue(response, DeveloperPublicDto[].class);

        assertEquals(3, developers.length);
    }

    @Test
    @DisplayName("Test get developer by fullName that does not exist and expect 200")
    void testGetDeveloperByNameNotFound() throws Exception {
        createDeveloper("Developer1");
        createDeveloper("Developer2");
        createDeveloper("Developer3");

        String response = mockMvc.perform(get(API_PATH + "/search/Developer4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DeveloperPublicDto[] developers = mapper.readValue(response, DeveloperPublicDto[].class);

        assertEquals(0, developers.length);
    }

    @Test
    @DisplayName("Test get developer by fullName with pagination and expect 200")
    void testGetDeveloperByNameWithPagination() throws Exception {
        createDeveloper("Developer1");
        createDeveloper("Developer2");
        createDeveloper("Developer3");

        String response = mockMvc.perform(get(API_PATH + "/search/Developer?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DeveloperPublicDto[] developers = mapper.readValue(response, DeveloperPublicDto[].class);

        assertEquals(2, developers.length);
    }


}
