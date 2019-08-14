package io.pivotal.workshop.redvsblue.redplayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class PlayerCreator implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerCreator.class);

    private final RestTemplate restTemplate;

    public PlayerCreator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        Player red = new Player(null, "Stupid Cunt", 0L, Team.RED);

        try {
            ResponseEntity<Player> response = this.restTemplate.postForEntity("http://localhost:8080/player", red, Player.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Player [{}] with ID [{}] created successfully", response.getBody().getName(), response.getBody().getId());
            }
        } catch (RestClientException ex) {
            LOGGER.warn("Could not create player [" + red.getName() + "]");
        }
    }
}
