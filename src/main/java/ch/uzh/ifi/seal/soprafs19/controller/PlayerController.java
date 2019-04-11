package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class PlayerController {

    @Autowired
    private PlayerService service;

    /*
    PlayerController(PlayerService service) {
        this.service = service;
    }
    */

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
    ResponseEntity getPlayer(@PathVariable Long id, @RequestParam(required = false) List<String> fields) {

        // TODO: Authentication: Check if token sent in header matches token stored in player with requested id

        // Get player by id
        Optional<Player> player = service.getPlayerById(id);

        // Check if player exists
        if (player.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player was not found!");
        }

        // If specific field was requested, only send that field of player entity
        if (fields != null) {
            for (String param : fields) {
                // TODO: Return only requested parameters of player entity
                return ResponseEntity.ok(player.get());
            }
        }

        // Send response 200
        return ResponseEntity.ok(player.get());
    }

    @PutMapping("/players/{id}")
    ResponseEntity updatePlayer(@PathVariable Long id) {

        // TODO: update player by id

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
