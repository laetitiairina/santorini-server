package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameController {

    @Autowired
    private GameService service;

    /*
    GameController(GameService gameService) {
        this.service = gameService;
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
    ResponseEntity getGame(@PathVariable("id") Long id, @RequestParam(required = false) List<String> fields) {

        // TODO: get game by id


        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/games/{id}")
    ResponseEntity updateGame(@PathVariable Long id, @RequestBody Game game) {
        service.updateGame(game);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
