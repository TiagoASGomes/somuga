package org.somuga.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.somuga.aspect.ErrorDto;
import org.somuga.converter.UserConverter;
import org.somuga.dto.user.UserCreateDto;
import org.somuga.dto.user.UserListDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.entity.User;
import org.somuga.repository.UserRepository;
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.somuga.testUtils.Utils.*;
import static org.somuga.util.message.Messages.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
public class UserE2ETest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String USER_ID = "google-auth2|1234567890";
    private final String PRIVATE_API_PATH = "/api/v1/user/private";
    private final String PUBLIC_API_PATH = "/api/v1/user/public";
    private final String ADMIN_API_PATH = "/api/v1/user/admin";
    private final String USERNAME = "UserName";
    MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebApplicationContext controller;
    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @BeforeAll
    public static void setUpMapper() {
        mapper.registerModule(new JavaTimeModule());
    }

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(controller)
                .apply(springSecurity())
                .build();
    }

    public UserPublicDto createUser(String id, String name, boolean active, String email) {
        User user = User.builder()
                .id(id)
                .userName(name)
                .email(email)
                .joinDate(LocalDate.now())
                .active(active)
                .build();
        return UserConverter.fromEntityToPublicDto(userRepository.save(user));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create user and expect status 201")
    void testCreate() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto(USERNAME, "email@example.com");

        String response = postRequest(PRIVATE_API_PATH, status().isCreated(), mapper.writeValueAsString(userCreateDto), mockMvc);

        UserPublicDto user = mapper.readValue(response, UserPublicDto.class);

        assertEquals(1, userRepository.count());
        User dbUser = userRepository.findById(USER_ID).orElse(null);
        assertNotNull(dbUser);
        assertEquals(USERNAME, user.userName(), dbUser.getUserName());
        assertEquals(USER_ID, user.id(), dbUser.getId());
        assertEquals(user.joinedDate(), dbUser.getJoinDate());
        assertTrue(dbUser.isActive());
    }

    @Test
    @DisplayName("Test create user without authentication and expect status 401")
    void testCreateWithoutAuthentication() throws Exception {
        postRequest(PRIVATE_API_PATH, status().isUnauthorized(), mapper.writeValueAsString(new UserCreateDto(USERNAME, "email@example.com")), mockMvc);

        assertEquals(0, userRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create user with duplicate username and expect status 400")
    void testCreateWithDuplicateUsername() throws Exception {
        createUser("different", USERNAME, true, "email2@example.com");
        UserCreateDto userCreateDto = new UserCreateDto(USERNAME, "email@example.com");

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(userCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(1, userRepository.count());
        assertEquals(DUPLICATE_USERNAME + USERNAME, errorDto.message());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create user with duplicate id and expect status 400")
    void testCreateWithDuplicateId() throws Exception {
        createUser(USER_ID, USERNAME, true, "email2@example.com");
        UserCreateDto userCreateDto = new UserCreateDto("different", "email@example.com");

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(userCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(1, userRepository.count());
        assertEquals(DUPLICATE_USER + USER_ID, errorDto.message());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create user with username exceeding 20 characters and expect status 400")
    void testCreateWithInvalidUsername() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto("ABCDEF".repeat(5), "email@example.com");

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(userCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, userRepository.count());
        assertTrue(errorDto.message().contains(INVALID_USERNAME));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create user with empty username and expect status 400")
    void testCreateWithEmptyUsername() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto("", "email@example.com");

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(userCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, userRepository.count());
        assertTrue(errorDto.message().contains(NON_EMPTY_USERNAME));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create user with null username and expect status 400")
    void testCreateWithNullUsername() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto(null, "email@example.com");

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(userCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, userRepository.count());
        assertTrue(errorDto.message().contains(NON_EMPTY_USERNAME));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create user with inactive user with same id and expect status 400")
    void testCreateWithInactiveUser() throws Exception {
        createUser(USER_ID, USERNAME, false, "email@example.com");
        UserCreateDto userCreateDto = new UserCreateDto(USERNAME, "email@example.com");

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(userCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(1, userRepository.count());
        assertEquals(DUPLICATE_USER + USER_ID, errorDto.message());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create user with duplicate name case insensitive and expect status 400")
    void testCreateWithDuplicateNameCaseInsensitive() throws Exception {
        createUser("different", USERNAME, true, "email2@example.com");
        UserCreateDto userCreateDto = new UserCreateDto(USERNAME.toLowerCase(), "email3@example.com");

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(userCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(1, userRepository.count());
        assertEquals(DUPLICATE_USERNAME + USERNAME.toLowerCase(), errorDto.message());
    }

    @Test
    @DisplayName("Test get all users and expect status 200")
    void testGetAll() throws Exception {
        UserPublicDto userPublicDto = createUser("1", "User1", true, "email@example.com");

        String response = getRequest(PUBLIC_API_PATH, status().isOk(), mockMvc);

        UserListDto users = mapper.readValue(response, UserListDto.class);

        assertEquals(1, users.users().size());
        assertEquals(1, users.count());
        assertEquals(userPublicDto.id(), users.users().get(0).id());
        assertEquals(userPublicDto.userName(), users.users().get(0).userName());
    }

    @Test
    @DisplayName("Test get all users with inactive user and expect status 200 with only active user")
    void testGetAllWithInactiveUser() throws Exception {
        createUser("1", "User1", false, "email@example.com");
        createUser("2", "User2", true, "email2@example.com");

        String response = getRequest(PUBLIC_API_PATH, status().isOk(), mockMvc);

        UserListDto users = mapper.readValue(response, UserListDto.class);

        assertEquals(1, users.users().size());
        assertEquals(1, users.count());
        assertEquals("2", users.users().get(0).id());
        assertEquals("User2", users.users().get(0).userName());
    }

    @Test
    @DisplayName("Test get all users with name and expect status 200")
    void testGetAllWithName() throws Exception {
        createUser("1", "User1", true, "email@example.com");
        createUser("2", "User2", true, "email2@example.com");
        createUser("3", "Different", true, "email3@example.com");

        String response = getRequest(PUBLIC_API_PATH + "?name=User", status().isOk(), mockMvc);

        UserListDto users = mapper.readValue(response, UserListDto.class);

        assertEquals(2, users.users().size());
        assertEquals(2, users.count());
    }

    @Test
    @DisplayName("Test get all users case insensitive and expect status 200")
    void testGetAllCaseInsensitive() throws Exception {
        createUser("1", "User1", true, "email@example.com");
        createUser("2", "User2", true, "email2@example.com");
        createUser("3", "Different", true, "email3@example.com");

        String response = getRequest(PUBLIC_API_PATH + "?name=user", status().isOk(), mockMvc);

        UserListDto users = mapper.readValue(response, UserListDto.class);

        assertEquals(2, users.users().size());
        assertEquals(2, users.count());
    }

    @Test
    @DisplayName("Test get all users with name and inactive users and expect status 200 with only active user")
    void testGetAllWithNameAndInactiveUser() throws Exception {
        createUser("1", "User1", false, "email@example.com");
        createUser("2", "User2", true, "email2@example.com");

        String response = getRequest(PUBLIC_API_PATH + "?name=User", status().isOk(), mockMvc);

        UserListDto users = mapper.readValue(response, UserListDto.class);

        assertEquals(1, users.users().size());
        assertEquals(1, users.count());
        assertEquals("2", users.users().get(0).id());
        assertEquals("User2", users.users().get(0).userName());
    }

    @Test
    @DisplayName("Test get by id and expect status 200")
    void testGetById() throws Exception {
        UserPublicDto userPublicDto = createUser("1", "User1", true, "email@example.com");

        String response = getRequest(PUBLIC_API_PATH + "/1", status().isOk(), mockMvc);

        UserPublicDto user = mapper.readValue(response, UserPublicDto.class);

        assertEquals(userPublicDto.id(), user.id());
        assertEquals(userPublicDto.userName(), user.userName());
    }

    @Test
    @DisplayName("Test get by id with inactive user and expect status 404")
    void testGetByIdWithInactiveUser() throws Exception {
        createUser("1", "User1", false, "email@example.com");

        String response = getRequest(PUBLIC_API_PATH + "/1", status().isNotFound(), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(USER_NOT_FOUND + "1", errorDto.message());
    }

    @Test
    @DisplayName("Test get by id with non existing user and expect status 404")
    void testGetByIdWithNonExistingUser() throws Exception {
        String response = getRequest(PUBLIC_API_PATH + "/1", status().isNotFound(), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(USER_NOT_FOUND + "1", errorDto.message());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update username and expect status 200")
    void testUpdateUsername() throws Exception {
        createUser(USER_ID, "User1", true, "email@example.com");
        UserCreateDto userCreateDto = new UserCreateDto("User2", "email@example.com");

        String response = putRequest(PRIVATE_API_PATH, status().isOk(), mapper.writeValueAsString(userCreateDto), mockMvc);

        UserPublicDto user = mapper.readValue(response, UserPublicDto.class);

        assertEquals(1, userRepository.count());
        User dbUser = userRepository.findById(USER_ID).orElse(null);
        assertNotNull(dbUser);
        assertEquals(user.userName(), dbUser.getUserName(), userCreateDto.userName());
        assertEquals(USER_ID, user.id(), dbUser.getId());
        assertEquals(user.joinedDate(), dbUser.getJoinDate());
        assertTrue(dbUser.isActive());
    }

    @Test
    @DisplayName("Test update username without authentication and expect status 401")
    void testUpdateUsernameWithoutAuthentication() throws Exception {
        createUser(USER_ID, "User1", true, "email@example.com");
        UserCreateDto userCreateDto = new UserCreateDto("User2", "email@example.com");

        putRequest(PRIVATE_API_PATH, status().isUnauthorized(), mapper.writeValueAsString(userCreateDto), mockMvc);

        User dbUser = userRepository.findById(USER_ID).orElse(null);
        assertNotNull(dbUser);
        assertNotEquals(userCreateDto.userName(), dbUser.getUserName());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update username with duplicate username and expect status 400")
    void testUpdateUsernameWithDuplicateUsername() throws Exception {
        createUser(USER_ID, "User1", true, "email@example.com");
        createUser("different", "User2", true, "email2@example.com");
        UserCreateDto userCreateDto = new UserCreateDto("User2", "email@example.com");

        String response = putRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(userCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);
        User dbUser = userRepository.findById(USER_ID).orElse(null);
        assertNotNull(dbUser);

        assertNotEquals(userCreateDto.userName(), dbUser.getUserName());

        assertEquals(2, userRepository.count());
        assertEquals(DUPLICATE_USERNAME + "User2", errorDto.message());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update username with duplicate name case insensitive and expect status 400")
    void testUpdateUsernameWithDuplicateNameCaseInsensitive() throws Exception {
        createUser(USER_ID, "User1", true, "email@example.com");
        createUser("different", "User2", true, "email2@example.com");
        UserCreateDto userCreateDto = new UserCreateDto("user2", "email@example.com");

        String response = putRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(userCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);
        User dbUser = userRepository.findById(USER_ID).orElse(null);
        assertNotNull(dbUser);

        assertNotEquals(userCreateDto.userName(), dbUser.getUserName());

        assertEquals(2, userRepository.count());
        assertEquals(DUPLICATE_USERNAME + "user2", errorDto.message());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update username with inactive user and expect status 404")
    void testUpdateUsernameWithInactiveUser() throws Exception {
        createUser(USER_ID, "User1", false, "email@example.com");
        UserCreateDto userCreateDto = new UserCreateDto("User2", "email@example.com");

        String response = putRequest(PRIVATE_API_PATH, status().isNotFound(), mapper.writeValueAsString(userCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);
        User dbUser = userRepository.findById(USER_ID).orElse(null);
        assertNotNull(dbUser);

        assertNotEquals(userCreateDto.userName(), dbUser.getUserName());

        assertEquals(1, userRepository.count());
        assertEquals(USER_NOT_FOUND + USER_ID, errorDto.message());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update username without user creation and expect status 404")
    void testUpdateUsernameWithoutUserCreation() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto("User2", "email@example.com");

        String response = putRequest(PRIVATE_API_PATH, status().isNotFound(), mapper.writeValueAsString(userCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, userRepository.count());
        assertEquals(USER_NOT_FOUND + USER_ID, errorDto.message());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete user and expect status 204")
    void testDelete() throws Exception {
        createUser(USER_ID, "User1", true, "email@example.com");

        deleteRequest(PRIVATE_API_PATH, status().isNoContent(), mockMvc);

        assertEquals(1, userRepository.count());
        User dbUser = userRepository.findById(USER_ID).orElse(null);
        assertNotNull(dbUser);
        assertFalse(dbUser.isActive());
    }

    @Test
    @DisplayName("Test delete user without authentication and expect status 401")
    void testDeleteWithoutAuthentication() throws Exception {
        createUser(USER_ID, "User1", true, "email@example.com");

        deleteRequest(PRIVATE_API_PATH, status().isUnauthorized(), mockMvc);

        assertEquals(1, userRepository.count());
        User dbUser = userRepository.findById(USER_ID).orElse(null);
        assertNotNull(dbUser);
        assertTrue(dbUser.isActive());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete user with inactive user and expect status 404")
    void testDeleteWithInactiveUser() throws Exception {
        createUser(USER_ID, "User1", false, "email@example.com");

        String response = deleteRequest(PRIVATE_API_PATH, status().isNotFound(), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(1, userRepository.count());
        assertEquals(USER_NOT_FOUND + USER_ID, errorDto.message());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete user with non existing user and expect status 404")
    void testDeleteWithNonExistingUser() throws Exception {
        String response = deleteRequest(PRIVATE_API_PATH, status().isNotFound(), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, userRepository.count());
        assertEquals(USER_NOT_FOUND + USER_ID, errorDto.message());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = "ADMIN")
    @DisplayName("Test admin delete user and expect status 204")
    void testAdminDelete() throws Exception {
        createUser("1", "User1", true, "email@example.com");

        deleteRequest(ADMIN_API_PATH + "/1", status().isNoContent(), mockMvc);

        assertEquals(0, userRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = "ADMIN")
    @DisplayName("Test admin delete user with inactive user and expect status 204")
    void testAdminDeleteWithInactiveUser() throws Exception {
        createUser("1", "User1", false, "email@example.com");

        deleteRequest(ADMIN_API_PATH + "/1", status().isNoContent(), mockMvc);

        assertEquals(0, userRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = "ADMIN")
    @DisplayName("Test admin delete user with non existing user and expect status 404")
    void testAdminDeleteWithNonExistingUser() throws Exception {
        String response = deleteRequest(ADMIN_API_PATH + "/1", status().isNotFound(), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(USER_NOT_FOUND + "1", errorDto.message());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test admin delete without authorization and expect status 403")
    void testAdminDeleteWithoutAuthorization() throws Exception {
        createUser("1", "User1", true, "email@example.com");

        deleteRequest(ADMIN_API_PATH + "/1", status().isForbidden(), mockMvc);

        assertEquals(1, userRepository.count());
    }

    @Test
    @DisplayName("Test admin delete without authentication and expect status 401")
    void testAdminDeleteWithoutAuthentication() throws Exception {
        createUser("1", "User1", true, "email@example.com");

        deleteRequest(ADMIN_API_PATH + "/1", status().isUnauthorized(), mockMvc);

        assertEquals(1, userRepository.count());
    }

}
