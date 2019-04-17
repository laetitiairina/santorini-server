package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs19.rules.IRuleSet;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.apache.tomcat.util.digester.Rules;
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

    /**
     * Create a new game
     * @param newGame
     * @return
     */
    public Game createGame(Game newGame) {
        gameRepository.save(newGame);
        log.debug("Created Information for Game: {}", newGame);
        return newGame;
    }

    public void updateGame(Game newGame) {
        // get the current game from repository
        long id = newGame.getId();
        Game currentGame = gameRepository.findById(id);

        // react to update depending on status
        Boolean successfullyUpdated = false; // set to true later, if update is valid
        IRuleSet rules= new SimpleRuleSet();
        switch (currentGame.getStatus()) {
            case CARDS10:
                //
                break;
            case CARDS2:
                //
                break;
            case STARTPLAYER:
                //
                break;
            case COLOR1:
                //
                break;
            case COLOR2:
                //
                break;
            case POSITION1:
                //
                break;
            case POSITION2:
                //
                break;
            case MOVE:
                if (rules.checkMovePhase(currentGame, newGame)) {
                    //
                    successfullyUpdated = true;
                }
                break;
            case BUILD:
                //
                break;
            case END:
                //
                break;
        }

        // update the status of the game for pinging
        if (successfullyUpdated) {
            incrementGameStatus(currentGame, currentGame.getIsGodMode(), false); // isEnd may vary
        }
    }

    /**
     * increments the game status to next level
     * @param game
     * @param isGodMode : Is the game played in God Mode?
     * @param isEnd : Has the game reached it's end?
     */
    public void incrementGameStatus (Game game, Boolean isGodMode,Boolean isEnd) {
        // convert type GameStatus to type Integer
        int status = game.getGameStatusInt(game.getStatus());

        // increment the status
        if (status == 9 && !isEnd) {
            // loop for move and build phase
            --status;
        }
        else {
           ++status;
        }

        // convert type Integer it back to type GameStatus
        game.setStatus(game.getGameStatusEnum(status));

        // save the new status
        gameRepository.save(game);
    }

}
