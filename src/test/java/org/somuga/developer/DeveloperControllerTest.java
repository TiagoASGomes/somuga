package org.somuga.developer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.somuga.aspect.Error;
import org.somuga.converter.DeveloperConverter;
import org.somuga.dto.developer.DeveloperCreateDto;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.entity.Developer;
import org.somuga.repository.DeveloperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.somuga.util.message.Messages.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration
@ActiveProfiles("test")
public class DeveloperControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String PRIVATE_API_PATH = "/api/v1/developer/private";
    private final String PUBLIC_API_PATH = "/api/v1/developer/public";
    MockMvc mockMvc;
    @Autowired
    private DeveloperRepository developerRepository;
    @Autowired
    private WebApplicationContext controller;
    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(controller)
                .apply(springSecurity())
                .build();
    }

    @After
    public void cleanUp() {
        developerRepository.deleteAll();
    }

    public DeveloperPublicDto createDeveloper(String name, List<String> socials) {
        Developer developer = new Developer(name, socials);

        return DeveloperConverter.fromEntityToPublicDto(developerRepository.save(developer));
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Test create developer and expect 201")
    public void testCreateDeveloperAuthorized() throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto("Developer", List.of("twitter.com/developer", "github.com/developer"));

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(developer)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        DeveloperPublicDto developerPublicDto = mapper.readValue(response, DeveloperPublicDto.class);

        assertEquals(developer.developerName(), developerPublicDto.developerName());
        assertNotNull(developerPublicDto.id());
    }

    @Test
    @DisplayName("Test create developer without authorization and expect 401")
    public void testCreateDeveloperUnauthorized() throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto("Developer", List.of("twitter.com/developer", "github.com/developer"));

        mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(developer)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Test create developer with empty fullName and expect 400")
    public void testCreateDeveloperEmptyName() throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto("", List.of("twitter.com/developer", "github.com/developer"));

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(developer)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_DEVELOPER_NAME));
        assertEquals(400, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertEquals(PRIVATE_API_PATH, error.getPath());
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Test create developer with null fullName and expect 400")
    public void testCreateDeveloperNullName() throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto(null, List.of("twitter.com/developer", "github.com/developer"));

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(developer)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_DEVELOPER_NAME));
        assertEquals(400, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertEquals(PRIVATE_API_PATH, error.getPath());
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Test create developer with fullName containing special characters and expect 400")
    public void testCreateDeveloperSpecialCharacters() throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto("Developer!", List.of("twitter.com/developer", "github.com/developer"));

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(developer)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_DEVELOPER_NAME));
        assertEquals(400, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertEquals(PRIVATE_API_PATH, error.getPath());
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Test create duplicate developer and expect 400")
    public void testCreateDuplicateDeveloper() throws Exception {
        createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));

        DeveloperCreateDto developer = new DeveloperCreateDto("Developer", List.of("twitter.com/developer", "github.com/developer"));

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(developer)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(DEVELOPER_ALREADY_EXISTS + developer.developerName()));
        assertEquals(400, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertEquals(PRIVATE_API_PATH, error.getPath());
    }

    @Test
    @DisplayName("Test get all developers and expect 200")
    public void testGetAllDevelopers() throws Exception {
        createDeveloper("Developer1", List.of("twitter.com/developer1", "github.com/developer1"));
        createDeveloper("Developer2", List.of("twitter.com/developer2", "github.com/developer2"));

        String response = mockMvc.perform(get(PUBLIC_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DeveloperPublicDto[] developers = mapper.readValue(response, DeveloperPublicDto[].class);

        assertEquals(2, developers.length);
    }

    @Test
    @DisplayName("Test get all developers with pagination and expect 200")
    public void testGetAllDevelopersWithPagination() throws Exception {
        createDeveloper("Developer1", List.of("twitter.com/developer1", "github.com/developer1"));
        createDeveloper("Developer2", List.of("twitter.com/developer2", "github.com/developer2"));
        createDeveloper("Developer3", List.of("twitter.com/developer3", "github.com/developer3"));

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DeveloperPublicDto[] developers = mapper.readValue(response, DeveloperPublicDto[].class);

        assertEquals(2, developers.length);
    }

    @Test
    @DisplayName("Test get developer by id and expect 200")
    public void testGetDeveloperById() throws Exception {
        DeveloperPublicDto developer = createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + developer.id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DeveloperPublicDto developerPublicDto = mapper.readValue(response, DeveloperPublicDto.class);

        assertEquals(developer.developerName(), developerPublicDto.developerName());
        assertEquals(developer.id(), developerPublicDto.id());
    }

    @Test
    @DisplayName("Test get developer by id that does not exist and expect 404")
    public void testGetDeveloperByIdNotFound() throws Exception {
        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/9999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(DEVELOPER_NOT_FOUND + 9999999));
        assertEquals(404, error.getStatus());
        assertEquals("GET", error.getMethod());
        assertEquals(PUBLIC_API_PATH + "/9999999", error.getPath());
    }

    @Test
    @DisplayName("Test get developer by fullName and expect 200")
    public void testGetDeveloperByName() throws Exception {
        createDeveloper("Developer1", List.of("twitter.com/developer1", "github.com/developer1"));
        createDeveloper("Developer2", List.of("twitter.com/developer2", "github.com/developer2"));
        createDeveloper("Developer3", List.of("twitter.com/developer3", "github.com/developer3"));

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/search/Developer")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DeveloperPublicDto[] developers = mapper.readValue(response, DeveloperPublicDto[].class);

        assertEquals(3, developers.length);
    }

    @Test
    @DisplayName("Test get developer by fullName that does not exist and expect 200")
    public void testGetDeveloperByNameNotFound() throws Exception {
        createDeveloper("Developer1", List.of("twitter.com/developer1", "github.com/developer1"));
        createDeveloper("Developer2", List.of("twitter.com/developer2", "github.com/developer2"));
        createDeveloper("Developer3", List.of("twitter.com/developer3", "github.com/developer3"));

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/search/Developer4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DeveloperPublicDto[] developers = mapper.readValue(response, DeveloperPublicDto[].class);

        assertEquals(0, developers.length);
    }

    @Test
    @DisplayName("Test get developer by fullName with pagination and expect 200")
    public void testGetDeveloperByNameWithPagination() throws Exception {
        createDeveloper("Developer1", List.of("twitter.com/developer1", "github.com/developer1"));
        createDeveloper("Developer2", List.of("twitter.com/developer2", "github.com/developer2"));
        createDeveloper("Developer3", List.of("twitter.com/developer3", "github.com/developer3"));

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/search/Developer?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DeveloperPublicDto[] developers = mapper.readValue(response, DeveloperPublicDto[].class);

        assertEquals(2, developers.length);
    }


}
