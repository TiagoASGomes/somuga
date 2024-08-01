package org.somuga.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.somuga.converter.MovieCrewConverter;
import org.somuga.dto.movie_crew.MovieCrewCreateDto;
import org.somuga.dto.movie_crew.MovieCrewPublicDto;
import org.somuga.entity.MovieCrew;
import org.somuga.repository.MovieCrewRepository;
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

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
class MovieCrewE2ETest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String USER_ID = "google-auth2|1234567890";
    private final String PUBLIC_API_PATH = "/api/v1/movie/crew/public";
    private final String ADMIN_API_PATH = "/api/v1/movie/crew/admin";
    private final String NAME = "Test Name";
    private final Date BIRTH_DATE = new Date();
    MockMvc mockMvc;
    @Autowired
    private MovieCrewRepository movieCrewRepository;
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
        movieCrewRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(controller)
                .apply(springSecurity())
                .build();
    }

    public MovieCrewPublicDto createMovieCrew(String name, Date birthDate) {
        MovieCrew movieCrew = MovieCrew.builder()
                .fullName(name)
                .birthDate(birthDate)
                .build();
        return MovieCrewConverter.fromEntityToPublicDto(movieCrewRepository.save(movieCrew));
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test create movie crew and expect status 201")
    void testCreateMovieCrew() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(NAME, BIRTH_DATE);

        String response = mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        MovieCrewPublicDto movieCrewResponse = mapper.readValue(response, MovieCrewPublicDto.class);

        assertEquals(1, movieCrewRepository.count());
        assertNotNull(movieCrewResponse.id());
        MovieCrew movieCrew1 = movieCrewRepository.findById(movieCrewResponse.id()).orElse(null);
        assertNotNull(movieCrew1);
        assertEquals(movieCrewCreateDto.fullName(), movieCrew1.getFullName());
        assertEquals(movieCrewCreateDto.birthDate(), movieCrew1.getBirthDate());
    }
}
