package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.helper.JsonHelper;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
public class PlayerController {

    @Autowired
    private PlayerService service;

    @Autowired
    private JsonHelper helper;

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
    ResponseEntity createPlayer(@RequestBody Player newPlayer) {

        // Create player
        Player player = service.createPlayer(newPlayer,true);

        // Check if player was created successfully
        if (player == null) {
            // Send response 409
            return ResponseEntity.status(HttpStatus.CONFLICT).body("There was a conflict while creating a player!");
        }

        // Transform player to json node
        JsonNode playerNode = helper.objectToJsonNode(player);
        //JsonNode playerNode = helper.getFilteredObjectAsJsonNode(player, Arrays.asList("id"));

        // Manually add token since JsonIgnore is set for it
        ((ObjectNode) playerNode).put("token",player.getToken());

        // Send response 201
        return ResponseEntity.status(HttpStatus.CREATED).body(playerNode);
    }

    @GetMapping("/players/{id}")
    ResponseEntity getPlayer(@PathVariable Long id, @RequestParam(required = false) List<String> fields) {

        // Get player by id
        Optional<Player> player = service.getPlayerById(id);

        // Check if player exists
        if (player.isEmpty()) {
            // Send response 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player was not found!");
        } else {
            // Update polling
            player.get().didPoll();
        }

        // If specific fields were requested, only send those fields of player entity
        if (fields != null) {
                // Send response 200
                return ResponseEntity.ok(helper.getFilteredObjectAsJsonNode(player.get(), fields));
        }

        // Send response 200
        return ResponseEntity.ok(player.get());
    }


    @PutMapping("/players/{id}")
    ResponseEntity updatePlayer(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
