package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.Color;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import ch.uzh.ifi.seal.soprafs19.entity.*;
import ch.uzh.ifi.seal.soprafs19.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs19.rules.IRuleSet;
import ch.uzh.ifi.seal.soprafs19.rules.RuleFactory;
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

    private GameRepository gameRepository;

    private PlayerRepository playerRepository;

    private RuleFactory ruleFactory;

    @Autowired
    public GameService(GameRepository gameRepository, PlayerRepository playerRepository, RuleFactory ruleFactory) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.ruleFactory = ruleFactory;
    }

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

        // if they want to rematch
        if (currentGame.getStatus() == GameStatus.END && rematch(currentGame, updatedGame)) {
            return true;
        }

        // react to update depending on status
        Game successfullyUpdatedGame = null; // set to true later, if update is valid
        IRuleSet currentPlayerRules = null;
        IRuleSet opponentPlayerRules = null;

        //reset message on game
        currentGame.setMessage(null);
        saveGame(currentGame);

        // get rules
        for (Player player : currentGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                currentPlayerRules = ruleFactory.getRuleSet(player);
            }
            else opponentPlayerRules = ruleFactory.getRuleSet(player);
        }

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
                if (currentPlayerRules.checkMovePhase(currentGame, updatedGame)) {
                    if (opponentPlayerRules.checkMovePhaseOpponent(currentGame, updatedGame)) {
                        successfullyUpdatedGame = move(currentGame, updatedGame);
                    }
                    else {
                        currentGame.setMessage("Move invalid because of opponents god power. Try again!");
                        saveGame(currentGame);
                    }

                }
                break;
            case BUILD:
                // TODO: include isBadRequest handling, add check logic (low priority)
                // check if it's a valid build
                if (currentPlayerRules.checkBuildPhase(currentGame, updatedGame)) {
                successfullyUpdatedGame = build(currentGame, updatedGame);
                }
                break;
        }

        // update the status of the game for pinging
        if (successfullyUpdatedGame != null) {
            // check if a player has won
            Player winner = currentPlayerRules.checkWinCondition(successfullyUpdatedGame);
            if (winner == null) {
                // increment the status normally
                incrementGameStatus(successfullyUpdatedGame, false);
            } else {
                // indicate winner and loser
                for (Player p : successfullyUpdatedGame.getPlayers()) {
                    if (p.getId().equals(winner.getId())) {
                        p.setIsCurrentPlayer(true);
                    } else {
                        p.setIsCurrentPlayer(false);
                    }
                    // Set players to inactive (for CheckPolling)
                    p.setIsActive(false);
                }
                // increment the status to END
                incrementGameStatus(successfullyUpdatedGame, true);
            }

            // saves updates to database
            gameRepository.save(successfullyUpdatedGame);

            return true;
        } else {
            return false;
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

        boolean isPlayer = false;
        for (Player player : currentGame.getPlayers()) {
            if (player.getId().equals(currentPlayer.getId())) {
                isPlayer = true;
            }
        }
        if (!isPlayer) {
            return null;
        }

        if (currentPlayer.getIsCurrentPlayer()) {
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

        List<Worker> workers = new ArrayList<>();
        List<Field> fields = new ArrayList<>();

        for (Field updatedField : updatedGame.getBoard().getFields()) {

            // find field in back-end game
            Field currentField = getFieldToUpdate(currentGame, updatedField);

            // field is valid & empty
            if (currentField != null && currentField.getWorker() == null
                    // not same field
                    && (fields.size() == 0 || !fields.get(0).equals(currentField))) {

                for (Player player : currentGame.getPlayers()) {
                    // only works if it's the current Player
                    if (player.getIsCurrentPlayer()) {
                        for (Worker worker : player.getWorkers()) {
                            if (worker.getId().equals(updatedField.getWorker().getId())) {
                                fields.add(currentField);
                                workers.add(worker);
                            }
                        }
                    }
                }
            }
        }

        // both fields need to be valid and two different workers have to be placed on them
        if (workers.size() == 2 && !workers.get(0).getId().equals(workers.get(1).getId())
                && fields.size() == 2 && !fields.get(0).getId().equals(fields.get(1).getId())) {
            fields.get(0).setWorker(workers.get(0));
            fields.get(1).setWorker(workers.get(1));
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
        Field fieldBefore = null, fieldAfter = null;

        for (Field field : updatedGame.getBoard().getFields()) {

            // find field that needs to be updated
            Field fieldToUpdate = getFieldToUpdate(currentGame, field);

            // update the worker value of the field and remember current Worker

            if (field.getWorker() != null) {
                currentWorker = field.getWorker();
                blocksAfter = fieldToUpdate.getBlocks();
                fieldAfter = fieldToUpdate;
            } else {
                fieldBefore = fieldToUpdate;
                blocksBefore = fieldToUpdate.getBlocks();
            }
        }

        if (fieldAfter == null || fieldBefore == null) {
            return null;
        }

        currentGame.setBlockDifference((blocksAfter - blocksBefore));

        //  set the right worker as isCurrentWorker for build phase
        for (Player player : currentGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                for (Worker worker : player.getWorkers()) {
                    if (worker.getId().equals(currentWorker.getId())) {
                        worker.setIsCurrentWorker(true);

                        // switch of worker is happening, only with Apollo card
                        if(fieldAfter.getWorker() != null && currentGame.getCurrentPlayer().getCard() == SimpleGodCard.APOLLO) {
                            Worker opponentWorker = fieldAfter.getWorker();
                            opponentWorker.setField(fieldBefore);
                            fieldBefore.setWorker(opponentWorker);
                        }
                        //push opponent worker one field, only with Minotaur card
                        else if(fieldAfter.getWorker() != null && currentGame.getCurrentPlayer().getCard() == SimpleGodCard.MINOTAUR){
                            Worker opponentWorker = fieldAfter.getWorker();
                            fieldBefore.setWorker(null);
                            int deltaX = 0;
                            int deltaY = 0;
                            //move left
                            if(opponentWorker.getField().getPosX() < worker.getField().getPosX()){
                                deltaX = -1;
                            }
                            //move right
                            else if(opponentWorker.getField().getPosX() > worker.getField().getPosX()) {
                                deltaX = 1;
                            }
                            // move down
                            if (opponentWorker.getField().getPosY() < worker.getField().getPosY()) {
                                deltaY = -1;
                            }// move up
                            if (opponentWorker.getField().getPosY() > worker.getField().getPosY()) {
                                deltaY = 1;
                            }
                            moveWorker(currentGame, opponentWorker, deltaX, deltaY);
                        }
                        // field was empty
                        else if(fieldAfter.getWorker() == null) {
                            fieldBefore.setWorker(null);
                        }

                        worker.setField(fieldAfter);
                        fieldAfter.setWorker(worker);
                    } else {
                        worker.setIsCurrentWorker(false);
                    }
                }
            }
        }
        return currentGame;
    }

    public boolean moveWorker(Game currentGame, Worker worker, int deltaX, int deltaY) {
        Field targetField = currentGame.getBoard().getFieldByCoordinates(worker.getField().getPosX() + deltaX, worker.getField().getPosY() + deltaY);
        if (targetField != null) {
            worker.setField(targetField);
            targetField.setWorker(worker);
            return true;
        }
        return false;
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
        }
            // set both workers to non-current
            for (Player player : currentGame.getPlayers()) {
                if (player.getIsCurrentPlayer()) {
                    for (Worker worker : player.getWorkers()) {
                        worker.setIsCurrentWorker(false);
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
        if (isEnd) {
            status = 9;
        } else if (status == 8) {
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

    public void abortGame(Game game) {
        // let game end inform front-end of abort
        for (Player player : game.getPlayers()) {
            player.setIsCurrentPlayer(false);
            player.setIsActive(false);
        }
        game.setStatus(GameStatus.END);
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
     * Update polling of player with token
     * @param token
     */
    public void updatePolling(String token) {
        // update the polls
        Player player = playerRepository.findByToken(token);
        if (player != null) {
            player.didPoll();
            playerRepository.save(player);
        }
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

    /**
     * reinitialize game
     * @param currentGame, updatedGame
     */
    public Boolean rematch(Game currentGame, Game updatedGame) {

        // update the  player's wantsRematch variable
        for (Player player : updatedGame.getPlayers()) {
            if (player.getWantsRematch()) {
                for (Player p : currentGame.getPlayers()) {
                    if (p.getId().equals(player.getId()) && player.getWantsRematch()) {
                        p.setWantsRematch(true);
                    }
                }
            }
        }
        saveGame(currentGame);

        // if both players want to rematch
        if (currentGame.getPlayers().get(0).getWantsRematch() && currentGame.getPlayers().get(1).getWantsRematch()) {

            // reset game variables
            currentGame.setStatus(currentGame.getIsGodMode() ? GameStatus.CARDS1 : GameStatus.COLOR1);
            currentGame.setCards(null);
            currentGame.setBlockDifference(0);
            currentGame.setBoard(new Board(currentGame, 5));

            // reset player & worker variables
            for (Player player : currentGame.getPlayers()) {
                player.setIsCurrentPlayer(false);
                player.setCard(null);
                player.setColor(null);
                player.setWantsRematch(false);

                // for polling
                player.setIsActive(true);
                player.didPoll();

                // worker
                for (Worker worker : player.getWorkers()) {
                    worker.setIsCurrentWorker(false);
                    worker.setField(null);
                }
            }

            // Start Player / Challenger
            currentGame.getPlayers().get(0).setIsCurrentPlayer(true);
            saveGame(currentGame);
            return true;
        }
        return false;
    }

    // M3: Fast-forward
    // TODO: Delete after M3
    /**
     * Fast-forward current game
     * @param currentGame
     * @return
     */
    public boolean fastforwardGame(Game currentGame) {
        System.out.println("fastforward");

        if(currentGame.getStatus() == GameStatus.END) {
            return false;
        }

        // Construct a game state that is near the end
        Integer[] blocksArr = {0,1,2,1,0, 0,2,3,2,1, 0,2,3,3,2, 1,0,0,2,0, 2,1,2,3,0};
        Boolean[] hasDomeArr = {false,false,false,false,false, false,false,false,false,false, false,false,true,false,false, false,false,false,false,false, false,false,false,true,false,};
        Integer[] workerPosArr = {2,18,3,11};
        List<Color> colorsArr = new ArrayList<Color>();
        colorsArr.add(Color.BLUE);
        colorsArr.add(Color.GREY);
        List<SimpleGodCard> cardsArr = new ArrayList<SimpleGodCard>();
        cardsArr.add(SimpleGodCard.APOLLO);
        cardsArr.add(SimpleGodCard.ARTEMIS);

        // If god mode, make sure cards are set
        if (currentGame.getIsGodMode()) {
            if (currentGame.getCards() == null) {
                currentGame.setCards(cardsArr);
            }
            for (Player player : currentGame.getPlayers()) {
                if (player.getCard() != null) {
                    if (cardsArr.contains(player.getCard())) {
                        cardsArr.remove(player.getCard());
                    }
                }
            }
            for (int i = 0; i < currentGame.getPlayers().size(); i++) {
                if (currentGame.getPlayers().get(i).getCard() == null) {
                    currentGame.getPlayers().get(i).setCard(cardsArr.get(0));
                    cardsArr.remove(0);
                }
            }
        }

        // Make sure colors are set
        for (Player player : currentGame.getPlayers()) {
            if (player.getColor() != null) {
                if (colorsArr.contains(player.getColor())) {
                    colorsArr.remove(player.getColor());
                }
            }
        }
        for (int i = 0; i < currentGame.getPlayers().size(); i++) {
            if (currentGame.getPlayers().get(i).getColor() == null) {
                currentGame.getPlayers().get(i).setColor(colorsArr.get(0));
                colorsArr.remove(0);
            }
        }

        // Set status
        if(currentGame.getStatus() == GameStatus.MOVE) {
            currentGame.setStatus(GameStatus.BUILD);
        } else {
            currentGame.setStatus(GameStatus.MOVE);
        }

        // Set fields
        for (int i = 0; i < currentGame.getBoard().getFields().size(); i++) {
            currentGame.getBoard().getFields().get(i).setBlocks(blocksArr[i]);
            currentGame.getBoard().getFields().get(i).setHasDome(hasDomeArr[i]);
            currentGame.getBoard().getFields().get(i).setWorker(null);
        }

        // Set workers
        for (Player player : currentGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                player.getWorkers().get(0).setIsCurrentWorker(true);
                player.getWorkers().get(1).setIsCurrentWorker(false);
                currentGame.getBoard().getFields().get(workerPosArr[0]).setWorker(player.getWorkers().get(0));
                currentGame.getBoard().getFields().get(workerPosArr[1]).setWorker(player.getWorkers().get(1));
            } else {
                player.getWorkers().get(0).setIsCurrentWorker(false);
                player.getWorkers().get(1).setIsCurrentWorker(false);
                currentGame.getBoard().getFields().get(workerPosArr[2]).setWorker(player.getWorkers().get(0));
                currentGame.getBoard().getFields().get(workerPosArr[3]).setWorker(player.getWorkers().get(1));
            }
        }

        gameRepository.save(currentGame);
        return true;
    }

    /**
     * saves game to DB
     * @param game
     */
    public void saveGame (Game game) {
        gameRepository.save(game);
    }
}
