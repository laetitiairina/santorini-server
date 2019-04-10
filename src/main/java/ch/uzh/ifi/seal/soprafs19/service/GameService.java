package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;


    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    /*
    public Iterable<Game> getGames() {
        return this.gameRepository.findAll();
    }
    */

    public Game createGame() {
        Game newGame = new Game();

        // TODO: set properties of newGame

        gameRepository.save(newGame);
        log.debug("Created Information for Game: {}", newGame);
        return newGame;
    }



}
