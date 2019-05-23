package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import ch.uzh.ifi.seal.soprafs19.entity.*;
import ch.uzh.ifi.seal.soprafs19.rules.godCards.PanRuleSet;
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
public class PanRuleSetTest extends SimpleRuleSetTest {

    public PanRuleSetTest() {
        this.ruleSet = new PanRuleSet();
    }

    @Autowired
    private GameService gameService;

    @Test
    public void winByJumpingDownTwoLevels() {
        game.getBoard().getFields().get(4).setBlocks(2);

        Player currentPlayer = game.getCurrentPlayer();

        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        Worker worker = board.getFields().get(4).getWorker();

        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(4));
        fields.add(board.getFields().get(9));
        fields.get(0).setWorker(null);
        fields.get(1).setWorker(worker);

        board.setFields(fields);

        Assert.assertTrue(gameService.updateGame(game, updatedGame));

        Assert.assertTrue(ruleSet.checkWinCondition(game) == currentPlayer);

    }

    @Override
    public void initGodGame(SimpleGodCard cardOne, SimpleGodCard cardTwo) {
        super.initGodGame(SimpleGodCard.PAN, SimpleGodCard.APOLLO);
    }
}
