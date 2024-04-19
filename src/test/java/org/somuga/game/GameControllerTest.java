package org.somuga.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateGame() throws Exception {
        GameCreateDto game = new GameCreateDto("Teste", new Date(), "company", "genre", List.of("PS2", "PS1"));
        String content = mapper.writeValueAsString(game);
        mapper.registerModule(new JavaTimeModule());

        String response = mockMvc.perform(post("/api/v1/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn().getResponse().getContentAsString();
        Game gameDto = mapper.readValue(response, Game.class);

        String response2 = mockMvc.perform(get("/api/v1/game/" + gameDto.getId()))
                .andReturn().getResponse().getContentAsString();

        Game gameDto2 = mapper.readValue(response, Game.class);

        System.out.println("");

    }
}
