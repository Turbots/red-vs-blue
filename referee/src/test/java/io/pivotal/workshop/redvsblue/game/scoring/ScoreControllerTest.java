package io.pivotal.workshop.redvsblue.game.scoring;

import io.pivotal.workshop.redvsblue.game.PlayerRepository;
import io.pivotal.workshop.redvsblue.game.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("dev")
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
class ScoreControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PlayerRepository playerRepository;

    private MockMvc mockMvc;
    private RestDocumentationResultHandler documentationHandler;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.documentationHandler = document("{method-name}", preprocessResponse(prettyPrint()));
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(restDocumentation).uris()
                        .withScheme("https")
                        .withHost("referee.apps.pcfone.io")
                        .withPort(443))
                .alwaysDo(this.documentationHandler)
                .build();
    }

    @Test
    void getScore() throws Exception {
        this.mockMvc.perform(get("/score").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("red").description("Score for Team RED"),
                                fieldWithPath("blue").description("Score for Team BLUE"))
                ));
    }

    @Test
    void resetScores() throws Exception {
        long score = this.playerRepository.teamScoreOf(Team.RED);

        this.mockMvc.perform(post("/score/reset").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        long resetScore = this.playerRepository.teamScoreOf(Team.RED);
        assertEquals(0, resetScore);
        assertNotEquals(score, resetScore);
    }

    @Test
    void getRanking() throws Exception {
        this.mockMvc.perform(get("/score/ranking").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("[].id").description("Player ID"),
                                fieldWithPath("[].name").description("Player Name"),
                                fieldWithPath("[].score").description("Player Score"),
                                fieldWithPath("[].team").description("Player Team"))
                ));
    }

}