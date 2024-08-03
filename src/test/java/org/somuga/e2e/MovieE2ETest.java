package org.somuga.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.somuga.aspect.Error;
import org.somuga.dto.crew_role.MovieRoleCreateDto;
import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MovieLikePublicDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.*;
import org.somuga.repository.LikeRepository;
import org.somuga.repository.MovieCrewRepository;
import org.somuga.repository.MovieRepository;
import org.somuga.repository.UserRepository;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.somuga.util.message.Messages.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
class MovieE2ETest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String USER_ID = "google-auth2|1234567890";
    private final String PRIVATE_API_PATH = "/api/v1/movie/private";
    private final String PUBLIC_API_PATH = "/api/v1/movie/public";
    private final String ADMIN_API_PATH = "/api/v1/movie/admin";
    private final List<MovieCrew> crew = new ArrayList<>();
    private final String TITLE = "Title";
    private final Date RELEASE_DATE = new Date();
    private final String DESCRIPTION = "Description";
    private final Integer DURATION = 120;
    private final String MEDIA_URL = "https://media.com";
    private final String IMAGE_URL = "https://image.com";
    MockMvc mockMvc;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieCrewRepository movieCrewRepository;
    @Autowired
    private WebApplicationContext controller;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LikeRepository likeRepository;
    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @BeforeAll
    public static void setUpMapper() {
        mapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(controller)
                .apply(springSecurity())
                .build();
        createCrew();
    }

    @AfterEach
    public void cleanUp() {
        likeRepository.deleteAll();
        userRepository.deleteAll();
        movieRepository.deleteAll();
        movieCrewRepository.deleteAll();
    }

    public void createCrew() {
        MovieCrew director = MovieCrew.builder().fullName("Director").birthDate(new Date()).build();
        MovieCrew producer = MovieCrew.builder().fullName("Producer").birthDate(new Date()).build();
        MovieCrew writer = MovieCrew.builder().fullName("Writer").birthDate(new Date()).build();
        MovieCrew actor = MovieCrew.builder().fullName("Actor").birthDate(new Date()).build();
        crew.add(director);
        crew.add(producer);
        crew.add(writer);
        crew.add(actor);
        movieCrewRepository.saveAll(crew);
    }

    public MoviePublicDto createMovie(String title, Date releaseDate, String description, Integer duration, List<MovieRoleCreateDto> crew, String mediaUrl, String imageUrl) throws Exception {
        MovieCreateDto movieCreateDto = new MovieCreateDto(title, releaseDate, description, duration, crew, mediaUrl, imageUrl);

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCreateDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return mapper.readValue(response, MoviePublicDto.class);
    }

    public Error createMovieBadRequest(String title, Date releaseDate, String description, Integer duration, List<MovieRoleCreateDto> crew, String mediaUrl, String imageUrl) throws Exception {
        MovieCreateDto movieCreateDto = new MovieCreateDto(title, releaseDate, description, duration, crew, mediaUrl, imageUrl);

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        return mapper.readValue(response, Error.class);
    }

    public List<MovieRoleCreateDto> createAllRoles() {
        List<MovieRoleCreateDto> movieRoleCreateDtos = new ArrayList<>();
        for (MovieCrew movieCrew : crew) {
            if (movieCrew.getFullName().equals("Actor")) {
                MovieRoleCreateDto movieRoleCreateDto = new MovieRoleCreateDto(movieCrew.getId(), movieCrew.getFullName(), "Character1");
                movieRoleCreateDtos.add(movieRoleCreateDto);
                continue;
            }
            MovieRoleCreateDto movieRoleCreateDto = new MovieRoleCreateDto(movieCrew.getId(), movieCrew.getFullName(), "");
            movieRoleCreateDtos.add(movieRoleCreateDto);
        }
        return movieRoleCreateDtos;
    }

    private void createLike(Long gameId, String userId) {
        Like like = Like.builder()
                .media(Game.builder().id(gameId).build())
                .user(User.builder().id(userId).build())
                .build();

        likeRepository.save(like);
    }

    private void createUser(String userId) {
        User user = User.builder()
                .id(userId)
                .userName("TestUser")
                .build();

        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie and expect status 201")
    void testCreateMovie() throws Exception {
        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
        Movie movieEntity = movieRepository.findById(moviePublicDto.id()).orElse(null);
        assertNotNull(movieEntity);


        assertEquals(TITLE, moviePublicDto.title());
        assertEquals(TITLE, movieEntity.getTitle());
        assertEquals(RELEASE_DATE, moviePublicDto.releaseDate());
        assertEquals(RELEASE_DATE, movieEntity.getReleaseDate());
        assertEquals(DESCRIPTION, moviePublicDto.description());
        assertEquals(DESCRIPTION, movieEntity.getDescription());
        assertEquals(DURATION, moviePublicDto.duration());
        assertEquals(DURATION, movieEntity.getDuration());
        assertEquals(crew.size(), moviePublicDto.crew().size());
        assertEquals(crew.size(), movieEntity.getMovieCrew().size());
    }

    @Test
    @DisplayName("Test create a movie unauthorized and expect status 401")
    void testCreateMovieUnauthorized() throws Exception {
        mockMvc.perform(post(PRIVATE_API_PATH)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new MovieCreateDto(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL)))
        ).andExpect(status().isUnauthorized());

        assertEquals(0, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie with empty data and expect status 400")
    void testCreateMovieWithEmptyTitle() throws Exception {
        Error error = createMovieBadRequest("", null, "", 0, List.of(), "", "");

        assertTrue(error.message().contains(INVALID_TITLE));
        assertTrue(error.message().contains(INVALID_RELEASE_DATE));
        assertTrue(error.message().contains(INVALID_DESCRIPTION));
        assertTrue(error.message().contains(INVALID_DURATION));
        assertTrue(error.message().contains(INVALID_CREW_ROLE));
        assertTrue(error.message().contains(INVALID_MEDIA_URL));
        assertEquals(0, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie with null data and expect status 400")
    void testCreateMovieWithNullTitle() throws Exception {
        Error error = createMovieBadRequest(null, null, null, null, null, null, null);

        assertTrue(error.message().contains(INVALID_TITLE));
        assertTrue(error.message().contains(INVALID_RELEASE_DATE));
        assertTrue(error.message().contains(INVALID_DESCRIPTION));
        assertTrue(error.message().contains(INVALID_DURATION));
        assertTrue(error.message().contains(INVALID_CREW_ROLE));
        assertTrue(error.message().contains(INVALID_MEDIA_URL));
        assertEquals(0, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie with title exceeding 255 characters and expect status 400")
    void testCreateMovieWithTitleExceeding255Characters() throws Exception {
        String title = "a".repeat(256);

        Error error = createMovieBadRequest(title, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);

        assertTrue(error.message().contains(MAX_TITLE_CHARACTERS));
        assertEquals(0, movieRepository.count());
    }


    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie future date and expect status 400")
    void testCreateMovieWithFutureDate() throws Exception {
        Error error = createMovieBadRequest(TITLE, new Date(System.currentTimeMillis() + 1000000), DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);

        assertTrue(error.message().contains(INVALID_RELEASE_DATE));
        assertEquals(0, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie with description exceeding 1000 characters and expect status 400")
    void testCreateMovieWithDescriptionExceeding1000Characters() throws Exception {
        String description = "a".repeat(1001);

        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, description, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);

        assertTrue(error.message().contains(MAX_DESCRIPTION_CHARACTERS));
        assertEquals(0, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie with duration under 0 and expect status 400")
    void testCreateMovieWithDurationUnder0() throws Exception {
        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, 0, createAllRoles(), MEDIA_URL, IMAGE_URL);

        assertTrue(error.message().contains(INVALID_DURATION));
        assertEquals(0, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie with duration over 1440 and expect status 400")
    void testCreateMovieWithDurationAbove1440() throws Exception {
        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, 1441, createAllRoles(), MEDIA_URL, IMAGE_URL);

        assertTrue(error.message().contains(INVALID_DURATION));
        assertEquals(0, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie with crew role id under 0 and expect status 400")
    void testCreateMovieWithCrewRoleIdUnder0() throws Exception {
        List<MovieRoleCreateDto> movieRoleCreateDtos = List.of(new MovieRoleCreateDto(-1L, "ACTOR", "Character1"));

        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, movieRoleCreateDtos, MEDIA_URL, IMAGE_URL);

        assertEquals(ID_GREATER_THAN_0, error.message());
        assertEquals(0, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie with crew role id null and expect status 400")
    void testCreateMovieWithCrewRoleIdNull() throws Exception {
        List<MovieRoleCreateDto> movieRoleCreateDtos = List.of(new MovieRoleCreateDto(null, "ACTOR", "Character1"));

        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, movieRoleCreateDtos, MEDIA_URL, IMAGE_URL);

        assertTrue(error.message().contains(ID_GREATER_THAN_0));
        assertEquals(0, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie with crew role null and expect status 400")
    void testCreateMovieWithCrewRoleNull() throws Exception {
        List<MovieRoleCreateDto> movieRoleCreateDtos = List.of(new MovieRoleCreateDto(1L, null, "Character1"));

        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, movieRoleCreateDtos, MEDIA_URL, IMAGE_URL);

        assertEquals(INVALID_MOVIE_ROLE, error.message());
        assertEquals(0, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie with crew role empty and expect status 400")
    void testCreateMovieWithCrewRoleEmpty() throws Exception {
        List<MovieRoleCreateDto> movieRoleCreateDtos = List.of(new MovieRoleCreateDto(1L, "", "Character1"));

        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, movieRoleCreateDtos, MEDIA_URL, IMAGE_URL);

        assertEquals(INVALID_MOVIE_ROLE, error.message());
        assertEquals(0, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie with invalid crew role and expect status 400")
    void testCreateMovieWithInvalidCrewRole() throws Exception {
        List<MovieRoleCreateDto> movieRoleCreateDtos = List.of(new MovieRoleCreateDto(1L, "INVALID", "Character1"));

        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, movieRoleCreateDtos, MEDIA_URL, IMAGE_URL);

        assertTrue(error.message().contains(INVALID_MOVIE_ROLE));
        assertEquals(0, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie with character name exceeding 255 characters and expect status 400")
    void testCreateMovieWithCharacterNameExceeding255Characters() throws Exception {
        List<MovieRoleCreateDto> movieRoleCreateDtos = List.of(new MovieRoleCreateDto(1L, "ACTOR", "a".repeat(256)));

        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, movieRoleCreateDtos, MEDIA_URL, IMAGE_URL);

        assertEquals(INVALID_CHARACTER_NAME, error.message());
        assertEquals(0, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie with actor with empty character name and expect status 400")
    void testCreateMovieWithActorEmptyCharacterName() throws Exception {
        List<MovieRoleCreateDto> movieRoleCreateDtos = List.of(new MovieRoleCreateDto(1L, "ACTOR", ""));

        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, movieRoleCreateDtos, MEDIA_URL, IMAGE_URL);

        assertEquals(CHARACTER_NAME_REQUIRED, error.message());
        assertEquals(0, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie with actor with null character name and expect status 400")
    void testCreateMovieWithActorNullCharacterName() throws Exception {
        List<MovieRoleCreateDto> movieRoleCreateDtos = List.of(new MovieRoleCreateDto(1L, "ACTOR", null));

        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, movieRoleCreateDtos, MEDIA_URL, IMAGE_URL);

        assertEquals(CHARACTER_NAME_REQUIRED, error.message());
        assertEquals(0, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create a movie crew id not found and expect status 404")
    void testCreateMovieCrewIdNotFound() throws Exception {
        List<MovieRoleCreateDto> movieRoleCreateDtos = List.of(new MovieRoleCreateDto(100L, "ACTOR", "Character1"));

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new MovieCreateDto(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, movieRoleCreateDtos, MEDIA_URL, IMAGE_URL))))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(MOVIE_CREW_NOT_FOUND + 100, error.message());
        assertEquals(0, movieRepository.count());
    }


    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test get all movies with no search criteria and expect status 200")
    void testGetAllMovies() throws Exception {
        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);

        String response = mockMvc.perform(get(PUBLIC_API_PATH)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));

        assertEquals(2, moviePublicDtos.size());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test get all movies with pagination and expect status 200")
    void testGetAllMoviesWithPagination() throws Exception {
        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?page=0&size=1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));

        assertEquals(1, moviePublicDtos.size());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test get all movies with title search criteria and expect status 200")
    void testGetAllMoviesWithTitleSearchCriteria() throws Exception {
        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
        createMovie("Different", RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?title=" + TITLE)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));

        assertEquals(2, moviePublicDtos.size());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test get all movies with title search criteria case insensitive and expect status 200")
    void testGetAllMoviesWithTitleSearchCriteriaCaseInsensitive() throws Exception {
        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
        createMovie("Different", RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?title=" + TITLE.toLowerCase())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));

        assertEquals(2, moviePublicDtos.size());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test get all movies with title search criteria paged and expect status 200")
    void testGetAllMoviesWithTitleSearchCriteriaPaged() throws Exception {
        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
        createMovie("Different", RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?title=" + TITLE + "&page=0&size=1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));

        assertEquals(1, moviePublicDtos.size());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test get all movies with crew search criteria and expect status 200")
    void testGetAllMoviesWithCrewSearchCriteria() throws Exception {
        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
        createMovie("Different", RELEASE_DATE, DESCRIPTION, DURATION, List.of(new MovieRoleCreateDto(crew.get(1).getId(), "ACTOR", "Name")), MEDIA_URL, IMAGE_URL);

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?crewId=" + crew.get(0).getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));

        assertEquals(2, moviePublicDtos.size());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test get all movies with crew search criteria paged and expect status 200")
    void testGetAllMoviesWithCrewSearchCriteriaPaged() throws Exception {
        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
        createMovie("Different", RELEASE_DATE, DESCRIPTION, DURATION, List.of(new MovieRoleCreateDto(crew.get(1).getId(), "ACTOR", "Name")), MEDIA_URL, IMAGE_URL);

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?crewId=" + crew.get(0).getId() + "&page=0&size=1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));

        assertEquals(1, moviePublicDtos.size());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test get all movies with crew search criteria and title search criteria and expect status 200")
    void testGetAllMoviesWithCrewAndTitleSearchCriteria() throws Exception {
        List<MovieRoleCreateDto> crewDtos = createAllRoles();
        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, crewDtos, MEDIA_URL, IMAGE_URL);
        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, List.of(new MovieRoleCreateDto(crew.get(1).getId(), "ACTOR", "Name")), MEDIA_URL, IMAGE_URL);
        createMovie("Different", RELEASE_DATE, DESCRIPTION, DURATION, crewDtos, MEDIA_URL, IMAGE_URL);

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?crewId=" + crew.get(0).getId() + "&title=" + TITLE)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));

        assertEquals(1, moviePublicDtos.size());
    }


    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test get movie by id and expect status 200")
    void testGetMovieById() throws Exception {
        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + moviePublicDto.id())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MovieLikePublicDto movieDto = mapper.readValue(response, MovieLikePublicDto.class);

        assertEquals(TITLE, movieDto.movie().title());
        assertEquals(RELEASE_DATE, movieDto.movie().releaseDate());
        assertEquals(DESCRIPTION, movieDto.movie().description());
        assertEquals(DURATION, movieDto.movie().duration());
        assertEquals(crew.size(), movieDto.movie().crew().size());
        assertFalse(movieDto.liked());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test get movie by id not found and expect status 404")
    void testGetMovieByIdNotFound() throws Exception {
        Error error = mapper.readValue(mockMvc.perform(get(PUBLIC_API_PATH + "/1")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString(), Error.class);

        assertTrue(error.message().contains(MOVIE_NOT_FOUND));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test get movie by id with liked movie and expect status 200 and liked true")
    void testGetMovieByIdWithLikedMovie() throws Exception {
        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
        createUser(USER_ID);
        createLike(moviePublicDto.id(), USER_ID);

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + moviePublicDto.id())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MovieLikePublicDto movieDto = mapper.readValue(response, MovieLikePublicDto.class);

        assertTrue(movieDto.liked());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test get movie by id with liked movie from another user and expect status 200 and liked false")
    void testGetMovieByIdWithLikedMovieFromAnotherUser() throws Exception {
        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
        createUser("AnotherUser");
        createLike(moviePublicDto.id(), "AnotherUser");

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + moviePublicDto.id())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MovieLikePublicDto movieDto = mapper.readValue(response, MovieLikePublicDto.class);

        assertFalse(movieDto.liked());
    }


    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update movie and expect status 200")
    void testUpdateMovie() throws Exception {
        List<MovieRoleCreateDto> crew = createAllRoles();
        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, crew.subList(0, 2), MEDIA_URL, IMAGE_URL);
        String newTitle = "New Title";
        Date newReleaseDate = new Date();
        String newDescription = "New Description";
        Integer newDuration = 150;
        String newImageUrl = "https://newimage.com";
        String newMediaUrl = "https://newmedia.com";

        MovieCreateDto movieCreateDto = new MovieCreateDto(newTitle, newReleaseDate, newDescription, newDuration, crew.subList(2, 4), newMediaUrl, newImageUrl);

        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/" + moviePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCreateDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MoviePublicDto updatedMovie = mapper.readValue(response, MoviePublicDto.class);
        Movie movieEntity = movieRepository.findById(updatedMovie.id()).orElse(null);
        assertNotNull(movieEntity);

        assertEquals(1, movieRepository.count());
        assertEquals(crew.size(), movieCrewRepository.count());

        assertEquals(newTitle, updatedMovie.title());
        assertEquals(newTitle, movieEntity.getTitle());
        assertEquals(newReleaseDate, updatedMovie.releaseDate());
        assertEquals(newReleaseDate, movieEntity.getReleaseDate());
        assertEquals(newDescription, updatedMovie.description());
        assertEquals(newDescription, movieEntity.getDescription());
        assertEquals(newDuration, updatedMovie.duration());
        assertEquals(newDuration, movieEntity.getDuration());
        assertEquals(2, updatedMovie.crew().size());
        assertEquals(2, movieEntity.getMovieCrew().size());
        assertEquals(newImageUrl, updatedMovie.imageUrl());
        assertEquals(newImageUrl, movieEntity.getImageUrl());
        assertEquals(newMediaUrl, updatedMovie.mediaUrl());
        assertEquals(newMediaUrl, movieEntity.getMediaUrl());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update movie with validation errors and expect status 400")
    void testUpdateMovieWithEmptyTitle() throws Exception {
        List<MovieRoleCreateDto> crew = createAllRoles();
        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, crew, MEDIA_URL, IMAGE_URL);

        MovieCreateDto movieCreateDto = new MovieCreateDto("a".repeat(256), RELEASE_DATE, DESCRIPTION, DURATION, crew, MEDIA_URL, IMAGE_URL);


        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/" + moviePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCreateDto)
                        ))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);
        Movie movieEntity = movieRepository.findById(moviePublicDto.id()).orElse(null);

        assertEquals(TITLE, movieEntity.getTitle());

        assertTrue(error.message().contains(MAX_TITLE_CHARACTERS));
        assertEquals(1, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update movie created by another user and expect status 403")
    void testUpdateMovieCreatedByAnotherUser() throws Exception {
        Movie movie = Movie.builder()
                .title(TITLE)
                .releaseDate(RELEASE_DATE)
                .description(DESCRIPTION)
                .duration(DURATION)
                .mediaUrl(MEDIA_URL)
                .imageUrl(IMAGE_URL)
                .mediaCreatorId("AnotherUser")
                .build();
        movieRepository.save(movie);

        MovieCreateDto movieCreateDto = new MovieCreateDto("NewTitle", RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);


        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/" + movie.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCreateDto)
                        ))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);
        Movie movieEntity = movieRepository.findById(movie.getId()).orElse(null);
        assertNotNull(movieEntity);

        assertEquals(TITLE, movieEntity.getTitle());

        assertEquals(UNAUTHORIZED_UPDATE, error.message());
    }

    @Test
    @DisplayName("Test update movie unauthenticated and expect status 401")
    void testUpdateMovieUnauthenticated() throws Exception {
        MovieCreateDto movieCreateDto = new MovieCreateDto("NewTitle", RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);

        mockMvc.perform(put(PRIVATE_API_PATH + "/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCreateDto)
                        ))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete movie and expect status 204")
    void testDeleteMovie() throws Exception {
        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);

        mockMvc.perform(delete(PRIVATE_API_PATH + "/" + moviePublicDto.id())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertEquals(0, movieRepository.count());
        assertEquals(crew.size(), movieCrewRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete movie created by another user and expect status 403")
    void testDeleteMovieCreatedByAnotherUser() throws Exception {
        Movie movie = Movie.builder()
                .title(TITLE)
                .releaseDate(RELEASE_DATE)
                .description(DESCRIPTION)
                .duration(DURATION)
                .mediaUrl(MEDIA_URL)
                .imageUrl(IMAGE_URL)
                .mediaCreatorId("AnotherUser")
                .build();
        movieRepository.save(movie);

        String response = mockMvc.perform(delete(PRIVATE_API_PATH + "/" + movie.getId())
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);
        assertEquals(1, movieRepository.count());

        assertEquals(UNAUTHORIZED_DELETE, error.message());
    }

    @Test
    @DisplayName("Test delete movie unauthenticated and expect status 401")
    void testDeleteMovieUnauthenticated() throws Exception {
        mockMvc.perform(delete(PRIVATE_API_PATH + "/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete movie not found and expect status 404")
    void testDeleteMovieNotFound() throws Exception {
        String response = mockMvc.perform(delete(PRIVATE_API_PATH + "/1")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(MOVIE_NOT_FOUND + 1, error.message());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = "ADMIN")
    @DisplayName("Test delete movie as admin and expect status 204")
    void testDeleteMovieAsAdmin() throws Exception {
        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);

        mockMvc.perform(delete(ADMIN_API_PATH + "/" + moviePublicDto.id())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertEquals(0, movieRepository.count());
        assertEquals(crew.size(), movieCrewRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = "ADMIN")
    @DisplayName("Test delete movie not found as admin and expect status 404")
    void testDeleteMovieNotFoundAsAdmin() throws Exception {
        String response = mockMvc.perform(delete(ADMIN_API_PATH + "/1")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(MOVIE_NOT_FOUND + 1, error.message());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = "ADMIN")
    @DisplayName("Test delete movie created by another user as admin and expect status 204")
    void testDeleteMovieCreatedByAnotherUserAsAdmin() throws Exception {
        Movie movie = Movie.builder()
                .title(TITLE)
                .releaseDate(RELEASE_DATE)
                .description(DESCRIPTION)
                .duration(DURATION)
                .mediaUrl(MEDIA_URL)
                .imageUrl(IMAGE_URL)
                .mediaCreatorId("AnotherUser")
                .build();
        movieRepository.save(movie);

        mockMvc.perform(delete(ADMIN_API_PATH + "/" + movie.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertEquals(0, movieRepository.count());
        assertEquals(crew.size(), movieCrewRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete movie unauthorized and expect status 403")
    void testDeleteMovieUnauthorized() throws Exception {
        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);

        mockMvc.perform(delete(ADMIN_API_PATH + "/" + moviePublicDto.id())
                        .with(csrf()))
                .andExpect(status().isForbidden());

        assertEquals(1, movieRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete movie with likes and expect status 204")
    void testDeleteMovieWithLikes() throws Exception {
        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
        createUser(USER_ID);
        createLike(moviePublicDto.id(), USER_ID);

        mockMvc.perform(delete(PRIVATE_API_PATH + "/" + moviePublicDto.id())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertEquals(0, movieRepository.count());
        assertEquals(0, likeRepository.count());
        assertEquals(crew.size(), movieCrewRepository.count());
    }

}
