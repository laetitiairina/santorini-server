package ch.uzh.ifi.seal.soprafs19.integration;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.HelperClass.HelperClass;
import ch.uzh.ifi.seal.soprafs19.constant.Color;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.entity.*;
import ch.uzh.ifi.seal.soprafs19.service.GameService;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for testing the starting of a new game, meaning from creating it to building the first block, for simple mode
 *
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class StartingSimpleGame {

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    private HelperClass helperClass;

    @Before
    public void before() {
        this.initHelper();
    }

    public void initHelper() {
        helperClass = new HelperClass(this.gameService, this.playerService);
    }

    @Test
    public void startSimpleGameSuccessfully() {

        Game simpleGame = helperClass.setup(false);

        // create game with chosen color
        Game updatedGame = SerializationUtils.clone(simpleGame);
        List<Player> players = updatedGame.getPlayers();
        Player playerToBeRemoved = null;

        // set current player's color to blue and remove other player
        for (Player player : players) {
            if (player.getIsCurrentPlayer()) {
                player.setColor(Color.BLUE);
            } else {
                playerToBeRemoved = player;
            }
        }
        players.remove(playerToBeRemoved);

        updatedGame.setPlayers(players);

        // update color of current player
        gameService.updateGame(simpleGame, updatedGame);

        // create game with chosen position
        updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // get Workers
        Worker worker1 = null;
        Worker worker2 = null;

        for (Player player : updatedGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                worker1 = player.getWorkers().get(0);
                worker2 = player.getWorkers().get(1);
            }
        }

        // place Workers on two random fields
        List<Field> fields = new ArrayList<>();
        board.getFields().get(4).setWorker(worker1);
        fields.add(board.getFields().get(4));
        board.getFields().get(18).setWorker(worker2);
        fields.add(board.getFields().get(18));
        board.setFields(fields);

        // update position of Workers
        gameService.updateGame(simpleGame, updatedGame);

        // create game with chosen color
        updatedGame = SerializationUtils.clone(simpleGame);
        players = updatedGame.getPlayers();
        playerToBeRemoved = null;

        // set current player's color to red and remove other player
        for (Player player : players) {
            if (player.getIsCurrentPlayer()) {
                player.setColor(Color.WHITE);
            } else {
                playerToBeRemoved = player;
            }
        }
        players.remove(playerToBeRemoved);

        updatedGame.setPlayers(players);

        // update color of current player
        gameService.updateGame(simpleGame, updatedGame);

        // create game with chosen position
        updatedGame = SerializationUtils.clone(simpleGame);
        board = updatedGame.getBoard();

        // get Workers
        Worker worker3 = null;
        Worker worker4 = null;

        for (Player player : updatedGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                worker3 = player.getWorkers().get(0);
                worker4 = player.getWorkers().get(1);
            }
        }

        // place Workers on two random fields
        fields = new ArrayList<>();
        board.getFields().get(7).setWorker(worker3);
        fields.add(board.getFields().get(7));
        board.getFields().get(23).setWorker(worker4);
        fields.add(board.getFields().get(23));
        board.setFields(fields);

        // update position of Workers
        gameService.updateGame(simpleGame, updatedGame);

        updatedGame = SerializationUtils.clone(simpleGame);
        board = updatedGame.getBoard();

        // move a worker by one field
        fields = new ArrayList<>();
        Worker worker = board.getFields().get(4).getWorker();

        fields.add(board.getFields().get(3));
        fields.get(0).setWorker(worker);

        fields.add(board.getFields().get(4));
        fields.get(1).setWorker(null);

        board.setFields(fields);

        // update position of Workers
        gameService.updateGame(simpleGame, updatedGame);

        updatedGame = SerializationUtils.clone(simpleGame);
        board = updatedGame.getBoard();

        // build a block
        fields = new ArrayList<>();

        fields.add(board.getFields().get(8));
        fields.get(0).setBlocks(1);

        board.setFields(fields);

        // update blocks
        boolean isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
        Assert.assertEquals(GameStatus.MOVE, simpleGame.getStatus());

        List<Field> assertFields = simpleGame.getBoard().getFields();
        long blocks = assertFields.get(8).getBlocks();
        Assert.assertEquals((long) 1, blocks);

        // only one field has a block
        int count = 0;
        for (Field field : assertFields) {
            if (field.getBlocks() == 0) {
                count++;
            }
        }
        Assert.assertEquals(24, count);

        // worker positions
        Assert.assertEquals(worker1, assertFields.get(3).getWorker());
        Assert.assertEquals(worker2, assertFields.get(18).getWorker());
        Assert.assertEquals(worker3, assertFields.get(7).getWorker());
        Assert.assertEquals(worker4, assertFields.get(23).getWorker());

        // empty fields
        count = 0;
        for (Field field : assertFields) {
            Worker w = field.getWorker();
            if (w == null) {
                count++;
            }
        }
        Assert.assertEquals(21, count);

        // colors
        for (Player player : simpleGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                Assert.assertEquals(Color.WHITE, player.getColor());
            } else {
                Assert.assertEquals(Color.BLUE, player.getColor());
            }
        }
    }
}