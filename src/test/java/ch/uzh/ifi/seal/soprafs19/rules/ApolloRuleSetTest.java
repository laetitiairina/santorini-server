package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.Color;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import ch.uzh.ifi.seal.soprafs19.entity.*;
import ch.uzh.ifi.seal.soprafs19.rules.godCards.ApolloRuleSet;
import ch.uzh.ifi.seal.soprafs19.service.GameService;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
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
 * @see ApolloRuleSet
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApolloRuleSetTest extends SimpleRuleSetTest {

    public ApolloRuleSetTest() {
        this.ruleSet = new ApolloRuleSet();
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

        Assert.assertEquals(SimpleGodCard.APOLLO, player1.getCard());
        Assert.assertTrue(player1.getIsCurrentPlayer());
        Assert.assertEquals(Color.BLUE, player1.getColor());
        Assert.assertEquals(GameStatus.MOVE, game.getStatus());
    }

    @Test
    public void switchPlaceWithOpponentWorker() {

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(game);
        Board board = updatedGame.getBoard();

        Worker ownWorker = board.getFields().get(18).getWorker();
        Worker opponentWorker = board.getFields().get(23).getWorker();

        // move a worker by one field in any direction
        List<Field> fields = new ArrayList<>();

        // new field
        fields.add(board.getFields().get(23));
        fields.get(0).setWorker(ownWorker);

        // old field
        fields.add(board.getFields().get(18));
        fields.get(1).setWorker(null);

        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        Assert.assertTrue(ruleSet.checkMovePhase(game, updatedGame));

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

        SimpleGodCard card1 = SimpleGodCard.APOLLO;
        cards.add(card1);

        SimpleGodCard card2 = SimpleGodCard.ARTEMIS;
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
