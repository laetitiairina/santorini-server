package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.helper.JsonHelper;
import ch.uzh.ifi.seal.soprafs19.service.GameService;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class GameController {

    @Autowired
    private GameService service;

    @Autowired
    private JsonHelper helper;

    /*
    GameController(GameService gameService, JsonHelper helper) {
        this.service = gameService;
        this.helper = helper;
    }
    */

    /*
    @GetMapping("/games")
    Iterable<Game> all() {
        return service.getGames();
    }
    */

    /*
    @PostMapping("/games")
    Game createGame(@RequestBody Game newGame) {
        return this.service.createUser(newGame);
    }
    */

    @GetMapping("/games/{id}")
    ResponseEntity getGame(@RequestHeader("Token") String token, @PathVariable("id") Long id, @RequestParam(required = false) List<String> fields) {

        // Get game by id
        Optional<Game> game = service.getGameById(id);

        // Check if game exists
        if (game.isEmpty()) {
            // Send response 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game was not found!");
        } else {
            // Update polling
            service.updatePolling(token);
        }

        // If specific fields were requested, only send those fields of game entity
        if (fields != null) {
            // Send response 200
            return ResponseEntity.ok(helper.getFilteredObjectAsJsonNode(game.get(), fields));
        }

        // Send response 200
        return ResponseEntity.ok(game.get());
    }

    @PutMapping("/games/{id}")
    ResponseEntity updateGame(@RequestHeader("Token") String token, @PathVariable Long id, @RequestBody Game updatedGame) {

        // Get game by id
        Optional<Game> currentGame = service.getGameById(id);

        // Check if game exists
        if (currentGame.isEmpty()) {
            // Send response 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game was not found!");
        }

        // Check if requested id is same as updatedGame id
        if (!currentGame.get().getId().equals(updatedGame.getId())) {
            // Send response 400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request!");
        }

        // Check if request came from the current player in the current game
        if (!service.checkPlayerAuthentication(currentGame.get(), token)) {
            // Send response 403
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthenticated request!");
        }

        if (service.updateGame(currentGame.get(), updatedGame)) {
            // Send response 204
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            // Send response 400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request!");
        }
    }
}
