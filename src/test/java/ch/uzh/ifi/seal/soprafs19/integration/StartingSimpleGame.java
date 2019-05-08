package ch.uzh.ifi.seal.soprafs19.integration;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.Color;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import ch.uzh.ifi.seal.soprafs19.entity.*;
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

    private Game simpleGame;

    /**
     * create two players and init game
     */
    public void setup() {
        // creating players and adding to queue for matchmaking
        // simple
        Player player1 = newPlayer(false);
        Player player2 = newPlayer(false);

        // get the games
        simpleGame = player1.getGame();
    }

    @Test
    public void startSimpleGameSuccessfully() {

        setup();
        Assert.assertEquals(GameStatus.COLOR1, simpleGame.getStatus());

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
        Assert.assertTrue(players.remove(playerToBeRemoved));
        updatedGame.setPlayers(players);
        Assert.assertEquals(1, updatedGame.getPlayers().size());

        // update color of current player
        boolean isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);

        Assert.assertEquals(GameStatus.POSITION1, simpleGame.getStatus());

        for (Player player : simpleGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                Assert.assertEquals(Color.BLUE, player.getColor());
            } else {
                Assert.assertNull(player.getColor());
            }
        }

        // create game with chosen position
        updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // create Workers
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

        Assert.assertEquals(2, board.getFields().size());

        // update position of Workers
        isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
        Assert.assertEquals(GameStatus.COLOR2, simpleGame.getStatus());

        List<Field> assertFields = simpleGame.getBoard().getFields();
        Assert.assertEquals(worker1, assertFields.get(4).getWorker());
        Assert.assertEquals(worker2, assertFields.get(18).getWorker());
        int count = 0;
        for (Field field : assertFields) {
            Worker worker = field.getWorker();
            if (worker == null) {
                count++;
            }
        }
        Assert.assertEquals(23, count);

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
        Assert.assertTrue(players.remove(playerToBeRemoved));
        updatedGame.setPlayers(players);
        Assert.assertEquals(1, updatedGame.getPlayers().size());

        // update color of current player
        isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);

        Assert.assertEquals(GameStatus.POSITION2, simpleGame.getStatus());

        for (Player player : simpleGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                Assert.assertEquals(Color.WHITE, player.getColor());
            } else {
                Assert.assertEquals(Color.BLUE, player.getColor());
            }
        }

        // create game with chosen position
        updatedGame = SerializationUtils.clone(simpleGame);
        board = updatedGame.getBoard();

        // create Workers
        worker1 = null;
        worker2 = null;

        for (Player player : updatedGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                worker1 = player.getWorkers().get(0);
                worker2 = player.getWorkers().get(1);
            }
        }

        // place Workers on two random fields
        fields = new ArrayList<>();
        board.getFields().get(7).setWorker(worker1);
        fields.add(board.getFields().get(7));
        board.getFields().get(23).setWorker(worker2);
        fields.add(board.getFields().get(23));
        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        // update position of Workers
        isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
        Assert.assertEquals(GameStatus.MOVE, simpleGame.getStatus());

        assertFields = simpleGame.getBoard().getFields();
        Assert.assertEquals(worker1, assertFields.get(7).getWorker());
        Assert.assertEquals(worker2, assertFields.get(23).getWorker());
        int count1 = 0;
        int count2 = 0;
        for (Field field : assertFields) {
            Worker worker = field.getWorker();
            if (worker == null) {
                count1++;
            } else if (!worker.getId().equals(worker1.getId()) && !worker.getId().equals(worker2.getId())) {
                count2++;
            }
        }

        Assert.assertEquals(21, count1);
        Assert.assertEquals(2, count2);

        // create game with chosen position
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

        Assert.assertEquals(2, board.getFields().size());

        // update position of Workers
        isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
        Assert.assertEquals(GameStatus.BUILD, simpleGame.getStatus());

        assertFields = simpleGame.getBoard().getFields();
        Assert.assertEquals(worker, assertFields.get(3).getWorker());
        Assert.assertNull(assertFields.get(4).getWorker());

        count = 0;

        for (Field field : assertFields) {
            Worker updatedWorker = field.getWorker();
            if (updatedWorker == null || !updatedWorker.getId().equals(worker.getId())) {
                count++;
            }
        }

        Assert.assertEquals(24, count);

        // create game with chosen position
        updatedGame = SerializationUtils.clone(simpleGame);
        board = updatedGame.getBoard();

        // build a block
        fields = new ArrayList<>();

        fields.add(board.getFields().get(8));
        fields.get(0).setBlocks(1);

        board.setFields(fields);

        Assert.assertEquals(1, board.getFields().size());

        // update blocks
        isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
        Assert.assertEquals(GameStatus.MOVE, simpleGame.getStatus());

        assertFields = simpleGame.getBoard().getFields();
        long blocks = assertFields.get(8).getBlocks();
        Assert.assertEquals((long) 1, blocks);

        count = 0;

        for (Field field : assertFields) {
            if (field.getBlocks() == 0) {
                count++;
            }
        }

        Assert.assertEquals(24, count);

    }

    /**
     * creates a new player in the playerRepository
     *
     * @param isGodMode
     * @return Player
     */
    public Player newPlayer(Boolean isGodMode) {
        Player player = new Player();
        player.setIsGodMode(isGodMode);
        return playerService.createPlayer(player, true);
    }
}