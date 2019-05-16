package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.Color;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import ch.uzh.ifi.seal.soprafs19.entity.*;
import ch.uzh.ifi.seal.soprafs19.rules.godCards.ApolloRuleSet;
import ch.uzh.ifi.seal.soprafs19.rules.godCards.HermesRuleSet;
import ch.uzh.ifi.seal.soprafs19.service.GameService;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for the GameResource REST resource.
 *
 * @see ApolloRuleSet
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class HermesRuleSetTest extends SimpleRuleSetTest {

    public HermesRuleSetTest() {
        this.ruleSet = new HermesRuleSet();
    }

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;


    @Override
    public void moveWorkerSuccessfully(){

            setup();
            statusMove(game);

            // create game with chosen position
            Game updatedGame = SerializationUtils.clone(game);
            Board board = updatedGame.getBoard();

            Worker worker = board.getFields().get(4).getWorker();

            //set all fields
            List<Field> fields = updatedGame.getBoard().getFields();

            //Move Worker from one Corner to the other
            fields.get(4).setWorker(null);
            fields.get(16).setWorker(worker);

            //set some blocks
            fields.get(0).setBlocks(1);
            fields.get(1).setBlocks(1);
            fields.get(2).setBlocks(2);
            fields.get(24).setBlocks(2);
            fields.get(23).setBlocks(1);


            // update position of Workers
            boolean isSuccessful = ruleSet.checkMovePhase(game, updatedGame);

            // Asserts
            Assert.assertTrue(isSuccessful);
        }


    @Override
    public void moveWorkerInvalidField(){

    }

    @Test
    public void correctlyInitialized() {
        Player player1 = game.getPlayers().get(0);

        Assert.assertEquals(SimpleGodCard.HERMES, player1.getCard());
        Assert.assertTrue(player1.getIsCurrentPlayer());
        Assert.assertEquals(Color.BLUE, player1.getColor());
        Assert.assertEquals(GameStatus.MOVE, game.getStatus());
    }


    @Override
    public void setup() {
        // creating players and adding to queue for matchmaking
        Player player1 = newPlayer(true);
        Player player2 = newPlayer(true);

        // get the game
        game = player1.getGame();

        initGodGame();
    }

    public void initGodGame() {

        // select the two cards
        List<SimpleGodCard> cards = new ArrayList<>();

        SimpleGodCard card1 = SimpleGodCard.HERMES;
        cards.add(card1);

        SimpleGodCard card2 = SimpleGodCard.ARTEMIS;
        cards.add(card2);

        game.setCards(cards);

        Player player1 = game.getPlayers().get(0);
        player1.setCard(card1);
        player1.setIsCurrentPlayer(true);
        playerService.savePlayer(player1);
        Player player2 = game.getPlayers().get(1);
        player2.setCard(card2);
        player2.setIsCurrentPlayer(false);
        playerService.savePlayer(player2);

        game.setStatus(GameStatus.COLOR1);
        gameService.saveGame(game);

    }
}
