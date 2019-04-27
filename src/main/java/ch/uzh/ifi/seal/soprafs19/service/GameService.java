package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.Color;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import ch.uzh.ifi.seal.soprafs19.entity.Card;
import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.repository.CardRepository;
import ch.uzh.ifi.seal.soprafs19.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs19.rules.IRuleSet;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
//import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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
    private CardRepository cardRepository;

    @Autowired
    private EntityManager entityManager;

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

    public boolean updateGame(Game currentGame, Game updatedGame) {
        // Authentication and checks done in GameController

        // Todo: look at how to use correctly after Can is done with the implementation
        IRuleSet rules = new SimpleRuleSet();

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
                // TODO: include isBadRequest handling, add check logic
                // check if it's a valid move
                //if (rules.checkMovePhase(currentGame, updatedGame)) {
                    successfullyUpdatedGame = move(currentGame, updatedGame);
                //}
                break;
            case BUILD:
                // TODO: include isBadRequest handling, add check logic
                // check if it's a valid build
                //if (rules.checkBuildPhase(currentGame, updatedGame)) {
                    successfullyUpdatedGame = build(currentGame, updatedGame);
                //}
                break;
        }

        // update the status of the game for pinging
        if (successfullyUpdatedGame != null) {

            // increment the status
            // TODO: actually only needs to send successfullyUpdatedGame to check..
            if (rules.checkWinCondition(currentGame, updatedGame)) {
                incrementGameStatus(successfullyUpdatedGame, true);
            } else {
                incrementGameStatus(successfullyUpdatedGame, false);
            }

            // saves updates to database
            gameRepository.save(successfullyUpdatedGame);

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

    public Game setGodModeInit(Game currentGame, Game updatedGame) {
        switch (currentGame.getStatus()) {
            case CARDS1:
                return setCards1 (currentGame, updatedGame);
            case CARDS2:
                return setCards2(currentGame, updatedGame);
            case STARTPLAYER:
                return setStartPlayer(currentGame, updatedGame);
        }
        return null;
    }

    public Game setStartPlayer (Game currentGame, Game updatedGame) {
        Player currentPlayer = updatedGame.getPlayers().get(0);
        List<Player> players = currentGame.getPlayers();

        if (currentPlayer.isCurrentPlayer()) { // && currentPlayer.getGame().getId() == currentGame.getId()
            long id = currentPlayer.getId();
            currentPlayer = playerRepository.findById(id);

            for (Player player: players) {
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
     * sets the two cards in the player entities at status CARDS2
     *
     * @param currentGame
     * @param updatedGame
     * @return
     */
    public Game setCards2 (Game currentGame, Game updatedGame) {
        // front-end has to send exactly 1 player and an existing card
        Card card = null;
        if (updatedGame.getPlayers().get(0).getCard().getId() != null) {
            card = entityManager.find(Card.class, updatedGame.getPlayers().get(0).getCard().getId());
        }
        if (updatedGame.getPlayers().size() == 1 && card != null) {
            // get cards
            // TODO: does this work, or do we need to get the card from the currentGame first?
            Card chosenCard = updatedGame.getPlayers().get(0).getCard();
            List<Card> currentCards = currentGame.getCards();

            // get players
            List<Player> currentPlayers = currentGame.getPlayers();
            long id = updatedGame.getPlayers().get(0).getId();
            Player currentPlayer = playerRepository.findById(id);

            // check if the chosenCard is one of the 2 currentCards
            // and the currentPlayer is one of the two currentPlayers and the Challenger
            if (currentCards.contains(chosenCard) && currentPlayers.contains(currentPlayer) && currentPlayer.isCurrentPlayer()) {
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
     * sets the two cards in the game at status CARDS1
     *
     * @param currentGame
     * @param updatedGame
     * @return
     */
    public Game setCards1 (Game currentGame, Game updatedGame) {
        // check if the cards are valid
        List<Card> cards = new ArrayList<>();

        for (Card card : updatedGame.getCards()) {
            // check, if the given value is a valid card
            // TODO: really necessary?
            if (EnumUtils.isValidEnum(SimpleGodCard.class, card.getCardName().toString())) {
                cards.add(card);
            }
        }

        // front-end has to send exactly 2 cards
        if (cards.size() == 2 && updatedGame.getCards().size() == 2 && cards.get(0).getCardName() != cards.get(1).getCardName()) {

            for(Card card : cards) {
                cardRepository.save(card);
            }

            // set cards
            currentGame.setCards(cards);
            gameRepository.save(currentGame);

            // other player is now current player
            nextTurn(currentGame);
            return currentGame;
        }
        return null;
    }
    /**
     * updates the position of a worker
     *
     * @param currentGame
     * @param updatedGame
     * @return updated game
     */
    public Game move (Game currentGame, Game updatedGame) {
        for (Field field : updatedGame.getBoard().getFields()) {
            // find field that needs to be updated
            Field fieldToUpdate = getFieldById(currentGame, field.getId());

            // update the worker values of the field
            fieldToUpdate.setWorker(field.getWorker());
        }
        nextTurn(currentGame);
        return currentGame;
    }

    /**
     * updates the number of blocks or existence of a dome on a field
     *
     * @param currentGame
     * @param updatedGame
     * @return updated game
     */
    public Game build (Game currentGame, Game updatedGame) {
        for (Field field : updatedGame.getBoard().getFields()) {
            // find field that needs to be updated
            Field fieldToUpdate = getFieldById(currentGame, field.getId());

            // update the blocks and has Dome value of the field
            fieldToUpdate.setBlocks(field.getBlocks());
            fieldToUpdate.setHasDome(field.getHasDome());
        }
        nextTurn(currentGame);
        return currentGame;
    }

    /**
     * get Field by Id
     *
     * @param game
     * @param fieldId
     * @return
     */
    public Field getFieldById(Game game, long fieldId) {
        for (Field field : game.getBoard().getFields()) {
            if (field.getId() == fieldId) {
                return field;
            }
        }
        return null;
    }

    public Game setColor(Game currentGame, Game updatedGame) {
        Player updatedPlayer = updatedGame.getPlayers().get(0);

        if (updatedGame.getPlayers().size() == 1 && updatedPlayer.isCurrentPlayer()) {
            long id = updatedGame.getPlayers().get(0).getId();
            for (Player player : currentGame.getPlayers()) {
                if (player.getId() == id) {
                    if (updatedPlayer.getColor() != null && EnumUtils.isValidEnum(Color.class, updatedPlayer.getColor().toString())) {
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

    public Game setPosition(Game currentGame, Game updatedGame) {
        List<Field> fields = updatedGame.getBoard().getFields();
        int count = 0;

        for (Field updatedField : fields) {
            Field currentField = getFieldById(currentGame, updatedField.getId());
            if (currentField.getWorker() == null) {
                currentField.setWorker(updatedField.getWorker());
                // only works if it's the current Player
                Player player = updatedField.getWorker().getPlayer();
                if (player.isCurrentPlayer() && playerRepository.findByToken(player.getToken()).isCurrentPlayer()) {
                    ++count;
                }
            }
        }
        // both fields need to be valid
        if (count == 2) {
            nextTurn(currentGame);
            return currentGame;
        } else {
            return null;
        }
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
            --status;
        } else {
            ++status;
        }

        // convert type Integer it back to type GameStatus
        game.setStatus(game.getGameStatusEnum(status));

        // save the new status
        gameRepository.save(game);
    }

    public void nextTurn(Game game) {
        for (Player player : game.getPlayers()) {
            // reverse value
            player.setIsCurrentPlayer(!player.isCurrentPlayer());
        }
        // save
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
                if (player.isCurrentPlayer()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
}
