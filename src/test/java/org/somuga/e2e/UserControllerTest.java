//package org.somuga.e2e;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.junit.jupiter.api.*;
//import org.somuga.aspect.Error;
//import org.somuga.converter.UserConverter;
//import org.somuga.dto.user.UserCreateDto;
//import org.somuga.dto.user.UserPublicDto;
//import org.somuga.entity.User;
//import org.somuga.repository.UserRepository;
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
//import java.util.Date;
//import java.util.List;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.somuga.util.message.Messages.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@ContextConfiguration
//@ActiveProfiles("test")
//public class UserControllerTest {
//
//    private static final ObjectMapper mapper = new ObjectMapper();
//    private final String USER_ID = "google-auth2|1234567890";
//    private final String PRIVATE_API_PATH = "/api/v1/user/private";
//    private final String PUBLIC_API_PATH = "/api/v1/user/public";
//    private final String USERNAME = "UserName";
//    private final UserCreateDto userCreateDto = new UserCreateDto(USERNAME);
//    MockMvc mockMvc;
//    @Autowired
//    private UserRepository userTestRepository;
//    @Autowired
//    private WebApplicationContext controller;
//    @MockBean
//    @SuppressWarnings("unused")
//    private JwtDecoder jwtDecoder;
//
//    @BeforeAll
//    public static void setUpMapper() {
//        mapper.registerModule(new JavaTimeModule());
//    }
//
//    @AfterEach
//    public void cleanUp() {
//        userTestRepository.deleteAll();
//    }
//
//    @BeforeEach
//    public void setUp() {
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(controller)
//                .apply(springSecurity())
//                .build();
//    }
//
//    public UserPublicDto createUser(String id, String name) {
//        User user = new User(id, name);
//        user.setJoinDate(new Date());
//        user.setActive(true);
//        return UserConverter.fromEntityToPublicDto(userTestRepository.save(user));
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create user and expect status 201 and user dto")
//    void testCreate() throws Exception {
//        String response = mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(userCreateDto)))
//                .andExpect(status().isCreated())
//                .andReturn().getResponse().getContentAsString();
//
//        UserPublicDto user = mapper.readValue(response, UserPublicDto.class);
//        assertEquals(USERNAME, user.userName());
//        assertEquals(USER_ID, user.id());
//        assertNotNull(user.joinedDate());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create user with invalid body and expect status 400 and error message")
//    void testCreateInvalidBody() throws Exception {
//        UserCreateDto user = new UserCreateDto("ABC");
//
//        String response = mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(user)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(400, error.getStatus());
//        assertTrue(error.getMessage().contains(INVALID_USERNAME));
//        assertEquals(PRIVATE_API_PATH, error.getPath());
//        assertEquals("POST", error.getMethod());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create user with empty fields and expect status 400 and error message")
//    void testCreateEmptyFields() throws Exception {
//        UserCreateDto user = new UserCreateDto("");
//
//        String response = mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(user)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(400, error.getStatus());
//        assertTrue(error.getMessage().contains(NON_EMPTY_USERNAME));
//        assertEquals(PRIVATE_API_PATH, error.getPath());
//        assertEquals("POST", error.getMethod());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create user with empty body and expect status 400 and error message")
//    void testCreateEmptyBody() throws Exception {
//
//        mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(""))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID + 1)
//    @DisplayName("Test create user with duplicate userName and expect status 400 and error message")
//    void testCreateDuplicateName() throws Exception {
//        createUser(USER_ID, USERNAME);
//
//        List<User> users = userTestRepository.findAll();
//
//        String response = mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(userCreateDto)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(DUPLICATE_USERNAME + USERNAME, error.getMessage());
//        assertEquals(400, error.getStatus());
//        assertEquals(PRIVATE_API_PATH, error.getPath());
//        assertEquals("POST", error.getMethod());
//    }
//
//
//    @Test
//    @DisplayName("Test get all users with 2 users present and expect status 200 and 2 users")
//    void testGetAll2Users() throws Exception {
//        for (int i = 0; i < 2; i++) {
//            createUser(USER_ID + i, USERNAME + i);
//        }
//
//        mockMvc.perform(get(PUBLIC_API_PATH))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(2)));
//    }
//
//    @Test
//    @DisplayName("Test get all users with 0 users and expect status 200 and 0 users")
//    void testGetAllEmpty() throws Exception {
//        mockMvc.perform(get(PUBLIC_API_PATH))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(0)));
//    }
//
//    @Test
//    @DisplayName("Test get all users with 5 and a page limit of 3 and expect 3 users in page 0 and 2 users in page 1")
//    void testGetAllPaged() throws Exception {
//        for (int i = 0; i < 5; i++) {
//            createUser(USER_ID + i, USERNAME + i);
//        }
//        mockMvc.perform(get(PUBLIC_API_PATH + "?page=0&size=3"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(3)));
//        mockMvc.perform(get(PUBLIC_API_PATH + "?page=1&size=3"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(2)));
//    }
//
//    @Test
//    @DisplayName("Test get all with page size over 100 and expect list with 100 users")
//    void testGetAllPagedMaxSize() throws Exception {
//        for (int i = 0; i < 101; i++) {
//            createUser(USER_ID + i, USERNAME + i);
//        }
//        mockMvc.perform(get(PUBLIC_API_PATH + "?page=0&size=101"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(100)));
//    }
//
//    @Test
//    @DisplayName("Test get all with 3 users and 1 deleted and expect page with 2 users")
//    void testGetAllWithDeletedUsers() throws Exception {
//        String id = "";
//        for (int i = 0; i < 3; i++) {
//            id = createUser(USER_ID + i, USERNAME + i).id();
//        }
//        userTestRepository.deleteById(id);
//        mockMvc.perform(get(PUBLIC_API_PATH))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(2)));
//    }
//
//    @Test
//    @DisplayName("Test get all with 5 users and 1 deleted with page sizes of 2")
//    void testGetAllWithDeletedUsersPaged() throws Exception {
//        String id = "";
//        for (int i = 0; i < 5; i++) {
//            id = createUser(USER_ID + i, USERNAME + i).id();
//        }
//        userTestRepository.deleteById(id);
//        mockMvc.perform(get(PUBLIC_API_PATH + "?page=0&size=4"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(4)));
//        mockMvc.perform(get(PUBLIC_API_PATH + "?page=1&size=4"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(0)));
//    }
//
//    @Test
//    @DisplayName("Test get users all by fullName with 3 matched and expect list with 3 users")
//    void testGetAllByName() throws Exception {
//        for (int i = 0; i < 3; i++) {
//            createUser(USER_ID + i, USERNAME + i);
//        }
//        createUser(USER_ID + "A", "Teste");
//        mockMvc.perform(get(PUBLIC_API_PATH + "/name/" + USERNAME))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(3)));
//    }
//
//    @Test
//    @DisplayName("Test get users all by fullName with 0 matches and expect empty list")
//    void testGetAllByNameNoFound() throws Exception {
//        for (int i = 0; i < 3; i++) {
//            createUser(USER_ID + i, USERNAME + i);
//        }
//        mockMvc.perform(get(PUBLIC_API_PATH + "/name/NotFound"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(0)));
//    }
//
//    @Test
//    @DisplayName("Test get users all by fullName with 1 deleted user and expect list 2 users")
//    void testGetAllByNameWithDeleted() throws Exception {
//        String id = "";
//        for (int i = 0; i < 3; i++) {
//            id = createUser(USER_ID + i, USERNAME + i).id();
//        }
//        userTestRepository.deleteById(id);
//        mockMvc.perform(get(PUBLIC_API_PATH + "/name/" + USERNAME))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(2)));
//    }
//
//    @Test
//    @DisplayName("Test get user by id and expect status 200 and user")
//    void testGetById() throws Exception {
//        String id = createUser(USER_ID, USERNAME).id();
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + id))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        UserPublicDto user = mapper.readValue(response, UserPublicDto.class);
//
//        assertEquals(id, user.id());
//        assertEquals(USERNAME, user.userName());
//        assertNotNull(user.joinedDate());
//    }
//
//    @Test
//    @DisplayName("Test get user by id with not existing id and expect status 404 and message")
//    void testGetByIdNotFound() throws Exception {
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + 1))
//                .andExpect(status().isNotFound())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(USER_NOT_FOUND + 1, error.getMessage());
//        assertEquals(404, error.getStatus());
//        assertEquals(PUBLIC_API_PATH + "/" + 1, error.getPath());
//        assertEquals("GET", error.getMethod());
//        assertNotNull(error.getTimestamp());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test update username and expect status 200 and updated user")
//    void testUpdateUserName() throws Exception {
//        createUser(USER_ID, USERNAME);
//        UserCreateDto updateNameDto = new UserCreateDto("NewName");
//
//        String response = mockMvc.perform(put(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(updateNameDto)))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        UserPublicDto user = mapper.readValue(response, UserPublicDto.class);
//
//        assertEquals(USER_ID, user.id());
//        assertEquals("NewName", user.userName());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test update username with not existing user and expect status 404 and message")
//    void testUpdateUserNameNotFound() throws Exception {
//        UserCreateDto updateNameDto = new UserCreateDto("NewName");
//
//        String response = mockMvc.perform(put(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(updateNameDto)))
//                .andExpect(status().isNotFound())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(USER_NOT_FOUND + USER_ID, error.getMessage());
//        assertEquals(404, error.getStatus());
//        assertEquals(PRIVATE_API_PATH, error.getPath());
//        assertEquals("PUT", error.getMethod());
//        assertNotNull(error.getTimestamp());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test update username with duplicate fullName and expect status 400 and message")
//    void testUpdateUserNameDuplicateName() throws Exception {
//        createUser(USER_ID, USERNAME);
//        createUser(USER_ID + 1, "NewName");
//        UserCreateDto updateNameDto = new UserCreateDto("NewName");
//
//        String response = mockMvc.perform(put(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(updateNameDto)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(DUPLICATE_USERNAME + "NewName", error.getMessage());
//        assertEquals(400, error.getStatus());
//        assertEquals(PRIVATE_API_PATH, error.getPath());
//        assertEquals("PUT", error.getMethod());
//        assertNotNull(error.getTimestamp());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test delete user and expect status 204 and deleted user")
//    void testDelete() throws Exception {
//        createUser(USER_ID, USERNAME);
//
//        mockMvc.perform(delete(PRIVATE_API_PATH)
//                        .with(csrf()))
//                .andExpect(status().isNoContent());
//
//        mockMvc.perform(get(PUBLIC_API_PATH + "/" + USER_ID))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test delete user with not existing user and expect status 404 and message")
//    void testDeleteNotFound() throws Exception {
//
//        String response = mockMvc.perform(delete(PRIVATE_API_PATH)
//                        .with(csrf()))
//                .andExpect(status().isNotFound())
//                .andReturn().getResponse().getContentAsString();
//
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(USER_NOT_FOUND + USER_ID, error.getMessage());
//        assertEquals(404, error.getStatus());
//        assertEquals(PRIVATE_API_PATH, error.getPath());
//        assertEquals("DELETE", error.getMethod());
//        assertNotNull(error.getTimestamp());
//    }
//
//}
