package io.pivotal.workshop.redvsblue;

import io.pivotal.workshop.redvsblue.game.Ball;
import io.pivotal.workshop.redvsblue.game.Player;
import io.pivotal.workshop.redvsblue.game.PlayerRepository;
import io.pivotal.workshop.redvsblue.game.scoring.ScoringProperties;
import io.pivotal.workshop.redvsblue.game.scoring.messaging.InputChannels;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("dev")
@SuppressWarnings("OptionalGetWithoutIsPresent")
class RefereeApplicationTests {

    @Autowired
    private InputChannels inputChannels;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ScoringProperties scoringProperties;

    //@Test
    @DisplayName("Message resulting in successful hit is registered correctly in database")
    void testScoringGeneratesAHit() {
        this.scoringProperties.setOdds(100);

        Player dieterHubau = this.playerRepository.findById(1L).get();
        Player patrickSteiner = this.playerRepository.findById(6L).get();

        Long score = dieterHubau.getScore();

        inputChannels.balls().send(new GenericMessage<>(new Ball(dieterHubau, patrickSteiner)));

        assertEquals(new Long(score + 1), this.playerRepository.findById(1L).get().getScore());
    }
}
