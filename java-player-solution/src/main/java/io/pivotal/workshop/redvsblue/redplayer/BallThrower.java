package io.pivotal.workshop.redvsblue.redplayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class BallThrower {

    private static final Logger LOGGER = LoggerFactory.getLogger(BallThrower.class);
    private static final Random RANDOM = new Random();

    private final RestTemplate restTemplate;
    private final OutputChannels outputChannels;
    private final RedPlayerProperties properties;

    public BallThrower(RestTemplate restTemplate, OutputChannels outputChannels, RedPlayerProperties properties) {
        this.restTemplate = restTemplate;
        this.outputChannels = outputChannels;
        this.properties = properties;
    }

    @Scheduled(fixedRate = 5000)
    public void throwBall() {
        ResponseEntity<List<Player>> response = restTemplate.exchange(
                properties.getRefereeUrl() + "/score/ranking/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Player>>() {
                });
        List<Player> players = response.getBody();

        if (players == null || players.isEmpty()) {
            LOGGER.info("Can't throw a ball when there are no players...");
        } else {
            List<Player> teamRed = players.stream().filter(player -> player.isTeam(Team.RED)).collect(Collectors.toList());
            List<Player> teamBlue = players.stream().filter(player -> player.isTeam(Team.BLUE)).collect(Collectors.toList());

            teamRed.forEach(shooter -> {
                Player target = teamBlue.get(RANDOM.nextInt(teamBlue.size()));

                LOGGER.info("Throwing ball from [{}] to [{}]", shooter.getName(), target.getName());
                this.outputChannels.output().send(new GenericMessage<>(new Ball(shooter, target)));
            });
        }
    }
}
