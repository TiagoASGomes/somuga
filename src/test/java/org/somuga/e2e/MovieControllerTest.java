//package org.somuga.e2e;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.junit.jupiter.api.*;
//import org.somuga.aspect.Error;
//import org.somuga.dto.crew_role.MovieRoleCreateDto;
//import org.somuga.dto.movie.MovieCreateDto;
//import org.somuga.dto.movie.MoviePublicDto;
//import org.somuga.entity.MovieCrew;
//import org.somuga.repository.MovieCrewRepository;
//import org.somuga.repository.MovieCrewRoleRepository;
//import org.somuga.repository.MovieRepository;
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
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.somuga.util.message.Messages.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@SpringBootTest
//@ContextConfiguration
//@ActiveProfiles("test")
//class MovieControllerTest {
//
//    private static final ObjectMapper mapper = new ObjectMapper();
//    private final String USER_ID = "google-auth2|1234567890";
//    private final String PRIVATE_API_PATH = "/api/v1/movie/private";
//    private final String PUBLIC_API_PATH = "/api/v1/movie/public";
//    private final List<MovieCrew> crew = new ArrayList<>();
//    private final String TITLE = "Title";
//    private final Date RELEASE_DATE = new Date();
//    private final String DESCRIPTION = "Description";
//    private final Integer DURATION = 120;
//    private final String MEDIA_URL = "https://media.com";
//    private final String IMAGE_URL = "https://image.com";
//    MockMvc mockMvc;
//    @Autowired
//    private MovieRepository movieRepository;
//    @Autowired
//    private MovieCrewRepository movieCrewRepository;
//    @Autowired
//    private MovieCrewRoleRepository movieCrewRoleRepository;
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
//    @BeforeEach
//    public void setUp() {
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(controller)
//                .apply(springSecurity())
//                .build();
//        createCrew();
//    }
//
//    @AfterEach
//    public void cleanUp() {
//        movieRepository.deleteAll();
//        movieCrewRepository.deleteAll();
//    }
//
//    public void createCrew() {
//        MovieCrew director = new MovieCrew("Director", new Date());
//        MovieCrew producer = new MovieCrew("Producer", new Date());
//        MovieCrew writer = new MovieCrew("Writer", new Date());
//        MovieCrew actor = new MovieCrew("Actor", new Date());
//        crew.add(director);
//        crew.add(producer);
//        crew.add(writer);
//        crew.add(actor);
//        movieCrewRepository.saveAll(crew);
//    }
//
//    public MoviePublicDto createMovie(String title, Date releaseDate, String description, Integer duration, List<MovieRoleCreateDto> crew, String mediaUrl, String imageUrl) throws Exception {
//        MovieCreateDto movieCreateDto = new MovieCreateDto(title, releaseDate, description, duration, crew, mediaUrl, imageUrl);
//
//        String response = mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(movieCreateDto)))
//                .andExpect(status().isCreated())
//                .andReturn().getResponse().getContentAsString();
//
//        return mapper.readValue(response, MoviePublicDto.class);
//    }
//
//    public Error createMovieBadRequest(String title, Date releaseDate, String description, Integer duration, List<MovieRoleCreateDto> crew, String mediaUrl, String imageUrl) throws Exception {
//        MovieCreateDto movieCreateDto = new MovieCreateDto(title, releaseDate, description, duration, crew, mediaUrl, imageUrl);
//
//        String response = mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(movieCreateDto)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        return mapper.readValue(response, Error.class);
//    }
//
//    public List<MovieRoleCreateDto> createAllRoles() {
//        List<MovieRoleCreateDto> movieRoleCreateDtos = new ArrayList<>();
//        for (MovieCrew movieCrew : crew) {
//            if (movieCrew.getFullName().equals("Actor")) {
//                MovieRoleCreateDto movieRoleCreateDto = new MovieRoleCreateDto(movieCrew.getId(), movieCrew.getFullName().toUpperCase(), "Character1");
//                movieRoleCreateDtos.add(movieRoleCreateDto);
//                continue;
//            }
//            MovieRoleCreateDto movieRoleCreateDto = new MovieRoleCreateDto(movieCrew.getId(), movieCrew.getFullName().toUpperCase(), "");
//            movieRoleCreateDtos.add(movieRoleCreateDto);
//        }
//        return movieRoleCreateDtos;
//    }
//
//    public List<MovieRoleCreateDto> createOneRole(Long id, String fullName, String character) {
//        return List.of(new MovieRoleCreateDto(id, fullName, character));
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie and expect status 201")
//    void testCreateMovie() throws Exception {
//        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        assertEquals(TITLE, moviePublicDto.title());
//        assertEquals(RELEASE_DATE, moviePublicDto.releaseDate());
//        assertEquals(DESCRIPTION, moviePublicDto.description());
//        assertEquals(DURATION, moviePublicDto.duration());
//        assertEquals(crew.size(), moviePublicDto.crew().size());
//    }
//
//    @Test
//    @DisplayName("Test create a movie unauthorized and expect status 401")
//    void testCreateMovieUnauthorized() throws Exception {
//        mockMvc.perform(post(PRIVATE_API_PATH)
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(new MovieCreateDto(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL)))
//        ).andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with empty title and expect status 400")
//    void testCreateMovieWithEmptyTitle() throws Exception {
//        Error error = createMovieBadRequest("", RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(INVALID_TITLE));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with title exceeding 255 characters and expect status 400")
//    void testCreateMovieWithTitleExceeding255Characters() throws Exception {
//        String title = "a".repeat(256);
//
//        Error error = createMovieBadRequest(title, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(MAX_TITLE_CHARACTERS));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with null title and expect status 400")
//    void testCreateMovieWithNullTitle() throws Exception {
//        Error error = createMovieBadRequest(null, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(INVALID_TITLE));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie future date and expect status 400")
//    void testCreateMovieWithFutureDate() throws Exception {
//        Error error = createMovieBadRequest(TITLE, new Date(System.currentTimeMillis() + 1000000), DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(INVALID_RELEASE_DATE));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with null date and expect status 400")
//    void testCreateMovieWithNullDate() throws Exception {
//        Error error = createMovieBadRequest(TITLE, null, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(INVALID_RELEASE_DATE));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with empty description and expect status 400")
//    void testCreateMovieWithEmptyDescription() throws Exception {
//        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, "", DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(INVALID_DESCRIPTION));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with description exceeding 1000 characters and expect status 400")
//    void testCreateMovieWithDescriptionExceeding1000Characters() throws Exception {
//        String description = "a".repeat(1001);
//
//        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, description, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(MAX_DESCRIPTION_CHARACTERS));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with null description and expect status 400")
//    void testCreateMovieWithNullDescription() throws Exception {
//        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, null, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(INVALID_DESCRIPTION));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with duration under 0 and expect status 400")
//    void testCreateMovieWithDurationUnder0() throws Exception {
//        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, 0, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(INVALID_DURATION));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with duration over 1440 and expect status 400")
//    void testCreateMovieWithDurationAbove1440() throws Exception {
//        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, 1441, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(INVALID_DURATION));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with null duration and expect status 400")
//    void testCreateMovieWithNullDuration() throws Exception {
//        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, null, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(INVALID_DURATION));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with empty crew and expect status 400")
//    void testCreateMovieWithEmptyCrew() throws Exception {
//        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, new ArrayList<>(), MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(INVALID_CREW_ROLE));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with null crew and expect status 400")
//    void testCreateMovieWithNullCrew() throws Exception {
//        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, null, MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(INVALID_CREW_ROLE));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with crew role id under 0 and expect status 400")
//    void testCreateMovieWithCrewRoleIdUnder0() throws Exception {
//        List<MovieRoleCreateDto> movieRoleCreateDtos = createOneRole(-1L, "ACTOR", "Character1");
//
//        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, movieRoleCreateDtos, MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(ID_GREATER_THAN_0));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with crew role id null and expect status 400")
//    void testCreateMovieWithCrewRoleIdNull() throws Exception {
//        List<MovieRoleCreateDto> movieRoleCreateDtos = createOneRole(null, "ACTOR", "Character1");
//
//        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, movieRoleCreateDtos, MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(ID_GREATER_THAN_0));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with crew role null and expect status 400")
//    void testCreateMovieWithCrewRoleNull() throws Exception {
//        List<MovieRoleCreateDto> movieRoleCreateDtos = createOneRole(1L, null, "Character1");
//
//        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, movieRoleCreateDtos, MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(INVALID_MOVIE_ROLE));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with crew role empty and expect status 400")
//    void testCreateMovieWithCrewRoleEmpty() throws Exception {
//        List<MovieRoleCreateDto> movieRoleCreateDtos = createOneRole(1L, "", "Character1");
//
//        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, movieRoleCreateDtos, MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(INVALID_MOVIE_ROLE));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with invalid crew role and expect status 400")
//    void testCreateMovieWithInvalidCrewRole() throws Exception {
//        List<MovieRoleCreateDto> movieRoleCreateDtos = createOneRole(1L, "INVALID", "Character1");
//
//        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, movieRoleCreateDtos, MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(INVALID_MOVIE_ROLE));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test create a movie with character name exceeding 255 characters and expect status 400")
//    void testCreateMovieWithCharacterNameExceeding255Characters() throws Exception {
//        List<MovieRoleCreateDto> movieRoleCreateDtos = createOneRole(1L, "ACTOR", "a".repeat(256));
//
//        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, movieRoleCreateDtos, MEDIA_URL, IMAGE_URL);
//
//        assertTrue(error.getMessage().contains(INVALID_CHARACTER_NAME));
//        assertEquals(400, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test get all movies and expect status 200")
//    void testGetAllMovies() throws Exception {
//        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH)
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));
//
//        assertEquals(2, moviePublicDtos.size());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test get all movies with pagination and expect status 200")
//    void testGetAllMoviesWithPagination() throws Exception {
//        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?page=0&size=1")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));
//
//        assertEquals(1, moviePublicDtos.size());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test get all movies by title and expect status 200")
//    void testGetAllMoviesByTitle() throws Exception {
//        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//        createMovie("Different", RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/search/" + TITLE)
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));
//
//        assertEquals(2, moviePublicDtos.size());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test get all movies by title with pagination and expect status 200")
//    void testGetAllMoviesByTitleWithPagination() throws Exception {
//        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//        createMovie("Different", RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/search/" + TITLE + "?page=0&size=1")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));
//
//        assertEquals(1, moviePublicDtos.size());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test get all movies by crew id and expect status 200")
//    void testGetAllMoviesByCrewId() throws Exception {
//        List<MovieRoleCreateDto> crew = createAllRoles();
//        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, crew, MEDIA_URL, IMAGE_URL);
//        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, crew, MEDIA_URL, IMAGE_URL);
//        MovieRoleCreateDto lastCrew = crew.remove(1);
//        createMovie(TITLE + 3, RELEASE_DATE, DESCRIPTION, DURATION, crew, MEDIA_URL, IMAGE_URL);
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/crew/" + lastCrew.movieCrewId())
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));
//
//        assertEquals(2, moviePublicDtos.size());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test get all movies by crew id with pagination and expect status 200")
//    void testGetAllMoviesByCrewIdWithPagination() throws Exception {
//        List<MovieRoleCreateDto> crew = createAllRoles();
//        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, crew, MEDIA_URL, IMAGE_URL);
//        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, crew, MEDIA_URL, IMAGE_URL);
//        MovieRoleCreateDto lastCrew = crew.remove(1);
//        createMovie(TITLE + 3, RELEASE_DATE, DESCRIPTION, DURATION, crew, MEDIA_URL, IMAGE_URL);
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/crew/" + lastCrew.movieCrewId() + "?page=0&size=1")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));
//
//        assertEquals(1, moviePublicDtos.size());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test get movie by id and expect status 200")
//    void testGetMovieById() throws Exception {
//        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + moviePublicDto.id())
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        MoviePublicDto movie = mapper.readValue(response, MoviePublicDto.class);
//
//        assertEquals(TITLE, movie.title());
//        assertEquals(RELEASE_DATE, movie.releaseDate());
//        assertEquals(DESCRIPTION, movie.description());
//        assertEquals(DURATION, movie.duration());
//        assertEquals(crew.size(), movie.crew().size());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test get movie by id not found and expect status 404")
//    void testGetMovieByIdNotFound() throws Exception {
//        Error error = mapper.readValue(mockMvc.perform(get(PUBLIC_API_PATH + "/1")
//                        .with(csrf()))
//                .andExpect(status().isNotFound())
//                .andReturn().getResponse().getContentAsString(), Error.class);
//
//        assertTrue(error.getMessage().contains(MOVIE_NOT_FOUND));
//        assertEquals(404, error.getStatus());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test update movie and expect status 200")
//    void testUpdateMovie() throws Exception {
//        List<MovieRoleCreateDto> crew = createAllRoles();
//        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, crew, MEDIA_URL, IMAGE_URL);
//        String newTitle = "New Title";
//        Date newReleaseDate = new Date();
//        String newDescription = "New Description";
//        Integer newDuration = 150;
//        crew.remove(3);
//        crew.remove(2);
//
//        MovieCreateDto movieCreateDto = new MovieCreateDto(newTitle, newReleaseDate, newDescription, newDuration, crew, MEDIA_URL, IMAGE_URL);
//
//        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/" + moviePublicDto.id())
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(movieCreateDto)))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        MoviePublicDto updatedMovie = mapper.readValue(response, MoviePublicDto.class);
//
//        assertEquals(newTitle, updatedMovie.title());
//        assertEquals(newReleaseDate, updatedMovie.releaseDate());
//        assertEquals(newDescription, updatedMovie.description());
//        assertEquals(newDuration, updatedMovie.duration());
//        assertEquals(crew.size(), updatedMovie.crew().size());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test update movie invalid id and expect status 404")
//    void testUpdateMovieInvalidId() throws Exception {
//        List<MovieRoleCreateDto> crew = createAllRoles();
//        String newTitle = "New Title";
//        Date newReleaseDate = new Date();
//        String newDescription = "New Description";
//        Integer newDuration = 150;
//        crew.remove(3);
//        crew.remove(2);
//
//        MovieCreateDto movieCreateDto = new MovieCreateDto(newTitle, newReleaseDate, newDescription, newDuration, crew, MEDIA_URL, IMAGE_URL);
//
//        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/1")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(movieCreateDto)))
//                .andExpect(status().isNotFound())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertEquals(MOVIE_NOT_FOUND + 1, error.getMessage());
//        assertEquals(404, error.getStatus());
//    }
//
//    @Test
//    @DisplayName("Test update movie unauthorized and expect status 401")
//    void testUpdateMovieUnauthorized() throws Exception {
//        mockMvc.perform(put(PRIVATE_API_PATH + "/1")
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(new MovieCreateDto(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL)))
//        ).andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    @WithMockUser(username = USER_ID)
//    @DisplayName("Test delete movie and expect status 204")
//    void testDeleteMovie() throws Exception {
//        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles(), MEDIA_URL, IMAGE_URL);
//
//        mockMvc.perform(delete(PRIVATE_API_PATH + "/" + moviePublicDto.id())
//                        .with(csrf()))
//                .andExpect(status().isNoContent());
//
//        assertEquals(0, movieRepository.count());
//        assertEquals(0, movieCrewRoleRepository.count());
//    }
//
//    @Test
//    @DisplayName("Test delete movie unauthorized and expect status 401")
//    void testDeleteMovieUnauthorized() throws Exception {
//        mockMvc.perform(delete(PRIVATE_API_PATH + "/1")
//                .with(csrf())
//        ).andExpect(status().isUnauthorized());
//    }
//
//
//}
