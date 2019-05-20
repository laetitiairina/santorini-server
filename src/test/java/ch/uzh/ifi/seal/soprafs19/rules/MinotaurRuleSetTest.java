package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.Color;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import ch.uzh.ifi.seal.soprafs19.entity.*;
import ch.uzh.ifi.seal.soprafs19.rules.godCards.MinotaurRuleSet;
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
public class MinotaurRuleSetTest extends SimpleRuleSetTest {

    public MinotaurRuleSetTest() {
        this.ruleSet = new MinotaurRuleSet();
    }

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    @Override
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

        // Asserts not lost because worker can still switch
        Assert.assertNull(hasLost);
    }

    @Test
    public void correctlyInitialized() {
        Player player1 = game.getPlayers().get(0);

        Assert.assertEquals(SimpleGodCard.MINOTAUR, player1.getCard());
        Assert.assertTrue(player1.getIsCurrentPlayer());
        Assert.assertEquals(Color.BLUE, player1.getColor());
        Assert.assertEquals(GameStatus.MOVE, game.getStatus());
    }

    @Test
    public void pushOpponentWorker() {

        // adjust setup
        Field field = game.getBoard().getFields().get(4);
        Worker worker = field.getWorker();
        game.getBoard().getFields().get(8).setWorker(worker);
        worker.setField(game.getBoard().getFields().get(8));
        field.setWorker(null);

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        Worker ownWorker = board.getFields().get(8).getWorker();
        Worker opponentWorker = board.getFields().get(7).getWorker();

        // move a worker by one field in any direction
        List<Field> fields = new ArrayList<>();

        // new field
        fields.add(board.getFields().get(7));
        fields.get(0).setWorker(ownWorker);

        // old field
        fields.add(board.getFields().get(8));
        fields.get(1).setWorker(null);

        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        Assert.assertTrue(gameService.updateGame(game, updatedGame));

        Assert.assertTrue(game.getBoard().getFields().get(6).getWorker().getId().equals(opponentWorker.getId()));

        Assert.assertTrue(game.getBoard().getFields().get(8).getWorker() == null);

        Assert.assertTrue(game.getBoard().getFields().get(7).getWorker().getId().equals(ownWorker.getId()));

    }

    @Test
    public void pushOpponentWorkerOverEdgeFails() {

        // adjust setup
        Field field = game.getBoard().getFields().get(4);
        Field field2 = game.getBoard().getFields().get(7);
        Worker worker = field.getWorker();
        Worker worker2 = field.getWorker();
        game.getBoard().getFields().get(6).setWorker(worker);
        worker.setField(game.getBoard().getFields().get(6));
        game.getBoard().getFields().get(5).setWorker(worker2);
        worker2.setField(game.getBoard().getFields().get(5));
        field.setWorker(null);
        field2.setWorker(null);

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        Worker ownWorker = board.getFields().get(8).getWorker();
        Worker opponentWorker = board.getFields().get(7).getWorker();

        // move a worker by one field in any direction
        List<Field> fields = new ArrayList<>();

        // new field
        fields.add(board.getFields().get(5));
        fields.get(0).setWorker(ownWorker);

        // old field
        fields.add(board.getFields().get(6));
        fields.get(1).setWorker(null);

        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        Assert.assertFalse(ruleSet.checkMovePhase(game, updatedGame));

        Assert.assertFalse(gameService.updateGame(game, updatedGame));

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

        SimpleGodCard card1 = SimpleGodCard.MINOTAUR;
        cards.add(card1);

        SimpleGodCard card2 = SimpleGodCard.APOLLO;
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
