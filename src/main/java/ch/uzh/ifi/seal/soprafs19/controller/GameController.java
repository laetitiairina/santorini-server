package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.service.GameService;
import ch.uzh.ifi.seal.soprafs19.service.BoardService;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameController {

    private final GameService gameService;

    private final BoardService boardService;

    private final PlayerService playerService;

    GameController(GameService gameService, BoardService boardService, PlayerService playerService) {
        this.gameService = gameService;
        this.boardService = boardService;
        this.playerService = playerService;
    }

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
    ResponseEntity getPlayer(@PathVariable("id") Long id, @RequestParam("fields") String fields) {

        // TODO: get game by id

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/games/{id}")
    ResponseEntity updatePlayer(@PathVariable Long id) {

        // TODO: update game by id

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
