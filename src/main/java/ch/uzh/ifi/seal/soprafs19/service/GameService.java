package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import ch.uzh.ifi.seal.soprafs19.entity.Card;
import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs19.rules.IRuleSet;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Primary
@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;

    private final PlayerRepository playerRepository;

    @Autowired
    public GameService(GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    /*
    public Iterable<Game> getGames() {
        return this.gameRepository.findAll();
    }
    */

    /**
     * Create a new game
     *
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
        IRuleSet rules = new SimpleRuleSet();

        // react to update depending on status

        boolean successfullyUpdated = false; // set to true later, if update is valid

        // only checks the first 3 states, if isGodMode is true
        if (currentGame.getIsGodMode()) {
            successfullyUpdated = checkGodMode(currentGame, updatedGame);
        }

        // check the remaining states
        switch (currentGame.getStatus()) {
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
                        Field fieldToUpdate = getFieldById(currentGame, field.getId());

                        // update the worker values of the field
                        fieldToUpdate.setWorker(field.getWorker());
                    }
                    successfullyUpdated = true;
                }
                break;
            case BUILD:
                if (rules.checkBuildPhase(currentGame, updatedGame)) {
                    for (Field field : updatedGame.getBoard().getFields()) {
                        // find field that needs to be updated
                        Field fieldToUpdate = getFieldById(currentGame, field.getId());

                        // update the blocks and has Dome value of the field
                        fieldToUpdate.setBlocks(field.getBlocks());
                        fieldToUpdate.setHasDome(field.getHasDome());
                    }
                    successfullyUpdated = true;
                }
                break;
        }

        // update the status of the game for pinging
        if (successfullyUpdated) {
            // saves updates to database
            gameRepository.save(currentGame);
            if (rules.checkWinCondition(currentGame, updatedGame)) {
                incrementGameStatus(currentGame, true);
            } else {
                incrementGameStatus(currentGame, false);
            }
        }
    }

    public Boolean checkGodMode(Game currentGame, Game updatedGame) {
        switch (currentGame.getStatus()) {
            case CARDS1:
                // front-end has to send exactly 2 cards
                if (updatedGame.getCards().size() != 2) {
                    return false;
                } else {

                    // check if the cards are valid
                    List<Card> cards = new LinkedList<>();
                    for (Card card : updatedGame.getCards()) {
                        // check if the given value is a valid card
                        if (EnumUtils.isValidEnum(SimpleGodCard.class, card.getCardName().toString())) {
                            cards.add(card);
                        } else {
                            return false;
                        }
                    }

                    // set cards
                    currentGame.setCards(cards);

                    // other player is now current player
                    nextTurn(currentGame);

                    return true;
                }
            case CARDS2:
                // front-end has to send exactly 1 player
                if (updatedGame.getPlayers().size() != 1) {
                    return false;
                } else {

                    // get cards
                    Card chosenCard = updatedGame.getPlayers().get(0).getCard();
                    List<Card> cards = currentGame.getCards();

                    // get players
                    List<Player> players = currentGame.getPlayers();
                    Player player = playerRepository.findByUserId(updatedGame.getPlayers().get(0).getId());


                    // check if the chosenCard is one of the 2 cards and the player is one of the two players
                    // TODO: and the Challenger, really?
                    if (cards.remove(chosenCard) && players.remove(player) && player.isCurrentPlayer()) {
                        // now players only contains the opponent
                        // and cards only contains the other card
                        players.get(0).setCard(cards.get(0));
                        player.setCard(chosenCard);
                        return true;
                    } else {
                        return false;
                    }
                }
            case STARTPLAYER:
                Player player = updatedGame.getPlayers().get(0);
                List<Player> players = currentGame.getPlayers();

                if (player.isCurrentPlayer()) {
                    player = playerRepository.findByUserId(player.getId());
                    players.remove(player);

                    player.setCurrentPlayer(true);
                    players.get(0).setCurrentPlayer(false);
                    return true;
                } else {
                    return false;
                }
        }
        return false;
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
        if (status == 9 && !isEnd) {
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
            player.setCurrentPlayer(!player.isCurrentPlayer());
        }
        // save
        gameRepository.save(game);
    }
}
