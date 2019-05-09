package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.Color;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import ch.uzh.ifi.seal.soprafs19.entity.*;
import ch.uzh.ifi.seal.soprafs19.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs19.service.GameService;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Fields;
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
            statusMove(simpleGame);

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
        statusMove(simpleGame);

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
        statusMove(simpleGame);

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
        statusMove(simpleGame);
        move(simpleGame, simpleGame.getBoard().getFields().get(4), simpleGame.getBoard().getFields().get(4));
        //move(4, 3);
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
        statusMove(simpleGame);
        move(simpleGame, simpleGame.getBoard().getFields().get(4), simpleGame.getBoard().getFields().get(4));
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

    @Test
    public void win() {
        // set up
        setup();
        statusMove(simpleGame);

        // change game to final step
        simpleGame.getBoard().getFields().get(4).setBlocks(2);
        simpleGame.getBoard().getFields().get(9).setBlocks(3);
        gameService.saveGame(simpleGame);

        // move worker to level 3 tower
        move(simpleGame, simpleGame.getBoard().getFields().get(4),  simpleGame.getBoard().getFields().get(9));

        // update position of Workers
        Player isSuccessful = simpleRuleSet.checkWinCondition(simpleGame);

        // Asserts
        Assert.assertNotNull(isSuccessful);
    }

    @Test
    public void notWin() {
        // set up
        setup();
        statusMove(simpleGame);

        // change game to final step
        simpleGame.getBoard().getFields().get(4).setBlocks(2);
        gameService.saveGame(simpleGame);

        // move worker to level 3 tower
        move(simpleGame, simpleGame.getBoard().getFields().get(4),  simpleGame.getBoard().getFields().get(9));

        // update position of Workers
        Player isSuccessful = simpleRuleSet.checkWinCondition(simpleGame);

        // Asserts
        Assert.assertNull(isSuccessful);
    }

    @Test
    public void oneWorkerIsStuck() {
        // set up
        setup();
        statusMove(simpleGame);

        // make worker stuck
        simpleGame.getBoard().getFields().get(3).setBlocks(2);
        simpleGame.getBoard().getFields().get(8).setHasDome(true);
        simpleGame.getBoard().getFields().get(9).setBlocks(3);
        simpleGame.getBoard().getFields().get(9).setHasDome(true);
        gameService.saveGame(simpleGame);

        // check
        boolean isStuck = simpleRuleSet.isWorkerStuck(simpleGame, simpleGame.getBoard().getFields().get(4).getWorker());
        Player hasLost = simpleRuleSet.checkWinCondition(simpleGame);

        // Asserts
        Assert.assertNull(hasLost);
        Assert.assertTrue(isStuck);
    }

    @Test
    public void lose() {
        // set up
        setup();
        statusMove(simpleGame);

        List<Field> fields = simpleGame.getBoard().getFields();
        // Worker  1 isStuck
        fields.get(3).setBlocks(2);
        fields.get(8).setHasDome(true);
        fields.get(9).setBlocks(3);
        fields.get(9).setHasDome(true);

        // Worker 2 isStuck
        fields.get(12).setBlocks(3);
        fields.get(13).setBlocks(3);
        fields.get(14).setBlocks(1);
        fields.get(14).setHasDome(true);
        fields.get(17).setHasDome(true);
        fields.get(19).setBlocks(3);
        fields.get(19).setHasDome(true);
        fields.get(22).setBlocks(3);
        fields.get(24).setBlocks(2);
        fields.get(24).setHasDome(true);

        gameService.saveGame(simpleGame);

        Assert.assertNotNull(fields.get(23).getWorker());

        // check
        Player hasLost = simpleRuleSet.checkWinCondition(simpleGame);

        // Asserts
        Assert.assertNotNull(hasLost);
    }

    /**
     * method to move a worker
     * @param from
     * @param to
     */
    // not based on previous step anymore
    public void move(Game game, Field from, Field to) {
        Worker worker = from.getWorker();
        worker.setIsCurrentWorker(true);
        from.setWorker(null);
        to.setWorker(worker);
        game.setStatus(GameStatus.BUILD);
        gameService.saveGame(game);
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
    }

    public void statusPosition1(Game game) {
        for (Player player : game.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                player.setColor(Color.BLUE);
                playerService.savePlayer(player);
            }
        }

        game.setStatus(GameStatus.POSITION1);
        gameService.saveGame(game);
    }

    public void statusColor2(Game game) {
        statusPosition1(game);

        for (Player player : game.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                game.getBoard().getFields().get(4).setWorker(player.getWorkers().get(0));
                player.getWorkers().get(0).setField(game.getBoard().getFields().get(4));

                game.getBoard().getFields().get(18).setWorker(player.getWorkers().get(1));
                player.getWorkers().get(1).setField(game.getBoard().getFields().get(18));
                playerService.savePlayer(player);
            }
        }
        game.setStatus(GameStatus.COLOR2);
        nextTurn(game);
    }

    public void statusPosition2(Game game) {

        statusColor2(game);

        for (Player player : game.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                player.setColor(Color.WHITE);
                playerService.savePlayer(player);
            }
        }

        game.setStatus(GameStatus.POSITION2);
        gameService.saveGame(game);
    }

    public void statusMove(Game game) {
        statusPosition2(game);

        for (Player player : game.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                game.getBoard().getFields().get(7).setWorker(player.getWorkers().get(0));
                player.getWorkers().get(0).setField(game.getBoard().getFields().get(7));

                game.getBoard().getFields().get(23).setWorker(player.getWorkers().get(1));
                player.getWorkers().get(1).setField(game.getBoard().getFields().get(23));
                playerService.savePlayer(player);
            }
        }
        game.setStatus(GameStatus.MOVE);
        nextTurn(game);
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
        gameService.saveGame(game);
    }

}
