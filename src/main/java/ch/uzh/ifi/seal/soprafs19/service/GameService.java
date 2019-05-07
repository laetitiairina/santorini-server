package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;
import ch.uzh.ifi.seal.soprafs19.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

@Primary
@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private SimpleRuleSet simpleRuleSet;

    /*
    @Autowired
    public GameService(GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }
    */

    /*
    public Iterable<Game> getGames() {
        return this.gameRepository.findAll();
    }
    */

    /**
     * Get game by id
     *
     * @param id
     * @return
     */
    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }

    /**
     * Create a new game
     *
     * @param newGame
     */
    public void createGame(Game newGame) {
        gameRepository.save(newGame);
        log.debug("Created Information for Game: {}", newGame);
    }

    /**
     * updates game information
     * @param currentGame
     * @param updatedGame
     * @return
     */
    public boolean updateGame(Game currentGame, Game updatedGame) {

        // react to update depending on status
        Game successfullyUpdatedGame = null; // set to true later, if update is valid
        boolean isBadRequest = true; // true if the move / build valid but JSON, etc. was incorrect

        // only checks the first 3 states, if isGodMode is true
        if (currentGame.getIsGodMode()) {
            successfullyUpdatedGame = setGodModeInit(currentGame, updatedGame);
        }

        // check the remaining states
        switch (currentGame.getStatus()) {
            case COLOR1:
            case COLOR2:
                successfullyUpdatedGame = setColor(currentGame, updatedGame);
                break;
            case POSITION1:
            case POSITION2:
                successfullyUpdatedGame = setPosition(currentGame, updatedGame);
                break;
            case MOVE:
                // TODO: include isBadRequest handling, add check logic (low priority)
                // check if it's a valid move
                if (simpleRuleSet.checkMovePhase(currentGame, updatedGame)) {
                successfullyUpdatedGame = move(currentGame, updatedGame);
                }
                break;
            case BUILD:
                // TODO: include isBadRequest handling, add check logic (low priority)
                // check if it's a valid build
                if (simpleRuleSet.checkBuildPhase(currentGame, updatedGame)) {
                successfullyUpdatedGame = build(currentGame, updatedGame);
                }
                break;
        }

        // update the status of the game for pinging
        if (successfullyUpdatedGame != null) {

            // saves updates to database
            gameRepository.save(successfullyUpdatedGame);


            // increment the status

                if (simpleRuleSet.checkWinCondition(successfullyUpdatedGame)) {
                    incrementGameStatus(successfullyUpdatedGame, true);
                } else {
                    incrementGameStatus(successfullyUpdatedGame, false);
                }
            return true;
        } else {
            /*
             * TODO:
             * Only return false if request itself was bad (e.g. updatedGame contained invalid JSON),
             * return true even if the turn was invalid but request/updatedGame was ok
             */
            return !isBadRequest;
        }
    }

    /**
     *  handles the states only used by the god mode
     * @param currentGame
     * @param updatedGame
     * @return
     */
    public Game setGodModeInit(Game currentGame, Game updatedGame) {
        switch (currentGame.getStatus()) {
            case CARDS1:
                return setCards1(currentGame, updatedGame);
            case CARDS2:
                return setCards2(currentGame, updatedGame);
            case STARTPLAYER:
                return setStartPlayer(currentGame, updatedGame);
        }
        return null;
    }

    /**
     * sets the two cards in the game at status CARDS1
     *
     * @param currentGame
     * @param updatedGame
     * @return
     */
    public Game setCards1(Game currentGame, Game updatedGame) {

        // front-end has to send exactly 2 cards
        if (updatedGame.getCards().size() == 2 && updatedGame.getCards().get(0) != updatedGame.getCards().get(1)) {
            // set cards
            currentGame.setCards(updatedGame.getCards());

            // other player is now current player
            nextTurn(currentGame);
            return currentGame;
        }
        return null;
    }

    /**
     * sets the two cards in the player entities at status CARDS2
     *
     * @param currentGame
     * @param updatedGame
     * @return
     */
    public Game setCards2(Game currentGame, Game updatedGame) {

        // front-end has to send exactly 1 player and an existing card
        if (updatedGame.getPlayers().size() == 1 && updatedGame.getPlayers().get(0).getCard() != null) {
            // get cards
            SimpleGodCard chosenCard = updatedGame.getPlayers().get(0).getCard();
            List<SimpleGodCard> currentCards = currentGame.getCards();

            // get players
            List<Player> currentPlayers = currentGame.getPlayers();
            long id = updatedGame.getPlayers().get(0).getId();
            Player currentPlayer = playerRepository.findById(id);

            // check if the chosenCard is one of the 2 currentCards
            // and the currentPlayer is one of the two currentPlayers and the Challenger
            if (currentCards.contains(chosenCard) && currentPlayers.contains(currentPlayer) && currentPlayer.getIsCurrentPlayer()) {
                currentCards.remove(chosenCard);
                currentPlayers.remove(currentPlayer);

                // now currentPlayers only contains the opponent
                // and currentCards only contains the other card
                currentPlayers.get(0).setCard(currentCards.get(0));
                currentPlayer.setCard(chosenCard);

                // add again
                currentPlayers.add(currentPlayer);
                currentCards.add(chosenCard);

                // other player is now current player
                nextTurn(currentGame);
                return currentGame;
            }
        }
        return null;
    }

    /**
     * sets the Start Player in God Mode
     * @param currentGame
     * @param updatedGame
     * @return
     */
    public Game setStartPlayer(Game currentGame, Game updatedGame) {
        Player currentPlayer = updatedGame.getPlayers().get(0);
        List<Player> players = currentGame.getPlayers();

        if (currentPlayer.getIsCurrentPlayer()) { // && currentPlayer.getGame().getId() == currentGame.getId()
            long id = currentPlayer.getId();
            currentPlayer = playerRepository.findById(id);

            for (Player player : players) {
                if (player.getId().equals(currentPlayer.getId())) {
                    player.setIsCurrentPlayer(true);
                } else {
                    player.setIsCurrentPlayer(false);
                }
            }

            return currentGame;
        }
        return null;
    }

    /**
     * sets the color of a Player
     * @param currentGame
     * @param updatedGame
     * @return
     */
    public Game setColor(Game currentGame, Game updatedGame) {
        Player updatedPlayer = updatedGame.getPlayers().get(0);

        if (updatedGame.getPlayers().size() == 1 && updatedPlayer.getIsCurrentPlayer()) {
            long id = updatedPlayer.getId();
            for (Player player : currentGame.getPlayers()) {
                if (player.getId() == id) {
                    if (updatedPlayer.getColor() != null) {
                        player.setColor(updatedPlayer.getColor());
                    } else {
                        return null;
                    }
                }
            }
            if (currentGame.getPlayers().get(0).getColor() != currentGame.getPlayers().get(1).getColor()) {
                return currentGame;
            }
        }
        return null;
    }

    /**
     * sets the position of the two workers in the beginning
     * @param currentGame
     * @param updatedGame
     * @return
     */
    public Game setPosition(Game currentGame, Game updatedGame) {
        List<Long> workerIds = new ArrayList<>();

        for (Field updatedField : updatedGame.getBoard().getFields()) {

            // find field in back-end game
            Field currentField = getFieldToUpdate(currentGame, updatedField);

            if (currentField != null && currentField.getWorker() == null) {

                // only works if it's the current Player
                for (Player player : currentGame.getPlayers()) {
                    if (player.getIsCurrentPlayer()) {
                        for (Worker worker : player.getWorkers()) {
                            if (worker.getId().equals(updatedField.getWorker().getId())) {
                                currentField.setWorker(worker);
                                workerIds.add(worker.getId());
                            }
                        }
                    }
                }
            }
        }
        // both fields need to be valid and two different workers have to be placed on them
        if (workerIds.size() == 2 && !workerIds.get(0).equals(workerIds.get(1))) {
            nextTurn(currentGame);
            return currentGame;
        } else {
            return null;
        }
    }

    /**
     * updates the position of a worker
     *
     * @param currentGame
     * @param updatedGame
     * @return updated game
     */
    public Game move(Game currentGame, Game updatedGame) {
        Worker currentWorker = null;
        int blocksBefore = -1, blocksAfter = -1;
        Field fieldAfter = null;

        for (Field field : updatedGame.getBoard().getFields()) {

            // find field that needs to be updated
            Field fieldToUpdate = getFieldToUpdate(currentGame, field);

            // update the worker value of the field and remember current Worker
            if (field.getWorker() != null) {
                currentWorker = field.getWorker();
                blocksBefore = fieldToUpdate.getBlocks();
                fieldAfter = fieldToUpdate;
            } else {
                blocksAfter = fieldToUpdate.getBlocks();
            }
            fieldToUpdate.setWorker(field.getWorker());
        }

        currentGame.setHasMovedUp((blocksBefore < blocksAfter));

        //  set the right worker as isCurrentWorker for build phase
        for (Player player : currentGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                for (Worker worker : player.getWorkers()) {
                    if (worker.getId().equals(currentWorker.getId())) {
                        worker.setIsCurrentWorker(true);
                        worker.setField(fieldAfter);
                    } else {
                        worker.setIsCurrentWorker(false);
                    }
                }
            }
        }

        return currentGame;
    }

    /**
     * updates the number of blocks or existence of a dome on a field
     *
     * @param currentGame
     * @param updatedGame
     * @return updated game
     */
    public Game build(Game currentGame, Game updatedGame) {
        for (Field field : updatedGame.getBoard().getFields()) {
            // find field that needs to be updated
            Field fieldToUpdate = getFieldToUpdate(currentGame, field);

            // update the blocks and has Dome value of the field
            fieldToUpdate.setBlocks(field.getBlocks());
            fieldToUpdate.setHasDome(field.getHasDome());

            // set both workers to non-current
            for (Player player : currentGame.getPlayers()) {
                if (player.getIsCurrentPlayer()) {
                    for (Worker worker : player.getWorkers()) {
                        worker.setIsCurrentWorker(false);
                    }
                }
            }
        }
        nextTurn(currentGame);
        return currentGame;
    }

    /**
     * increments the game status to next level
     *
     * @param game
     * @param isEnd : Has the game reached it's end?
     */
    public void incrementGameStatus(Game game, Boolean isEnd) {
        // convert type GameStatus to type Integer
        int status = game.getGameStatusInt(game.getStatus());

        // increment the status
        if (status == 8 && !isEnd) {
            // loop for move and build phase
            status--;
        } else {
            status++;
        }

        // convert type Integer it back to type GameStatus
        game.setStatus(game.getGameStatusEnum(status));

        // save the new status
        gameRepository.save(game);
    }

    /**
     * switches who's the current Player
     * @param game
     */
    public void nextTurn(Game game) {
        for (Player player : game.getPlayers()) {
            // reverse value
            player.setIsCurrentPlayer(!player.getIsCurrentPlayer());
        }
        // save
        gameRepository.save(game);
    }

    /**
     * Check if token matches the current player in the current game
     *
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

    /**
     * finds to field to be updated
     * @param game
     * @param field
     * @return
     */
    private Field getFieldToUpdate(Game game, Field field) {
        Field fieldToUpdate = null;
        for (Field currentField : game.getBoard().getFields()) {
            if (currentField.getId().equals(field.getId())) {
                fieldToUpdate = currentField;
            }
        }
        return fieldToUpdate;
    }
}
