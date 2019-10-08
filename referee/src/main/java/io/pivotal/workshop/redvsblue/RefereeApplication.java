package io.pivotal.workshop.redvsblue;

import io.pivotal.workshop.redvsblue.game.Player;
import io.pivotal.workshop.redvsblue.game.PlayerRepository;
import io.pivotal.workshop.redvsblue.game.Team;
import io.pivotal.workshop.redvsblue.game.scoring.ScoringProperties;
import io.pivotal.workshop.redvsblue.game.scoring.messaging.InputChannels;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableBinding(InputChannels.class)
@EnableConfigurationProperties(ScoringProperties.class)
public class RefereeApplication {

    public RefereeApplication(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(RefereeApplication.class, args);
    }

    private final PlayerRepository playerRepository;

    @Bean
    @Profile("dev")
    public CommandLineRunner commandLineRunner() {
        return args -> {
            this.playerRepository.save(new Player("Dieter Hubau", Team.RED, 50L));
            this.playerRepository.save(new Player("Andreas Evers", Team.RED, 100L));
            this.playerRepository.save(new Player("David Carron", Team.RED, 20L));
            this.playerRepository.save(new Player("Rouven Besters", Team.RED, 80L));
            this.playerRepository.save(new Player("Niels Teekens", Team.RED, 75L));

            this.playerRepository.save(new Player("Patrick Steiner", Team.BLUE, 1L));
            this.playerRepository.save(new Player("Lars Rosenquist", Team.BLUE, 2L));
            this.playerRepository.save(new Player("Jan-Willem Van Buuren", Team.BLUE, 5L));
            this.playerRepository.save(new Player("Wouter Sliedrecht", Team.BLUE, 67L));
            this.playerRepository.save(new Player("Jürgen Hoffmann", Team.BLUE, 73L));
        };
    }
}
