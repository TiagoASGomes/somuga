//package org.somuga.user;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.somuga.aspect.Error;
//import org.somuga.dto.user.UserCreateDto;
//import org.somuga.dto.user.UserPublicDto;
//import org.somuga.dto.user.UserUpdateNameDto;
//import org.somuga.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Date;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.somuga.util.message.Messages.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//public class UserControllerTest {
//
//    private static final ObjectMapper mapper = new ObjectMapper();
//    private final String USERNAME = "UserName";
//    private final String EMAIL = "email@example.com";
//    private final String API_PATH = "/api/v1/user";
//    private final UserCreateDto userCreateDto = new UserCreateDto(USERNAME, EMAIL);
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private UserRepository userTestRepository;
//
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
//    public long createUser(String name, String email) throws Exception {
//        UserCreateDto userDto = new UserCreateDto(name, email);
//
//        String response = mockMvc.perform(post(API_PATH)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(userDto)))
//                .andExpect(status().isCreated())
//                .andReturn().getResponse().getContentAsString();
//
//        return mapper.readValue(response, UserPublicDto.class).id();
//    }
//
//    @Test
//    @DisplayName("Test create user and expect status 201 and user dto")
//    void testCreate() throws Exception {
//        Date prev = new Date();
//
//        String response = mockMvc.perform(post(API_PATH)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(userCreateDto)))
//                .andExpect(status().isCreated())
//                .andReturn().getResponse().getContentAsString();
//
//        Date after = new Date();
//
//        UserPublicDto user = mapper.readValue(response, UserPublicDto.class);
//        assertEquals(USERNAME, user.userName());
//        assertEquals(EMAIL, user.email());
//        assertNotNull(user.id());
//        assertTrue(user.joinedDate().after(prev) && after.after(user.joinedDate()));
//    }
//
//    @Test
//    @DisplayName("Test create user with invalid body and expect status 400 and error message")
//    void testCreateInvalidBody() throws Exception {
//        UserCreateDto user = new UserCreateDto("ABC", "INVALID");
//
//        String response = mockMvc.perform(post(API_PATH)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(user)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(400, error.getStatus());
//        assertTrue(error.getMessage().contains(INVALID_USERNAME));
//        assertTrue(error.getMessage().contains(INVALID_EMAIL));
//        assertEquals(API_PATH, error.getPath());
//        assertEquals("POST", error.getMethod());
//    }
//
//    @Test
//    @DisplayName("Test create user with empty fields and expect status 400 and error message")
//    void testCreateEmptyFields() throws Exception {
//        UserCreateDto user = new UserCreateDto("", "");
//
//        String response = mockMvc.perform(post(API_PATH)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(user)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(400, error.getStatus());
//        assertTrue(error.getMessage().contains(NON_EMPTY_USERNAME));
//        assertTrue(error.getMessage().contains(NON_EMPTY_EMAIL));
//        assertEquals(API_PATH, error.getPath());
//        assertEquals("POST", error.getMethod());
//    }
//
//    @Test
//    @DisplayName("Test create user with empty body and expect status 400 and error message")
//    void testCreateEmptyBody() throws Exception {
//
//        mockMvc.perform(post(API_PATH)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(""))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Test create user with duplicate userName and expect status 400 and error message")
//    void testCreateDuplicateName() throws Exception {
//        createUser(USERNAME, "email2@example.com");
//
//        String response = mockMvc.perform(post(API_PATH)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(userCreateDto)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(DUPLICATE_USERNAME + USERNAME, error.getMessage());
//        assertEquals(400, error.getStatus());
//        assertEquals(API_PATH, error.getPath());
//        assertEquals("POST", error.getMethod());
//    }
//
//    @Test
//    @DisplayName("Test create user with duplicate email and expect status 400 and error message")
//    void testCreateDuplicateEmail() throws Exception {
//        createUser("userName2", EMAIL);
//
//        String response = mockMvc.perform(post(API_PATH)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(userCreateDto)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(DUPLICATE_EMAIL + EMAIL, error.getMessage());
//        assertEquals(400, error.getStatus());
//        assertEquals(API_PATH, error.getPath());
//        assertEquals("POST", error.getMethod());
//    }
//
//    @Test
//    @DisplayName("Test get all users with 2 users present and expect status 200 and 2 users")
//    void testGetAll2Users() throws Exception {
//        for (int i = 0; i < 2; i++) {
//            createUser(USERNAME + i, "email" + i + "@example.com");
//        }
//
//        mockMvc.perform(get(API_PATH))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(2)));
//    }
//
//    @Test
//    @DisplayName("Test get all users with 0 users and expect status 200 and 0 users")
//    void testGetAllEmpty() throws Exception {
//        mockMvc.perform(get(API_PATH))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(0)));
//    }
//
//    @Test
//    @DisplayName("Test get all users with 5 and a page limit of 3 and expect 3 users in page 0 and 2 users in page 1")
//    void testGetAllPaged() throws Exception {
//        for (int i = 0; i < 5; i++) {
//            createUser(USERNAME + i, "email" + i + "@example.com");
//        }
//        mockMvc.perform(get(API_PATH + "?page=0&size=3"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(3)));
//        mockMvc.perform(get(API_PATH + "?page=1&size=3"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(2)));
//    }
//
//    @Test
//    @DisplayName("Test get all with page size over 100 and expect list with 100 users")
//    void testGetAllPagedMaxSize() throws Exception {
//        for (int i = 0; i < 101; i++) {
//            createUser(USERNAME + i, "email" + i + "@example.com");
//        }
//        mockMvc.perform(get(API_PATH + "?page=0&size=101"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(100)));
//    }
//
//    @Test
//    @DisplayName("Test get all with 3 users and 1 deleted and expect page with 2 users")
//    void testGetAllWithDeletedUsers() throws Exception {
//        long id = 1L;
//        for (int i = 0; i < 3; i++) {
//            id = createUser(USERNAME + i, "email" + i + "@example.com");
//        }
//        mockMvc.perform(delete(API_PATH + "/" + id))
//                .andExpect(status().isNoContent());
//        mockMvc.perform(get(API_PATH))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(2)));
//    }
//
//    @Test
//    @DisplayName("Test get all with 5 users and 1 deleted with page sizes of 2")
//    void testGetAllWithDeletedUsersPaged() throws Exception {
//        long id = 1L;
//        for (int i = 0; i < 5; i++) {
//            id = createUser(USERNAME + i, "email" + i + "@example.com");
//        }
//        mockMvc.perform(delete(API_PATH + "/" + id))
//                .andExpect(status().isNoContent());
//        mockMvc.perform(get(API_PATH + "?page=0&size=4"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(4)));
//        mockMvc.perform(get(API_PATH + "?page=1&size=4"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(0)));
//    }
//
//    @Test
//    @DisplayName("Test get users all by fullName with 3 matched and expect list with 3 users")
//    void testGetAllByName() throws Exception {
//        for (int i = 0; i < 3; i++) {
//            createUser(USERNAME + i, "email" + i + "@example.com");
//        }
//        createUser("Teste", "teste@example.com");
//        mockMvc.perform(get(API_PATH + "/name/" + USERNAME))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(3)));
//    }
//
//    @Test
//    @DisplayName("Test get users all by fullName with 0 matches and expect empty list")
//    void testGetAllByNameNoFound() throws Exception {
//        for (int i = 0; i < 3; i++) {
//            createUser(USERNAME + i, "email" + i + "@example.com");
//        }
//        mockMvc.perform(get(API_PATH + "/name/NotFound"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(0)));
//    }
//
//    @Test
//    @DisplayName("Test get users all by fullName with 1 deleted user and expect list 2 users")
//    void testGetAllByNameWithDeleted() throws Exception {
//        long id = 1L;
//        for (int i = 0; i < 3; i++) {
//            id = createUser(USERNAME + i, "email" + i + "@example.com");
//        }
//        mockMvc.perform(delete(API_PATH + "/" + id))
//                .andExpect(status().isNoContent());
//        mockMvc.perform(get(API_PATH + "/name/" + USERNAME))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(2)));
//    }
//
//    @Test
//    @DisplayName("Test get user by id and expect status 200 and user")
//    void testGetById() throws Exception {
//        long id = createUser(USERNAME, EMAIL);
//
//        String response = mockMvc.perform(get(API_PATH + "/" + id))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        UserPublicDto user = mapper.readValue(response, UserPublicDto.class);
//
//        assertEquals(id, user.id());
//        assertEquals(EMAIL, user.email());
//        assertEquals(USERNAME, user.userName());
//        assertNotNull(user.joinedDate());
//    }
//
//    @Test
//    @DisplayName("Test get user by id with not existing id and expect status 404 and message")
//    void testGetByIdNotFound() throws Exception {
//
//        String response = mockMvc.perform(get(API_PATH + "/" + 1))
//                .andExpect(status().isNotFound())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(USER_NOT_FOUND + 1, error.getMessage());
//        assertEquals(404, error.getStatus());
//        assertEquals(API_PATH + "/" + 1, error.getPath());
//        assertEquals("GET", error.getMethod());
//        assertNotNull(error.getTimestamp());
//    }
//
//    @Test
//    @DisplayName("Test update username and expect status 200 and updated user")
//    void testUpdateUserName() throws Exception {
//        long id = createUser(USERNAME, EMAIL);
//        UserUpdateNameDto updateNameDto = new UserUpdateNameDto("NewName");
//
//        String response = mockMvc.perform(patch(API_PATH + "/" + id)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(updateNameDto)))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        UserPublicDto user = mapper.readValue(response, UserPublicDto.class);
//
//        assertEquals(id, user.id());
//        assertEquals("NewName", user.userName());
//        assertEquals(EMAIL, user.email());
//    }
//
//    @Test
//    @DisplayName("Test update username with not existing user and expect status 404 and message")
//    void testUpdateUserNameNotFound() throws Exception {
//        UserUpdateNameDto updateNameDto = new UserUpdateNameDto("NewName");
//
//        String response = mockMvc.perform(patch(API_PATH + "/" + 1)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(updateNameDto)))
//                .andExpect(status().isNotFound())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(USER_NOT_FOUND + 1, error.getMessage());
//        assertEquals(404, error.getStatus());
//        assertEquals(API_PATH + "/" + 1, error.getPath());
//        assertEquals("PATCH", error.getMethod());
//        assertNotNull(error.getTimestamp());
//    }
//
//    @Test
//    @DisplayName("Test update username with duplicate fullName and expect status 400 and message")
//    void testUpdateUserNameDuplicateName() throws Exception {
//        long id = createUser(USERNAME, EMAIL);
//        createUser("NewName", "newemail@example.com");
//        UserUpdateNameDto updateNameDto = new UserUpdateNameDto("NewName");
//
//        String response = mockMvc.perform(patch(API_PATH + "/" + id)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(updateNameDto)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(DUPLICATE_USERNAME + "NewName", error.getMessage());
//        assertEquals(400, error.getStatus());
//        assertEquals(API_PATH + "/" + id, error.getPath());
//        assertEquals("PATCH", error.getMethod());
//        assertNotNull(error.getTimestamp());
//    }
//
//    @Test
//    @DisplayName("Test delete user and expect status 204 and deleted user")
//    void testDelete() throws Exception {
//        long id = createUser(USERNAME, EMAIL);
//
//        mockMvc.perform(delete(API_PATH + "/" + id))
//                .andExpect(status().isNoContent());
//
//        String response = mockMvc.perform(get(API_PATH + "/" + id))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        UserPublicDto user = mapper.readValue(response, UserPublicDto.class);
//
//        assertEquals(id, user.id());
//        assertEquals("", user.email());
//        assertEquals("", user.userName());
//        assertNotNull(user.joinedDate());
//    }
//
//    @Test
//    @DisplayName("Test delete user with not existing user and expect status 404 and message")
//    void testDeleteNotFound() throws Exception {
//
//        String response = mockMvc.perform(delete(API_PATH + "/" + 1))
//                .andExpect(status().isNotFound())
//                .andReturn().getResponse().getContentAsString();
//
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(USER_NOT_FOUND + 1, error.getMessage());
//        assertEquals(404, error.getStatus());
//        assertEquals(API_PATH + "/" + 1, error.getPath());
//        assertEquals("DELETE", error.getMethod());
//        assertNotNull(error.getTimestamp());
//    }
//
//}
