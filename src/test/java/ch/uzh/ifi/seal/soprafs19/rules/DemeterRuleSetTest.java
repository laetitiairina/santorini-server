package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import ch.uzh.ifi.seal.soprafs19.entity.Board;
import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.rules.godCards.DemeterRuleSet;
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

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class DemeterRuleSetTest extends SimpleRuleSetTest {

    public DemeterRuleSetTest() {
        this.ruleSet = new DemeterRuleSet();
    }

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    @Override
    public void setup() {
        // creating players and adding to queue for matchmaking
        Player player1 = newPlayer(true);
        Player player2 = newPlayer(true);

        // get the game
        game = player1.getGame();

        initGodGame(SimpleGodCard.DEMETER, SimpleGodCard.APOLLO);
    }

    @Test
    public void buildOnTwoFieldsSuccessfully() {

        // set up and move worker
        move(game, game.getBoard().getFields().get(4), game.getBoard().getFields().get(9));
        //move(4, 3);
        Assert.assertTrue(game.getPlayers().get(0).getWorkers().get(0).getIsCurrentWorker());

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        // build a block
        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(8));
        fields.add(board.getFields().get(14));
        fields.get(0).setBlocks(1);
        fields.get(1).setBlocks(1);

        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = ruleSet.checkBuildPhase(game, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
    }

    @Test
    public void buildOneFieldsSuccessfully() {

        // set up and move worker
        move(game, game.getBoard().getFields().get(4), game.getBoard().getFields().get(9));
        //move(4, 3);
        Assert.assertTrue(game.getPlayers().get(0).getWorkers().get(0).getIsCurrentWorker());

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        // build a block
        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(8));
        fields.get(0).setBlocks(1);

        board.setFields(fields);

        Assert.assertEquals(1, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = ruleSet.checkBuildPhase(game, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
    }

    @Test
    public void buildOnTwoFieldsWithWrongWorkerFails() {

        // set up and move worker
        move(game, game.getBoard().getFields().get(4), game.getBoard().getFields().get(9));
        //move(4, 3);
        Assert.assertTrue(game.getPlayers().get(0).getWorkers().get(0).getIsCurrentWorker());

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        // build a block
        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(17));
        fields.add(board.getFields().get(22));
        fields.get(0).setBlocks(1);
        fields.get(1).setBlocks(1);

        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = ruleSet.checkBuildPhase(game, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
    }


}
