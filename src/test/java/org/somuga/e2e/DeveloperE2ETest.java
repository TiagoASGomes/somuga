package org.somuga.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.somuga.aspect.ErrorDto;
import org.somuga.converter.DeveloperConverter;
import org.somuga.dto.developer.DeveloperCreateDto;
import org.somuga.dto.developer.DeveloperListDto;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.entity.Developer;
import org.somuga.repository.DeveloperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.somuga.testUtils.Utils.*;
import static org.somuga.util.message.Messages.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
class DeveloperE2ETest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final String USER = "google-auth2|1234567890";
    private final String PUBLIC_API_PATH = "/api/v1/game/developer/public";
    private final String ADMIN_API_PATH = "/api/v1/game/developer/admin";

    private MockMvc mockMvc;
    @Autowired
    private DeveloperRepository developerRepository;
    @Autowired
    private WebApplicationContext controller;
    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(controller)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    public void cleanUp() {
        developerRepository.deleteAll();
    }

    public DeveloperPublicDto createDeveloper(String name, List<String> socials) {
        Developer developer = Developer.builder()
                .developerName(name)
                .socials(socials)
                .build();

        return DeveloperConverter.fromEntityToPublicDto(developerRepository.save(developer));
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test create developer and expect 201")
    void testCreateDeveloperAuthorized() throws Exception {
        DeveloperCreateDto developerCreateDto = new DeveloperCreateDto("Developer", List.of("twitter.com/developer", "github.com/developer"));

        String response = postRequest(ADMIN_API_PATH, status().isCreated(), mapper.writeValueAsString(developerCreateDto), mockMvc);

        DeveloperPublicDto developerPublicDto = mapper.readValue(response, DeveloperPublicDto.class);

        assertEquals(1, developerRepository.count());
        assertNotNull(developerPublicDto.id());

        Developer developer = developerRepository.findById(developerPublicDto.id()).orElse(null);

        assertNotNull(developer);
        assertEquals(developerCreateDto.developerName(), developerPublicDto.developerName(), developer.getDeveloperName());
        assertEquals(2, developerPublicDto.socials().size(), developer.getSocials().size());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create developer without authorization and expect 403")
    void testCreateDeveloperUnauthorized() throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto("Developer", List.of("twitter.com/developer", "github.com/developer"));

        postRequest(ADMIN_API_PATH, status().isForbidden(), mapper.writeValueAsString(developer), mockMvc);

        assertEquals(0, developerRepository.count());
    }

    @Test
    @DisplayName("Test create developer without authentication and expect 401")
    void testCreateDeveloperUnauthenticated() throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto("Developer", List.of("twitter.com/developer", "github.com/developer"));

        postRequest(ADMIN_API_PATH, status().isUnauthorized(), mapper.writeValueAsString(developer), mockMvc);

        assertEquals(0, developerRepository.count());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test create developer with invalid developer name and expect 400")
    void testCreateDeveloperInvalidDeveloperName() throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto("Developer!", List.of("twitter.com/developer", "github.com/developer"));

        String response = postRequest(ADMIN_API_PATH, status().isBadRequest(), mapper.writeValueAsString(developer), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, developerRepository.count());
        assertTrue(errorDto.message().contains(INVALID_DEVELOPER_NAME));
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test create developer with empty developer name and expect 400")
    void testCreateDeveloperEmptyDeveloperName() throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto("", List.of("twitter.com/developer", "github.com/developer"));

        String response = postRequest(ADMIN_API_PATH, status().isBadRequest(), mapper.writeValueAsString(developer), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, developerRepository.count());
        assertTrue(errorDto.message().contains(INVALID_DEVELOPER_NAME));
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test create developer with name exceeding 255 characters and expect 400")
    void testCreateDeveloperExceedingDeveloperName() throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto("ABCDEF".repeat(50), List.of("twitter.com/developer", "github.com/developer"));

        String response = postRequest(ADMIN_API_PATH, status().isBadRequest(), mapper.writeValueAsString(developer), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, developerRepository.count());
        assertTrue(errorDto.message().contains(INVALID_DEVELOPER_NAME));
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test create developer with no socials and expect 201")
    void testCreateDeveloperNoSocials() throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto("Developer", List.of());

        String response = postRequest(ADMIN_API_PATH, status().isCreated(), mapper.writeValueAsString(developer), mockMvc);

        DeveloperPublicDto developerPublicDto = mapper.readValue(response, DeveloperPublicDto.class);

        assertEquals(1, developerRepository.count());
        assertNotNull(developerPublicDto.id());

        Developer developerEntity = developerRepository.findById(developerPublicDto.id()).orElse(null);

        assertNotNull(developerEntity);
        assertEquals(developer.developerName(), developerPublicDto.developerName(), developerEntity.getDeveloperName());
        assertEquals(0, developerPublicDto.socials().size(), developerEntity.getSocials().size());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test create developer with null socials and expect 201")
    void testCreateDeveloperNullSocials() throws Exception {
        DeveloperCreateDto developer = new DeveloperCreateDto("Developer", null);

        String response = postRequest(ADMIN_API_PATH, status().isCreated(), mapper.writeValueAsString(developer), mockMvc);

        DeveloperPublicDto developerPublicDto = mapper.readValue(response, DeveloperPublicDto.class);

        assertEquals(1, developerRepository.count());
        assertNotNull(developerPublicDto.id());

        Developer developerEntity = developerRepository.findById(developerPublicDto.id()).orElse(null);

        assertNotNull(developerEntity);
        assertEquals(developer.developerName(), developerPublicDto.developerName(), developerEntity.getDeveloperName());
        assertEquals(0, developerPublicDto.socials().size());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test create developer with duplicate developer name and expect 400")
    void testCreateDeveloperDuplicateDeveloperName() throws Exception {
        createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));

        DeveloperCreateDto developer = new DeveloperCreateDto("Developer", List.of("twitter.com/developer", "github.com/developer"));

        String response = postRequest(ADMIN_API_PATH, status().isBadRequest(), mapper.writeValueAsString(developer), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(1, developerRepository.count());
        assertEquals(DEVELOPER_ALREADY_EXISTS + developer.developerName(), errorDto.message());
    }

    @Test
    @DisplayName("Test get all developers and expect 200")
    void testGetAllDevelopers() throws Exception {
        createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));
        createDeveloper("Teste", List.of("twitter.com/developer", "github.com/developer"));

        String response = getRequest(PUBLIC_API_PATH, status().isOk(), mockMvc);

        DeveloperListDto developers = mapper.readValue(response, DeveloperListDto.class);

        assertEquals(2, developers.developers().size());
        assertEquals(2, developers.count());
    }

    @Test
    @DisplayName("Test get all developers with name and expect 200")
    void testGetAllDevelopersWithName() throws Exception {
        createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));
        createDeveloper("Developerr", List.of("twitter.com/developer", "github.com/developer"));
        createDeveloper("Teste", List.of("twitter.com/developer", "github.com/developer"));

        String response = getRequest(PUBLIC_API_PATH + "?name=Developer", status().isOk(), mockMvc);

        DeveloperListDto developers = mapper.readValue(response, DeveloperListDto.class);

        assertEquals(2, developers.count());
        assertEquals(2, developers.developers().size());
    }

    @Test
    @DisplayName("Test get all developers with name case insensitive and expect 200")
    void testGetAllDevelopersWithNameCaseInsensitive() throws Exception {
        createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));
        createDeveloper("Developerr", List.of("twitter.com/developer", "github.com/developer"));

        createDeveloper("Teste", List.of("twitter.com/developer", "github.com/developer"));

        String response = getRequest(PUBLIC_API_PATH + "?name=developer", status().isOk(), mockMvc);

        DeveloperListDto developers = mapper.readValue(response, DeveloperListDto.class);

        assertEquals(2, developers.count());
        assertEquals(2, developers.developers().size());
    }

    @Test
    @DisplayName("Test get all developers with a search query that does not exist and expect 200")
    void testGetAllDevelopersWithNonExistentName() throws Exception {
        createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));
        createDeveloper("Teste", List.of("twitter.com/developer", "github.com/developer"));

        String response = getRequest(PUBLIC_API_PATH + "?name=NonExistent", status().isOk(), mockMvc);

        DeveloperListDto developers = mapper.readValue(response, DeveloperListDto.class);

        assertEquals(0, developers.count());
        assertEquals(0, developers.developers().size());
    }

    @Test
    @DisplayName("Test get developer by id and expect 200")
    void testGetDeveloperById() throws Exception {
        DeveloperPublicDto developerPublicDto = createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));

        String response = getRequest(PUBLIC_API_PATH + "/" + developerPublicDto.id(), status().isOk(), mockMvc);

        DeveloperPublicDto developer = mapper.readValue(response, DeveloperPublicDto.class);

        assertEquals(developerPublicDto, developer);
    }

    @Test
    @DisplayName("Test get developer by id not found and expect 404")
    void testGetDeveloperByIdNotFound() throws Exception {
        String response = getRequest(PUBLIC_API_PATH + "/1", status().isNotFound(), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(DEVELOPER_NOT_FOUND + 1, errorDto.message());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test update developer and expect 200")
    void testUpdateDeveloper() throws Exception {
        DeveloperPublicDto developerPublicDto = createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));
        DeveloperCreateDto developerCreateDto = new DeveloperCreateDto("Developer Updated", List.of("twitter.com/developer", "github.com/developer"));

        String response = putRequest(ADMIN_API_PATH + "/" + developerPublicDto.id(), status().isOk(), mapper.writeValueAsString(developerCreateDto), mockMvc);

        DeveloperPublicDto developerDto = mapper.readValue(response, DeveloperPublicDto.class);

        Developer developer = developerRepository.findById(developerPublicDto.id()).orElse(null);
        assertNotNull(developer);

        assertEquals(developerPublicDto.id(), developerDto.id());
        assertEquals(developerCreateDto.developerName(), developerDto.developerName(), developer.getDeveloperName());
        assertEquals(developerCreateDto.socials().size(), developerDto.socials().size(), developer.getSocials().size());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test update developer without authorization and expect 403")
    void testUpdateDeveloperUnauthorized() throws Exception {
        DeveloperPublicDto developerPublicDto = createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));
        DeveloperCreateDto developerCreateDto = new DeveloperCreateDto("Developer Updated", List.of("twitter.com/developer", "github.com/developer"));

        putRequest(ADMIN_API_PATH + "/" + developerPublicDto.id(), status().isForbidden(), mapper.writeValueAsString(developerCreateDto), mockMvc);

        Developer developer = developerRepository.findById(developerPublicDto.id()).orElse(null);
        assertNotNull(developer);
        assertNotEquals(developerCreateDto.developerName(), developer.getDeveloperName());
    }

    @Test
    @DisplayName("Test update developer without authentication and expect 401")
    void testUpdateDeveloperUnauthenticated() throws Exception {
        DeveloperPublicDto developerPublicDto = createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));
        DeveloperCreateDto developerCreateDto = new DeveloperCreateDto("Developer Updated", List.of("twitter.com/developer", "github.com/developer"));

        putRequest(ADMIN_API_PATH + "/" + developerPublicDto.id(), status().isUnauthorized(), mapper.writeValueAsString(developerCreateDto), mockMvc);

        Developer developer = developerRepository.findById(developerPublicDto.id()).orElse(null);
        assertNotNull(developer);
        assertNotEquals(developerCreateDto.developerName(), developer.getDeveloperName());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test update developer with invalid developer name and expect 400")
    void testUpdateDeveloperInvalidDeveloperName() throws Exception {
        DeveloperPublicDto developerPublicDto = createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));
        DeveloperCreateDto developerCreateDto = new DeveloperCreateDto("Developer!", List.of("twitter.com/developer", "github.com/developer"));

        String response = putRequest(ADMIN_API_PATH + "/" + developerPublicDto.id(), status().isBadRequest(), mapper.writeValueAsString(developerCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        Developer developer = developerRepository.findById(developerPublicDto.id()).orElse(null);
        assertNotNull(developer);
        assertNotEquals(developerCreateDto.developerName(), developer.getDeveloperName());

        assertEquals(developerPublicDto.id(), developer.getId());
        assertTrue(errorDto.message().contains(INVALID_DEVELOPER_NAME));
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test update developer with duplicate developer name and expect 400")
    void testUpdateDeveloperDuplicateDeveloperName() throws Exception {
        DeveloperPublicDto developerPublicDto = createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));
        createDeveloper("Developer2", List.of("twitter.com/developer", "github.com/developer"));
        DeveloperCreateDto developerCreateDto = new DeveloperCreateDto("Developer2", List.of("twitter.com/developer", "github.com/developer"));

        String response = putRequest(ADMIN_API_PATH + "/" + developerPublicDto.id(), status().isBadRequest(), mapper.writeValueAsString(developerCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        Developer developer = developerRepository.findById(developerPublicDto.id()).orElse(null);
        assertNotNull(developer);
        assertNotEquals(developerCreateDto.developerName(), developer.getDeveloperName());

        assertEquals(DEVELOPER_ALREADY_EXISTS + developerCreateDto.developerName(), errorDto.message());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test update developer not found and expect 404")
    void testUpdateDeveloperNotFound() throws Exception {
        DeveloperCreateDto developerCreateDto = new DeveloperCreateDto("Developer", List.of("twitter.com/developer", "github.com/developer"));

        String response = putRequest(ADMIN_API_PATH + "/1", status().isNotFound(), mapper.writeValueAsString(developerCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, developerRepository.count());
        assertEquals(DEVELOPER_NOT_FOUND + 1, errorDto.message());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test delete developer and expect 204")
    void testDeleteDeveloper() throws Exception {
        DeveloperPublicDto developerPublicDto = createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));

        deleteRequest(ADMIN_API_PATH + "/" + developerPublicDto.id(), status().isNoContent(), mockMvc);

        assertEquals(0, developerRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test delete developer without authorization and expect 403")
    void testDeleteDeveloperUnauthorized() throws Exception {
        DeveloperPublicDto developerPublicDto = createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));

        deleteRequest(ADMIN_API_PATH + "/" + developerPublicDto.id(), status().isForbidden(), mockMvc);

        assertEquals(1, developerRepository.count());
    }

    @Test
    @DisplayName("Test delete developer without authentication and expect 401")
    void testDeleteDeveloperUnauthenticated() throws Exception {
        DeveloperPublicDto developerPublicDto = createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));

        deleteRequest(ADMIN_API_PATH + "/" + developerPublicDto.id(), status().isUnauthorized(), mockMvc);

        assertEquals(1, developerRepository.count());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test delete developer not found and expect 404")
    void testDeleteDeveloperNotFound() throws Exception {
        deleteRequest(ADMIN_API_PATH + "/1", status().isNotFound(), mockMvc);
    }

}
