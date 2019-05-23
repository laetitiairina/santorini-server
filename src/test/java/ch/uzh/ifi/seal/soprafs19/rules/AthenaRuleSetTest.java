package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import ch.uzh.ifi.seal.soprafs19.entity.*;
import ch.uzh.ifi.seal.soprafs19.rules.godCards.AthenaRuleSet;
import ch.uzh.ifi.seal.soprafs19.service.GameService;
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
public class AthenaRuleSetTest extends SimpleRuleSetTest {

    public AthenaRuleSetTest() { this.ruleSet = new AthenaRuleSet(); }

    @Autowired
    private GameService gameService;

    @Override
    public void setup() {
        // creating players and adding to queue for matchmaking
        Player player1 = newPlayer(true);
        Player player2 = newPlayer(true);

        // get the game
        game = player1.getGame();

        initGodGame(SimpleGodCard.DEMETER, SimpleGodCard.ATHENA);
    }

    @Test
    public void testEffect() {

        // set up and move worker
        move(game, game.getBoard().getFields().get(4), game.getBoard().getFields().get(9));
        //move(4, 3);
        Assert.assertTrue(game.getPlayers().get(0).getWorkers().get(0).getIsCurrentWorker());
        Assert.assertTrue(game.getCurrentPlayer().getCard().ordinal() == SimpleGodCard.DEMETER.ordinal());

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();
        //Board board = game.getBoard();

        // build a block
        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(8));
        fields.add(board.getFields().get(14));
        fields.get(0).setBlocks(1);
        fields.get(1).setBlocks(1);

        board.setFields(fields);

        gameService.updateGame(game, updatedGame);

        //game updates.. no need to retrieve from repository
        //game = gameService.getGameById(game.getId()).get();

        Assert.assertTrue(game.getCurrentPlayer().getCard() == SimpleGodCard.ATHENA);

        updatedGame = SerializationUtils.clone(game);
        board = updatedGame.getBoard();

        //move(game, game.getBoard().getFields().get(7), game.getBoard().getFields().get(8));


        Worker worker = board.getFields().get(7).getWorker();
        worker.setIsCurrentWorker(true);
        board.getFields().get(7).setWorker(null);
        board.getFields().get(8).setWorker(worker);

        fields.clear();
        fields.add(board.getFields().get(7));
        fields.add(board.getFields().get(8));

        board.setFields(fields);

        gameService.updateGame(game, updatedGame);

        updatedGame = SerializationUtils.clone(game);
        board = updatedGame.getBoard();

        fields.clear();

        fields.add(board.getFields().get(13));
        fields.get(0).setBlocks(1);

        board.setFields(fields);

        gameService.updateGame(game, updatedGame);

        Assert.assertTrue(game.getCurrentPlayer().getCard().ordinal() == SimpleGodCard.DEMETER.ordinal());

        updatedGame = SerializationUtils.clone(game);
        board = updatedGame.getBoard();

        worker = board.getFields().get(9).getWorker();
        worker.setIsCurrentWorker(true);
        board.getFields().get(9).setWorker(null);
        board.getFields().get(14).setWorker(worker);

        fields.clear();

        fields.add(board.getFields().get(9));
        fields.add(board.getFields().get(14));

        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = ruleSet.checkMovePhaseOpponent(game, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
    }

    @Test
    public void checkWinConditionWithMoveUp() {
        // adjust setup
        Field field = game.getBoard().getFields().get(18);
        Field field2 = game.getBoard().getFields().get(7);
        Worker worker = field.getWorker();
        Worker worker2 = field2.getWorker();
        game.getBoard().getFields().get(13).setWorker(worker);
        //worker.setField(game.getBoard().getFields().get(6));
        game.getBoard().getFields().get(22).setWorker(worker2);
        //worker2.setField(game.getBoard().getFields().get(5));
        field.setWorker(null);
        field2.setWorker(null);

        //setup blocks
        game.getBoard().getFields().get(24).setBlocks(2);
        game.getBoard().getFields().get(19).setBlocks(2);
        game.getBoard().getFields().get(18).setBlocks(1);
        game.getBoard().getFields().get(17).setBlocks(2);
        game.getBoard().getFields().get(16).setBlocks(2);
        game.getBoard().getFields().get(21).setBlocks(1);

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        Worker ownWorker = board.getFields().get(13).getWorker();

        // move a worker by one field in any direction
        List<Field> fields = new ArrayList<>();

        // new field
        fields.add(board.getFields().get(18));
        fields.get(0).setWorker(ownWorker);

        // old field
        fields.add(board.getFields().get(13));
        fields.get(1).setWorker(null);

        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        Assert.assertTrue(ruleSet.checkMovePhase(game, updatedGame));

        Assert.assertTrue(gameService.updateGame(game, updatedGame));

        Assert.assertTrue(ruleSet.checkWinCondition(game).equals(field.getWorker().getPlayer()));

    }
}
