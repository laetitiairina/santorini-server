package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
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

import java.util.Optional;

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
     * Get game by id
     * @param id
     * @return
     */
    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }

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

    public boolean updateGame(Game currentGame, Game updatedGame) {
        // Authentication and checks done in GameController

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
            return true;
        }

        /*
         * TODO:
         * Only return false if request itself was bad (e.g. updatedGame contained invalid JSON),
         * return true even if the turn was invalid but request/updatedGame was ok
         */
        //return false;
        return true;
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

    /**
     * Check if token matches the current player in the current game
     * @param currentGame
     * @param token
     * @return
     */
    public boolean checkPlayerAuthentication(Game currentGame, String token) {
        for (Player player : currentGame.getPlayers()) {
            if (player.getToken().equals(token)) {
                if (player.getIsCurrentPlayer()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
}
