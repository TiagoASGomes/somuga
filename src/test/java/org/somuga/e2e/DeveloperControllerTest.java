//package org.somuga.e2e;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.somuga.aspect.Error;
//import org.somuga.converter.DeveloperConverter;
//import org.somuga.dto.developer.DeveloperCreateDto;
//import org.somuga.dto.developer.DeveloperPublicDto;
//import org.somuga.entity.Developer;
//import org.somuga.repository.DeveloperRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.util.List;
//
//import static org.junit.Assert.assertTrue;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.somuga.util.message.Messages.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@ContextConfiguration
//@ActiveProfiles("test")
//class DeveloperControllerTest {
//
//    private static final ObjectMapper mapper = new ObjectMapper();
//    private final String USER = "google-auth2|1234567890";
//    private final String PRIVATE_API_PATH = "/api/v1/developer/private";
//    private final String PUBLIC_API_PATH = "/api/v1/developer/public";
//    MockMvc mockMvc;
//    @Autowired
//    private DeveloperRepository developerRepository;
//    @Autowired
//    private WebApplicationContext controller;
//    @MockBean
//    @SuppressWarnings("unused")
//    private JwtDecoder jwtDecoder;
//
//    @BeforeEach
//    public void setup() {
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(controller)
//                .apply(springSecurity())
//                .build();
//    }
//
//    @AfterEach
//    public void cleanUp() {
//        developerRepository.deleteAll();
//    }
//
//    public DeveloperPublicDto createDeveloper(String name, List<String> socials) {
//        Developer developer = new Developer(name, socials, USER);
//
//        return DeveloperConverter.fromEntityToPublicDto(developerRepository.save(developer));
//    }
//
//    @Test
//    @WithMockUser(username = USER)
//    @DisplayName("Test create developer and expect 201")
//    void testCreateDeveloperAuthorized() throws Exception {
//        DeveloperCreateDto developer = new DeveloperCreateDto("Developer", List.of("twitter.com/developer", "github.com/developer"));
//
//        String response = mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(developer)))
//                .andExpect(status().isCreated())
//                .andReturn().getResponse().getContentAsString();
//
//        DeveloperPublicDto developerPublicDto = mapper.readValue(response, DeveloperPublicDto.class);
//
//        assertEquals(developer.developerName(), developerPublicDto.developerName());
//        assertNotNull(developerPublicDto.id());
//    }
//
//    @Test
//    @DisplayName("Test create developer without authorization and expect 401")
//    void testCreateDeveloperUnauthorized() throws Exception {
//        DeveloperCreateDto developer = new DeveloperCreateDto("Developer", List.of("twitter.com/developer", "github.com/developer"));
//
//        mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(developer)))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    @WithMockUser(username = USER)
//    @DisplayName("Test create developer with empty fullName and expect 400")
//    void testCreateDeveloperEmptyName() throws Exception {
//        DeveloperCreateDto developer = new DeveloperCreateDto("", List.of("twitter.com/developer", "github.com/developer"));
//
//        String response = mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(developer)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertTrue(error.getMessage().contains(INVALID_DEVELOPER_NAME));
//        assertEquals(400, error.getStatus());
//        assertEquals("POST", error.getMethod());
//        assertEquals(PRIVATE_API_PATH, error.getPath());
//    }
//
//    @Test
//    @WithMockUser(username = USER)
//    @DisplayName("Test create developer with null fullName and expect 400")
//    void testCreateDeveloperNullName() throws Exception {
//        DeveloperCreateDto developer = new DeveloperCreateDto(null, List.of("twitter.com/developer", "github.com/developer"));
//
//        String response = mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(developer)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertTrue(error.getMessage().contains(INVALID_DEVELOPER_NAME));
//        assertEquals(400, error.getStatus());
//        assertEquals("POST", error.getMethod());
//        assertEquals(PRIVATE_API_PATH, error.getPath());
//    }
//
//    @Test
//    @WithMockUser(username = USER)
//    @DisplayName("Test create developer with fullName containing special characters and expect 400")
//    void testCreateDeveloperSpecialCharacters() throws Exception {
//        DeveloperCreateDto developer = new DeveloperCreateDto("Developer!", List.of("twitter.com/developer", "github.com/developer"));
//
//        String response = mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(developer)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertTrue(error.getMessage().contains(INVALID_DEVELOPER_NAME));
//        assertEquals(400, error.getStatus());
//        assertEquals("POST", error.getMethod());
//        assertEquals(PRIVATE_API_PATH, error.getPath());
//    }
//
//    @Test
//    @WithMockUser(username = USER)
//    @DisplayName("Test create duplicate developer and expect 400")
//    void testCreateDuplicateDeveloper() throws Exception {
//        createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));
//
//        DeveloperCreateDto developer = new DeveloperCreateDto("Developer", List.of("twitter.com/developer", "github.com/developer"));
//
//        String response = mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(developer)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertTrue(error.getMessage().contains(DEVELOPER_ALREADY_EXISTS + developer.developerName()));
//        assertEquals(400, error.getStatus());
//        assertEquals("POST", error.getMethod());
//        assertEquals(PRIVATE_API_PATH, error.getPath());
//    }
//
//    @Test
//    @DisplayName("Test get all developers and expect 200")
//    void testGetAllDevelopers() throws Exception {
//        createDeveloper("Developer1", List.of("twitter.com/developer1", "github.com/developer1"));
//        createDeveloper("Developer2", List.of("twitter.com/developer2", "github.com/developer2"));
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        DeveloperPublicDto[] developers = mapper.readValue(response, DeveloperPublicDto[].class);
//
//        assertEquals(2, developers.length);
//    }
//
//    @Test
//    @DisplayName("Test get all developers with pagination and expect 200")
//    void testGetAllDevelopersWithPagination() throws Exception {
//        createDeveloper("Developer1", List.of("twitter.com/developer1", "github.com/developer1"));
//        createDeveloper("Developer2", List.of("twitter.com/developer2", "github.com/developer2"));
//        createDeveloper("Developer3", List.of("twitter.com/developer3", "github.com/developer3"));
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?page=0&size=2")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        DeveloperPublicDto[] developers = mapper.readValue(response, DeveloperPublicDto[].class);
//
//        assertEquals(2, developers.length);
//    }
//
//    @Test
//    @DisplayName("Test get developer by id and expect 200")
//    void testGetDeveloperById() throws Exception {
//        DeveloperPublicDto developer = createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + developer.id())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        DeveloperPublicDto developerPublicDto = mapper.readValue(response, DeveloperPublicDto.class);
//
//        assertEquals(developer.developerName(), developerPublicDto.developerName());
//        assertEquals(developer.id(), developerPublicDto.id());
//    }
//
//    @Test
//    @DisplayName("Test get developer by id that does not exist and expect 404")
//    void testGetDeveloperByIdNotFound() throws Exception {
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/9999999")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertTrue(error.getMessage().contains(DEVELOPER_NOT_FOUND + 9999999));
//        assertEquals(404, error.getStatus());
//        assertEquals("GET", error.getMethod());
//        assertEquals(PUBLIC_API_PATH + "/9999999", error.getPath());
//    }
//
//    @Test
//    @DisplayName("Test get developer by fullName and expect 200")
//    void testGetDeveloperByName() throws Exception {
//        createDeveloper("Developer1", List.of("twitter.com/developer1", "github.com/developer1"));
//        createDeveloper("Developer2", List.of("twitter.com/developer2", "github.com/developer2"));
//        createDeveloper("Developer3", List.of("twitter.com/developer3", "github.com/developer3"));
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/search/Developer")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        DeveloperPublicDto[] developers = mapper.readValue(response, DeveloperPublicDto[].class);
//
//        assertEquals(3, developers.length);
//    }
//
//    @Test
//    @DisplayName("Test get developer by fullName that does not exist and expect 200")
//    void testGetDeveloperByNameNotFound() throws Exception {
//        createDeveloper("Developer1", List.of("twitter.com/developer1", "github.com/developer1"));
//        createDeveloper("Developer2", List.of("twitter.com/developer2", "github.com/developer2"));
//        createDeveloper("Developer3", List.of("twitter.com/developer3", "github.com/developer3"));
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/search/Developer4")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        DeveloperPublicDto[] developers = mapper.readValue(response, DeveloperPublicDto[].class);
//
//        assertEquals(0, developers.length);
//    }
//
//    @Test
//    @DisplayName("Test get developer by fullName with pagination and expect 200")
//    void testGetDeveloperByNameWithPagination() throws Exception {
//        createDeveloper("Developer1", List.of("twitter.com/developer1", "github.com/developer1"));
//        createDeveloper("Developer2", List.of("twitter.com/developer2", "github.com/developer2"));
//        createDeveloper("Developer3", List.of("twitter.com/developer3", "github.com/developer3"));
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/search/Developer?page=0&size=2")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        DeveloperPublicDto[] developers = mapper.readValue(response, DeveloperPublicDto[].class);
//
//        assertEquals(2, developers.length);
//    }
//
//    @Test
//    @WithMockUser(username = USER)
//    @DisplayName("Test update developer and expect 200")
//    void testUpdateDeveloper() throws Exception {
//        DeveloperPublicDto developer = createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));
//
//        DeveloperCreateDto developerUpdate = new DeveloperCreateDto("DeveloperUpdated", List.of("twitter.com/developer", "github.com/developer"));
//
//        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/" + developer.id())
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(developerUpdate)))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        DeveloperPublicDto developerPublicDto = mapper.readValue(response, DeveloperPublicDto.class);
//
//        assertEquals(developerUpdate.developerName(), developerPublicDto.developerName());
//        assertEquals(developer.id(), developerPublicDto.id());
//    }
//
//    @Test
//    @DisplayName("Test update developer without authorization and expect 401")
//    void testUpdateDeveloperUnauthorized() throws Exception {
//        DeveloperCreateDto developer = new DeveloperCreateDto("Developer", List.of("twitter.com/developer", "github.com/developer"));
//
//        mockMvc.perform(put(PRIVATE_API_PATH + "/1")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(developer)))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    @WithMockUser(username = "different-user|1234567890")
//    @DisplayName("Test update developer created by different user and expect 403")
//    void testUpdateDeveloperDifferentUser() throws Exception {
//        DeveloperPublicDto developer = createDeveloper("Developer", List.of("twitter.com/developer", "github.com/developer"));
//
//        DeveloperCreateDto developerUpdate = new DeveloperCreateDto("DeveloperUpdated", List.of("twitter.com/developer", "github.com/developer"));
//
//        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/" + developer.id())
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(developerUpdate)))
//                .andExpect(status().isForbidden())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(UNAUTHORIZED_UPDATE, error.getMessage());
//        assertEquals(403, error.getStatus());
//        assertEquals("PUT", error.getMethod());
//        assertEquals(PRIVATE_API_PATH + "/" + developer.id(), error.getPath());
//    }
//
//
//}
