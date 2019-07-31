package io.pivotal.workshop.redvsblue.game.scoring;

import io.pivotal.workshop.redvsblue.game.Player;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ScoreController {

    private final ScoringService scoringService;

    public ScoreController(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @PostMapping("/reset")
    public void resetScores() {
        this.scoringService.resetScores();
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<Player>> getRanking() {
        return ResponseEntity.ok(this.scoringService.getOverallRanking());
    }

    @GetMapping("/score")
    public ResponseEntity<Pair<Long, Long>> getScore() {
        Pair<Long, Long> score = this.scoringService.getScore();

        if (score == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(score);
    }
}
