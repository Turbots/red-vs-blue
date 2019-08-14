package io.pivotal.workshop.redvsblue.game.scoring;

import io.pivotal.workshop.redvsblue.game.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/score")
public class ScoreController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScoreController.class);

    private final ScoringService scoringService;

    public ScoreController(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @GetMapping
    public ResponseEntity<Score> getScore() {
        Score score = this.scoringService.getScore();

        if (score == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(score);
    }

    @PostMapping("/reset")
    public void resetScores() {
        LOGGER.info("Resetting scores...");

        this.scoringService.resetScores();
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<Player>> getRanking() {
        return ResponseEntity.ok(this.scoringService.getOverallRanking());
    }

}
