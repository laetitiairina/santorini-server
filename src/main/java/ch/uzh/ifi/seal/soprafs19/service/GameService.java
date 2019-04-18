package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs19.rules.IRuleSet;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.apache.tomcat.util.digester.Rules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Primary
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

    public void updateGame(Game updatedGame) {
        // get the current game from repository
        long id = updatedGame.getId();
        Game currentGame = gameRepository.findById(id);

        // Todo: look at how to use correctly
        IRuleSet rules= new SimpleRuleSet();

        // react to update depending on status
        boolean successfullyUpdated = false; // set to true later, if update is valid
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
                // check if it's a valid move
                if (rules.checkMovePhase(currentGame, updatedGame)) {
                    for (Field field : updatedGame.getBoard().getFields()) {
                        // find field that needs to be updated
                        long fieldId = field.getId();
                        Field fieldToUpdate = getFieldById(currentGame, fieldId);

                        // update the worker values of the field
                        fieldToUpdate.setWorker(field.getWorker());
                    }
                    gameRepository.save(currentGame);
                    successfullyUpdated = true;
                }
                break;
            case BUILD:
                if (rules.checkBuildPhase(currentGame, updatedGame)) {
                    //
                    successfullyUpdated = true;
                }
                break;
            case END:
                //
                break;
        }

        // update the status of the game for pinging
        if (successfullyUpdated) {
            if (rules.checkWinCondition(currentGame, updatedGame)) {
                incrementGameStatus(currentGame, currentGame.getIsGodMode(), true);
            } else {
                incrementGameStatus(currentGame, currentGame.getIsGodMode(), false);
            }
        }
    }

    /**
     * get Field by Id
     * @param game
     * @param fieldId
     * @return
     */
    public Field getFieldById (Game game, long fieldId) {
        for (Field field : game.getBoard().getFields()) {
            if (field.getId() == fieldId) {
                return field;
            }
        }
        return null;
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
