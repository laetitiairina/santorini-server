package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/players/{id}")
    ResponseEntity getPlayer(@PathVariable Long id, @RequestParam("fields") String fields) {

        // TODO: get player by id

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/players/{id}")
    ResponseEntity updatePlayer(@PathVariable Long id) {

        // TODO: update player by id

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
