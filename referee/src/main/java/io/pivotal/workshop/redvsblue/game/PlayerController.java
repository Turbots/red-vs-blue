package io.pivotal.workshop.redvsblue.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/player")
public class PlayerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerController.class);

    private final PlayerRepository playerRepository;

    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @PostMapping
    public ResponseEntity createPlayer(@RequestBody @Valid Player player) {
        LOGGER.info("Creating player [{}]", player);

        if (this.playerRepository.existsByName(player.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Player [" + player.getName() + "] already exists");
        }

        return ResponseEntity.accepted().body(this.playerRepository.save(player));
    }

    @DeleteMapping
    public ResponseEntity deletePlayer(@RequestParam("id") Long id) {
        LOGGER.info("Deleting player with ID [{}]", id);

        this.playerRepository.findById(id).ifPresent(this.playerRepository::delete);

        return ResponseEntity.ok().build();
    }
}
