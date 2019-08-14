package io.pivotal.workshop.redvsblue.game.scoring;

import io.pivotal.workshop.redvsblue.game.Ball;
import io.pivotal.workshop.redvsblue.game.Player;
import io.pivotal.workshop.redvsblue.game.PlayerRepository;
import io.pivotal.workshop.redvsblue.game.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ScoringService {

    private static final Random RANDOM = new Random();

    private static final Logger LOGGER = LoggerFactory.getLogger(ScoringService.class);

    private final PlayerRepository playerRepository;
    private final ScoringProperties scoringProperties;

    public ScoringService(PlayerRepository playerRepository, ScoringProperties scoringProperties) {
        this.playerRepository = playerRepository;
        this.scoringProperties = scoringProperties;
    }

    public List<Player> getRankingForTeam(Team team) {
        return this.playerRepository.findFirst5ByTeam(team, Sort.by(Sort.Direction.DESC, "score"));
    }

    public List<Player> getOverallRanking() {
        return this.playerRepository.findAll(Sort.by(Sort.Direction.DESC, "score"));
    }

    @Transactional
    public void resetScores() {
        this.playerRepository.resetScores();
    }

    public Score getScore() {
        if (this.playerRepository.countPlayerByTeam(Team.RED) == 0 || this.playerRepository.countPlayerByTeam(Team.RED) == 0) {
            return null;
        }
        return new Score(this.playerRepository.teamScoreOf(Team.RED), this.playerRepository.teamScoreOf(Team.BLUE));
    }

    public boolean isValidThrow(@NotNull Ball ball) {
        if (ball.getThrower() == null || ball.getTarget() == null) {
            LOGGER.warn("Ball from [{}] to [{}] is invalid", ball.getThrower(), ball.getTarget());
            return false;
        } else {
            Optional<Player> throwerRecord = this.playerRepository.findById(ball.getThrower().getId());
            Optional<Player> targetRecord = this.playerRepository.findById(ball.getTarget().getId());

            return throwerRecord.map(thrower -> targetRecord.map(target -> {
                if (thrower.getTeam() != target.getTeam()) {
                    LOGGER.info("Checking Ball thrown from [{}] to [{}]...", thrower.getName(), target.getName());
                    return RANDOM.nextInt(100 / this.scoringProperties.getOdds()) == 0;
                } else {
                    return false;
                }
            }).orElse(false)).orElse(false);
        }
    }

    @Transactional
    public void registerHit(Player thrower) {
        LOGGER.info("HIT!");
        this.playerRepository.givePointTo(thrower);
    }
}
