package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.Color;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.entity.*;
import ch.uzh.ifi.seal.soprafs19.service.GameService;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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

    public SimpleRuleSetTest() {
        this.ruleSet = new SimpleRuleSet();
    }

    @Autowired
    private GameService gameService;

    protected SimpleRuleSet ruleSet;

    @Autowired
    private PlayerService playerService;

    protected Game game;


    @Before
    public void before() {
        setup();
        statusMove(game);
    }


    @Test
    public void moveWorkerSuccessfully() {

        int[] indexes = {3, 8, 9};
        for (int i : indexes) {
            setup();
            statusMove(game);

            // create game with chosen position
            Game updatedGame = SerializationUtils.clone(game);
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
            boolean isSuccessful = ruleSet.checkMovePhase(game, updatedGame);

            // Asserts
            Assert.assertTrue(isSuccessful);
        }
    }

    @Test
    public void moveWorker2Fields() {

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
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
        boolean isSuccessful = ruleSet.checkMovePhase(game, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
    }

    @Test
    public void moveWorkerInvalidField() {

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
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
        boolean isSuccessful = ruleSet.checkMovePhase(game, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
    }

    @Test
    public void dontMoveWorkerFails() {
        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(4));
        fields.add(board.getFields().get(4));

        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = ruleSet.checkMovePhase(game, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
    }

    @Test
    public void buildBlockSuccessfully() {

        // set up and move worker
        move(game, game.getBoard().getFields().get(4), game.getBoard().getFields().get(9));
        //move(4, 3);
        Assert.assertTrue(game.getPlayers().get(0).getWorkers().get(0).getIsCurrentWorker());

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        // build a block
        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(13));
        fields.get(0).setBlocks(1);

        board.setFields(fields);

        Assert.assertEquals(1, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = ruleSet.checkBuildPhase(game, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
    }

    @Test
    public void buildBlockOnWorker() {
        // set up and move worker
        move(game, game.getBoard().getFields().get(4), game.getBoard().getFields().get(8));
        Assert.assertTrue(game.getPlayers().get(0).getWorkers().get(0).getIsCurrentWorker());

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        // build a block
        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(7));
        fields.get(0).setBlocks(1);

        board.setFields(fields);

        Assert.assertEquals(1, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = ruleSet.checkBuildPhase(game, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
    }

    @Test
    public void buildDomeOnWrongLevel() {
        // set up and move worker
        move(game, game.getBoard().getFields().get(4), game.getBoard().getFields().get(9));
        Assert.assertTrue(game.getPlayers().get(0).getWorkers().get(0).getIsCurrentWorker());

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        // build a block
        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(8));
        fields.get(0).setHasDome(true);

        board.setFields(fields);

        Assert.assertEquals(1, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = ruleSet.checkBuildPhase(game, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
    }

    @Test
    public void buildToFarRemoved() {
        // set up and move worker
        move(game, game.getBoard().getFields().get(4), game.getBoard().getFields().get(9));
        Assert.assertTrue(game.getPlayers().get(0).getWorkers().get(0).getIsCurrentWorker());

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        // build a block
        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(2));
        fields.get(0).setBlocks(1);

        board.setFields(fields);

        Assert.assertEquals(1, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = ruleSet.checkBuildPhase(game, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
    }

    @Test
    public void win() {

        // change game to final step
        game.getBoard().getFields().get(4).setBlocks(2);
        game.getBoard().getFields().get(9).setBlocks(3);
        gameService.saveGame(game);

        // move worker to level 3 tower
        move(game, game.getBoard().getFields().get(4),  game.getBoard().getFields().get(9));

        // update position of Workers
        Player isSuccessful = ruleSet.checkWinCondition(game);

        // Asserts
        Assert.assertNotNull(isSuccessful);
    }

    @Test
    public void notWin() {

        // change game to final step
        game.getBoard().getFields().get(4).setBlocks(2);
        gameService.saveGame(game);

        // move worker to level 3 tower
        move(game, game.getBoard().getFields().get(4),  game.getBoard().getFields().get(9));

        // update position of Workers
        Player isSuccessful = ruleSet.checkWinCondition(game);

        // Asserts
        Assert.assertNull(isSuccessful);
    }

    @Test
    public void oneWorkerIsStuck() {

        // make worker stuck
        game.getBoard().getFields().get(3).setBlocks(2);
        game.getBoard().getFields().get(8).setHasDome(true);
        game.getBoard().getFields().get(9).setBlocks(3);
        game.getBoard().getFields().get(9).setHasDome(true);
        gameService.saveGame(game);

        // check
        boolean isStuck = ruleSet.isWorkerStuck(game, game.getBoard().getFields().get(4).getWorker());
        Player hasLost = ruleSet.checkWinCondition(game);

        // Asserts
        Assert.assertNull(hasLost);
        Assert.assertTrue(isStuck);
    }

    @Test
    public void lose() {

        List<Field> fields = game.getBoard().getFields();
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

        gameService.saveGame(game);

        Assert.assertNotNull(fields.get(23).getWorker());

        // check
        Player hasLost = ruleSet.checkWinCondition(game);

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
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        // build a block
        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(field));
        fields.get(0).setBlocks(board.getFields().get(field).getBlocks() + 1);

        board.setFields(fields);

        // update blocks
        gameService.updateGame(game, updatedGame);
    }

    public void buildDome(int field) {
        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        // build a block
        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(field));
        fields.get(0).setHasDome(true);

        board.setFields(fields);

        // update blocks
        gameService.updateGame(game, updatedGame);
    }

    /**
     * creates a game, two players and advances game directly to move phase
     */
    public void setup() {
        // creating players and adding to queue for matchmaking
        Player player1 = newPlayer(false);
        Player player2 = newPlayer(false);

        // get the game
        game = player1.getGame();
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
