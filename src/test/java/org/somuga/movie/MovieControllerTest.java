package org.somuga.movie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovieControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String API_PATH = "/api/v1/movie";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MovieRepository movieRepository;

    @BeforeAll
    public static void setUpMapper() {
        mapper.registerModule(new JavaTimeModule());
    }

    @AfterEach
    public void cleanUp() {
        movieRepository.deleteAll();
    }

    @Test
    @DisplayName("Simple test")
    void testCreateMovie() throws Exception {
        MovieCreateDto movieDto = new MovieCreateDto("Title", List.of("Actor1", "Actor2"), "Producer", new Date());

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieDto)))
                .andReturn().getResponse().getContentAsString();

        MoviePublicDto movie = mapper.readValue(response, MoviePublicDto.class);

        assertNotNull(movie.id());
        assertEquals(movieDto.title(), movie.title());
        assertEquals(movieDto.producer(), movie.producer());
        assertEquals(movieDto.releaseDate(), movie.releaseDate());
        assertTrue(movie.actors().contains("Actor1"));
        assertTrue(movie.actors().contains("Actor2"));

        String response2 = mockMvc.perform(get(API_PATH + "/" + movie.id()))
                .andReturn().getResponse().getContentAsString();

        MoviePublicDto movie2 = mapper.readValue(response2, MoviePublicDto.class);

        assertEquals(movie.id(), movie2.id());
        assertEquals(movieDto.title(), movie2.title());
        assertEquals(movieDto.producer(), movie2.producer());
        assertEquals(movieDto.releaseDate(), movie2.releaseDate());
        assertTrue(movie2.actors().contains("Actor1"));
        assertTrue(movie2.actors().contains("Actor2"));
    }
}
