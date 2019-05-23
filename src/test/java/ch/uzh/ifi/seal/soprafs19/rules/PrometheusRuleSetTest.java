package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.entity.Board;
import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;
import ch.uzh.ifi.seal.soprafs19.rules.godCards.PrometheusRuleSet;
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
public class PrometheusRuleSetTest extends SimpleRuleSetTest {

    public PrometheusRuleSetTest() {
        this.ruleSet = new PrometheusRuleSet();
    }

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    @Test
    public void buildAndMoveSuccessFully() {

        List<Field> fields = new ArrayList<>();
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        fields.add(board.getFields().get(3));
        fields.get(0).setBlocks(1);

        Worker worker = board.getFields().get(4).getWorker();

        // old field
        fields.add(board.getFields().get(4));
        fields.get(1).setWorker(null);

        // new field
        fields.add(board.getFields().get(9));
        fields.get(2).setWorker(worker);

        board.setFields(fields);

        Assert.assertTrue(ruleSet.checkMovePhase(game, updatedGame));

    }

    @Test
    public void buildAndMoveUpOnSameFieldFails() {
        List<Field> fields = new ArrayList<>();
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        fields.add(board.getFields().get(3));
        fields.get(0).setBlocks(1);

        Worker worker = board.getFields().get(4).getWorker();

        // old field
        fields.add(board.getFields().get(4));
        fields.get(1).setWorker(null);

        // new field
        fields.add(board.getFields().get(3));
        fields.get(2).setWorker(worker);

        board.setFields(fields);

        Assert.assertFalse(ruleSet.checkMovePhase(game, updatedGame));
    }

    @Test
    public void buildAndMoveUpOnOtherFieldFails() {
        List<Field> fields = new ArrayList<>();
        // adjust setup
        game.getBoard().getFields().get(9).setBlocks(1);

        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        fields.add(board.getFields().get(3));
        fields.get(0).setBlocks(1);

        Worker worker = board.getFields().get(4).getWorker();

        // old field
        fields.add(board.getFields().get(4));
        fields.get(1).setWorker(null);

        // new field
        fields.add(board.getFields().get(9));
        fields.get(2).setWorker(worker);

        board.setFields(fields);

        Assert.assertFalse(ruleSet.checkMovePhase(game, updatedGame));
    }
}
