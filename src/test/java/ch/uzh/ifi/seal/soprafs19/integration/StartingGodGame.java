package ch.uzh.ifi.seal.soprafs19.integration;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.HelperClass.HelperClass;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
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
 * Test class for testing the starting of a new game, meaning from creating it to choosing the colors (where simple game starts)
 *
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class StartingGodGame {

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
    public void startGodGameGameSuccessfully() {

        Game godGame = helperClass.setup(true);

        // select the two cards
        Game updatedGame = SerializationUtils.clone(godGame);
        List<SimpleGodCard> cards = new ArrayList<>();

        SimpleGodCard card1 = SimpleGodCard.APOLLO;
        cards.add(card1);

        SimpleGodCard card2 = SimpleGodCard.ARTEMIS;
        cards.add(card2);

        updatedGame.setCards(cards);

        // update the two cards
        gameService.updateGame(godGame, updatedGame);

        // select a card
        updatedGame = SerializationUtils.clone(godGame);
        List<Player> players = new ArrayList<>();

        for (Player player : updatedGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                player.setCard(updatedGame.getCards().get(0));
                players.add(player);
            }
        }
        updatedGame.setPlayers(players);

        // update the two cards
        gameService.updateGame(godGame, updatedGame);

        // select the player
        updatedGame = SerializationUtils.clone(godGame);

        players = new ArrayList<>();
        players.add(updatedGame.getPlayers().get(0));
        players.get(0).setIsCurrentPlayer(true);
        long playerId = players.get(0).getId();

        updatedGame.setPlayers(players);

        // update the player
        boolean isSuccessful = gameService.updateGame(godGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
        Assert.assertEquals(GameStatus.COLOR1, godGame.getStatus());
        Assert.assertEquals(cards, godGame.getCards());
        Assert.assertEquals(2, godGame.getPlayers().size());
        Assert.assertEquals(SimpleGodCard.APOLLO, godGame.getPlayers().get(1).getCard());
        Assert.assertEquals(SimpleGodCard.ARTEMIS, godGame.getPlayers().get(0).getCard());
        for (Player player : godGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                Assert.assertTrue(playerId == player.getId());
            } else {
                Assert.assertFalse(playerId == player.getId());
            }
        }
    }
}