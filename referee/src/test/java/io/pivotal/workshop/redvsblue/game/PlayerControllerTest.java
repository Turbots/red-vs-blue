package io.pivotal.workshop.redvsblue.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.cli.CliDocumentation.httpieRequest;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("dev")
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
class PlayerControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private ObjectWriter writer;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        writer = mapper.writer().withDefaultPrettyPrinter();

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void createPlayer() throws Exception {
        String json = writer.writeValueAsString(fakePlayer("Random Guy"));

        this.mockMvc.perform(post("/player")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andDo(document("createPlayer",
                        responseFields(
                                fieldWithPath("id").description("Player ID"),
                                fieldWithPath("name").description("Player Name"),
                                fieldWithPath("score").description("Player Score"),
                                fieldWithPath("team").description("Player Team"))
                ));
    }

    @Test
    void createPlayerThatExistsShouldFail() throws Exception {
        String json = writer.writeValueAsString(fakePlayer("Dieter Hubau"));

        this.mockMvc.perform(post("/player")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andDo(document("createPlayerThatExistsShouldFail"));
    }

    private Object fakePlayer(String name) {
        return new Player(name, Team.RED, 0L);
    }

    @Test
    void deletePlayer() throws Exception {
        this.mockMvc.perform(delete("/player")
                .param("id", "5")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("deletePlayer"));
    }
}