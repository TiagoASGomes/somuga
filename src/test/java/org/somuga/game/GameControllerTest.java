package org.somuga.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

//    @Test
//    void testCreateGame() throws Exception {
//        GameCreateDto game = new GameCreateDto("Teste", new Date(), "company", "genre", List.of("PS2", "PS1"));
//        String content = mapper.writeValueAsString(game);
//        mapper.registerModule(new JavaTimeModule());
//
//        String response = mockMvc.perform(post("/api/v1/game")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(content))
//                .andReturn().getResponse().getContentAsString();
//        Game gameDto = mapper.readValue(response, Game.class);
//
//        String response2 = mockMvc.perform(get("/api/v1/game/" + gameDto.getId()))
//                .andReturn().getResponse().getContentAsString();
//
//        Game gameDto2 = mapper.readValue(response, Game.class);
//
//        System.out.println("");
//
//    }
}
