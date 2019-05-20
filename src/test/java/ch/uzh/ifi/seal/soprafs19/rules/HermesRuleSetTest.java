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

    @Test
    public void moveOneWorkerMoreThanOneFieldOnSameLevelSuccessfully(){

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        Worker worker1 = board.getFields().get(4).getWorker();
        Worker worker2 = board.getFields().get(18).getWorker();

        // move a worker by one field in any direction
        List<Field> fields = new ArrayList<>();

        // old field 1
        fields.add(board.getFields().get(4));
        fields.get(0).setWorker(null);

        // new field 1
        fields.add(board.getFields().get(16));
        fields.get(1).setWorker(worker1);

        // old field 2
        fields.add(board.getFields().get(18));
        fields.get(2).setWorker(worker2);

        //new field 2
        fields.add(board.getFields().get(18));
        fields.get(3).setWorker(worker2);

        board.setFields(fields);

        //set some blocks
        game.getBoard().getFields().get(0).setBlocks(1);
        game.getBoard().getFields().get(1).setBlocks(1);
        game.getBoard().getFields().get(2).setBlocks(2);
        game.getBoard().getFields().get(24).setBlocks(2);
        game.getBoard().getFields().get(13).setBlocks(1);


        // update position of Workers
        boolean isSuccessful = ruleSet.checkMovePhase(game, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
    }

    @Test
    public void checkBuildPhaseWithoutCurrentWorker() {
        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        Worker worker1 = board.getFields().get(4).getWorker();
        Worker worker2 = board.getFields().get(18).getWorker();

        // move a worker by one field in any direction
        List<Field> fields = new ArrayList<>();

        // old field 1
        fields.add(board.getFields().get(4));
        fields.get(0).setWorker(null);

        // new field 1
        fields.add(board.getFields().get(9));
        fields.get(1).setWorker(worker1);

        // old field 2
        fields.add(board.getFields().get(18));
        fields.get(2).setWorker(worker2);

        //new field 2
        fields.add(board.getFields().get(18));
        fields.get(3).setWorker(worker2);

        board.setFields(fields);

        // Asserts
        Assert.assertTrue(gameService.updateGame(game, updatedGame));

        updatedGame = SerializationUtils.clone(game);
        board = updatedGame.getBoard();

        board.getFields().get(17).setBlocks(1);

        fields.clear();
        fields.add(board.getFields().get(17));
        updatedGame.getBoard().setFields(fields);

        Assert.assertTrue(gameService.updateGame(game, updatedGame));

    }

    @Test
    public void moveOnlyOneWorkerOneLevelUp() {
        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        Worker worker1 = board.getFields().get(4).getWorker();
        Worker worker2 = board.getFields().get(18).getWorker();

        // move a worker by one field in any direction
        List<Field> fields = new ArrayList<>();

        // old field 1
        fields.add(board.getFields().get(4));
        fields.get(0).setWorker(null);

        // new field 1
        fields.add(board.getFields().get(9));
        fields.get(1).setWorker(worker1);

        // old field 2
        fields.add(board.getFields().get(18));
        fields.get(2).setWorker(worker2);

        //new field 2
        fields.add(board.getFields().get(18));
        fields.get(3).setWorker(worker2);

        board.setFields(fields);

        //set some blocks
        game.getBoard().getFields().get(9).setBlocks(1);



        // update position of Workers
        boolean isSuccessful = ruleSet.checkMovePhase(game, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);

        Assert.assertTrue(gameService.updateGame(game, updatedGame));
    }


    @Override
    public void moveWorkerInvalidField(){
        // do nothing
    }

    @Override
    public void moveWorker2Fields() {
        // do nothing
    }

    @Override
    public void dontMoveWorkerFails() {
        // do nothing
    }

    @Override
    public void moveWorkerSuccessfully() {
        // do nothing
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
