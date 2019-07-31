package io.pivotal.workshop.redvsblue.game;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {

    @PostMapping("/player")
    public void createPlayer() {

    }
}
