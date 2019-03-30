package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {

    private final PlayerService service;

    PlayerController(PlayerService service) {
        this.service = service;
    }

    /*
    @GetMapping("/players")
    Iterable<Player> all() {
        return service.getPlayer();
    }
    */

    @PostMapping("/players")
    Player createPlayer(@RequestBody Player newPlayer) {
        return this.service.createPlayer(newPlayer);
    }
}
