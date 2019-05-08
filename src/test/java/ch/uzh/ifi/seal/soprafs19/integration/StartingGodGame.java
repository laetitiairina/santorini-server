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

    private Game godGame;

    /**
     * create two players and init game
     */
    public void setup() {
        // creating players and adding to queue for matchmaking
        // god
        Player player3 = newPlayer(true);
        Player player4 = newPlayer(true);

        // get the games
        godGame = player3.getGame();
    }

    @Test
    public void startGodGameGameSuccessfully() {

        setup();
        
        Assert.assertEquals(GameStatus.CARDS1, godGame.getStatus());

        // select the two cards
        Game updatedGame = SerializationUtils.clone(godGame);
        List<SimpleGodCard> cards = new ArrayList<>();

        SimpleGodCard card1 = SimpleGodCard.APOLLO;
        cards.add(card1);

        SimpleGodCard card2 = SimpleGodCard.ARTEMIS;
        cards.add(card2);

        updatedGame.setCards(cards);

        // update the two cards
        boolean isSuccessful = gameService.updateGame(godGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);

        Assert.assertEquals(GameStatus.CARDS2, godGame.getStatus());
        Assert.assertEquals(cards, godGame.getCards());


        // select the two cards
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
        isSuccessful = gameService.updateGame(godGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
        Assert.assertEquals(GameStatus.STARTPLAYER, godGame.getStatus());
        Assert.assertEquals(2, godGame.getCards().size());
        Assert.assertEquals(2, godGame.getPlayers().size());

        // select the player
        updatedGame = SerializationUtils.clone(godGame);

        players = new ArrayList<>();
        players.add(updatedGame.getPlayers().get(0));
        players.get(0).setIsCurrentPlayer(true);

        updatedGame.setPlayers(players);

        // update the two cards
        isSuccessful = gameService.updateGame(godGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
        Assert.assertEquals(GameStatus.COLOR1, godGame.getStatus());
        Assert.assertEquals(2, godGame.getPlayers().size());

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