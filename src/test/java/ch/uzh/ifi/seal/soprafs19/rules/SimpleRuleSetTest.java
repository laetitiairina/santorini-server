package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.Color;
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
 * Test class for the GameResource REST resource.
 *
 * @see SimpleRuleSet
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SimpleRuleSetTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private SimpleRuleSet simpleRuleSet;

    @Autowired
    private PlayerService playerService;

    private Game simpleGame;

    @Test
    public void moveWorkerSuccessfully() {

        int[] indexes = {3, 8, 9};
        for (int i : indexes) {
            setup();

            // create game with chosen position
            Game updatedGame = SerializationUtils.clone(simpleGame);
            Board board = updatedGame.getBoard();

            Worker worker = board.getFields().get(4).getWorker();

            // move a worker by one field in any direction
            List<Field> fields = new ArrayList<>();

            // new field
            fields.add(board.getFields().get(i));
            fields.get(0).setWorker(worker);

            // old field
            fields.add(board.getFields().get(4));
            fields.get(1).setWorker(null);

            board.setFields(fields);

            Assert.assertEquals(2, board.getFields().size());

            // update position of Workers
            boolean isSuccessful = simpleRuleSet.checkMovePhase(simpleGame, updatedGame);

            // Asserts
            Assert.assertTrue(isSuccessful);
        }
    }

    @Test
    public void moveWorker2Fields() {

        setup();

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        Worker worker = board.getFields().get(4).getWorker();

        // move a worker by one field in any direction
        List<Field> fields = new ArrayList<>();

        // new field
        fields.add(board.getFields().get(14));
        fields.get(0).setWorker(worker);

        // old field
        fields.add(board.getFields().get(4));
        fields.get(1).setWorker(null);

        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = simpleRuleSet.checkMovePhase(simpleGame, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
    }

    @Test
    public void moveWorkerInvalidField() {

        setup();

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        Worker worker = board.getFields().get(4).getWorker();

        // move a worker by one field in any direction
        List<Field> fields = new ArrayList<>();

        // new field
        fields.add(board.getFields().get(14));
        fields.get(0).setWorker(worker);
        fields.get(0).setPosX(-2);

        // old field
        fields.add(board.getFields().get(4));
        fields.get(1).setWorker(null);

        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = simpleRuleSet.checkMovePhase(simpleGame, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
    }

    @Test
    public void buildBlockSuccessfully() {

        // set up and move worker
        setup();
        move(4, 3);
        Assert.assertTrue(simpleGame.getPlayers().get(0).getWorkers().get(0).getIsCurrentWorker());

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // build a block
        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(8));
        fields.get(0).setBlocks(1);

        board.setFields(fields);

        Assert.assertEquals(1, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = simpleRuleSet.checkBuildPhase(simpleGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
    }

    @Test
    public void buildBlockOnWorker() {

        // set up and move worker
        setup();
        move(4, 3);
        Assert.assertTrue(simpleGame.getPlayers().get(0).getWorkers().get(0).getIsCurrentWorker());

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // build a block
        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(7));
        fields.get(0).setBlocks(1);

        board.setFields(fields);

        Assert.assertEquals(1, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = simpleRuleSet.checkBuildPhase(simpleGame, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
    }

    /**
     * method to move a worker
     * @param from
     * @param to
     */
    public void move(int from, int to) {
        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        Worker worker = board.getFields().get(from).getWorker();

        // move a worker
        List<Field> fields = new ArrayList<>();

        // new field
        fields.add(board.getFields().get(to));
        fields.get(0).setWorker(worker);

        // old field
        fields.add(board.getFields().get(from));
        fields.get(1).setWorker(null);

        board.setFields(fields);

        // update position
        gameService.updateGame(simpleGame, updatedGame);
    }

    public void buildBlock(int field) {
        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // build a block
        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(field));
        fields.get(0).setBlocks(board.getFields().get(field).getBlocks() + 1);

        board.setFields(fields);

        // update blocks
        gameService.updateGame(simpleGame, updatedGame);
    }

    public void buildDome(int field) {
        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // build a block
        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(field));
        fields.get(0).setHasDome(true);

        board.setFields(fields);

        // update blocks
        gameService.updateGame(simpleGame, updatedGame);
    }

    /**
     * creates a game, two players and advances game directly to move phase
     */
    public void setup() {
        // creating players and adding to queue for matchmaking
        Player player1 = newPlayer(false);
        Player player2 = newPlayer(false);

        // get the game
        simpleGame = player1.getGame();

        color1();
        position1();
        color2();
        position2();
    }

    public void color1() {
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
    }

    public void position1() {
        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
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

        // update position of Workers
        gameService.updateGame(simpleGame, updatedGame);
    }

    public void color2() {
        // create game with chosen color
        Game updatedGame = SerializationUtils.clone(simpleGame);
        List<Player> players = updatedGame.getPlayers();
        Player playerToBeRemoved = null;

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
    }

    public void position2() {
        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
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
        board.getFields().get(7).setWorker(worker1);
        fields.add(board.getFields().get(7));
        board.getFields().get(23).setWorker(worker2);
        fields.add(board.getFields().get(23));
        board.setFields(fields);

        // update position of Workers
        gameService.updateGame(simpleGame, updatedGame);
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
        return playerService.createPlayer(player,true);
    }

}
