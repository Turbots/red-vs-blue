package io.pivotal.workshop.redvsblue.game.scoring;

import io.pivotal.workshop.redvsblue.game.Ball;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
public class BallHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BallHandler.class);

    private final ScoringService scoringService;

    public BallHandler(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @StreamListener("balls")
    public void handleBallThrown(Ball ball) {
        if (ball == null) {
            throw new IllegalArgumentException("Ball should not be null");
        }

        LOGGER.debug("Ball thrown from [{}] to [{}]", ball.getThrower(), ball.getTarget());

        if (this.scoringService.isValidThrow(ball)) {
            this.scoringService.registerHit(ball.getThrower());
        } else {
            LOGGER.warn("Invalid throw!");
        }
    }
}
