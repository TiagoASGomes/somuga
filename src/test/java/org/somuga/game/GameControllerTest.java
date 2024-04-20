package org.somuga.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GamePublicDto;
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
class GameControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String API_PATH = "/api/v1/game";
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void setUpMapper() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Simple test")
    void testCreateMovie() throws Exception {
        GameCreateDto gameDto = new GameCreateDto("Title", new Date(), "Company", "genre", List.of("PS1", "PS2"));

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameDto)))
                .andReturn().getResponse().getContentAsString();

        GamePublicDto game = mapper.readValue(response, GamePublicDto.class);

        assertNotNull(game.id());
        assertEquals(gameDto.title(), game.title());
        assertEquals(gameDto.company(), game.company());
        assertEquals(gameDto.releaseDate(), game.releaseDate());
        assertEquals(gameDto.genre(), game.genre());
        assertTrue(game.platforms().contains("PS1"));
        assertTrue(game.platforms().contains("PS2"));

        String response2 = mockMvc.perform(get(API_PATH + "/" + game.id()))
                .andReturn().getResponse().getContentAsString();

        GamePublicDto game2 = mapper.readValue(response2, GamePublicDto.class);

        assertEquals(game2.id(), game.id());
        assertEquals(gameDto.title(), game2.title());
        assertEquals(gameDto.company(), game2.company());
        assertEquals(gameDto.releaseDate(), game2.releaseDate());
        assertEquals(gameDto.genre(), game2.genre());
        assertTrue(game2.platforms().contains("PS1"));
        assertTrue(game2.platforms().contains("PS2"));
    }

}
