package io.pivotal.workshop.redvsblue.game.scoring;

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

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void getScore() throws Exception {
        this.mockMvc.perform(get("/score").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("getScore",
                        responseFields(
                                fieldWithPath("first").description("Score for Team RED"),
                                fieldWithPath("second").description("Score for Team BLUE"))
                ));
    }

    @Test
    void resetScores() throws Exception {
        this.mockMvc.perform(post("/score/reset").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("resetScores"));
    }

    @Test
    void getRanking() throws Exception {
        this.mockMvc.perform(get("/score/ranking").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("getRanking",
                        responseFields(
                                fieldWithPath("[].id").description("Player ID"),
                                fieldWithPath("[].name").description("Player Name"),
                                fieldWithPath("[].score").description("Player Score"),
                                fieldWithPath("[].team").description("Player Team"))
                ));
    }

}