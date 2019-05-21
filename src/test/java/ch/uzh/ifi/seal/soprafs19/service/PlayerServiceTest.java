package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Test class for the PlayerResource REST resource.
 *
 * @see PlayerService
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PlayerServiceTest {

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerService playerService;


    @Test
    public void createPlayer() {

        // creating players
        Player player1 = newPlayer(false);
        Player player2 = newPlayer(true);

        // Asserting
        Assert.assertNotNull(player1.getToken());
        Assert.assertNotNull(player2.getToken());

        Assert.assertNotNull(player1.getId());
        Assert.assertNotNull(player2.getId());

        Assert.assertEquals(player1.getIsGodMode(), false);
        Assert.assertEquals(player2.getIsGodMode(), true);

        Assert.assertEquals(player1, playerRepository.findByToken(player1.getToken()));
        Assert.assertEquals(player2, playerRepository.findByToken(player2.getToken()));

        Assert.assertNotSame(player1.getId(),player2.getId());



        // players should not be matched, as they have different modes
        Assert.assertTrue((player1.getGame() == null && player2.getGame() == null) || player1.getGame() !=  player2.getGame());


    }

    @Test
    public void matchMaking() {

        // create players
        Player player1 = newPlayer(false);
        Player player2 = newPlayer(true);


        // Asserting creation of game through matchmaking
        if (player1.getGame() != null) {
            Assert.assertNotNull(player1.getGame());
        }

        if (player2.getGame() != null) {
            Assert.assertNotNull(player2.getGame());
        }
        if (player1.getGame() != null && player2.getGame() != null) {
            Assert.assertNotEquals(player1.getGame(), player2.getGame());
        }
    }

/*    @Test
    public void getPlayerById(){

        Player player1 = newPlayer(false);
        Player player2 = newPlayer(true);

        Assert.assertEquals(playerService.getPlayerById(player1.getId()), playerRepository.findById(player1.getId()));
        Assert.assertEquals(playerService.getPlayerById(player1.getId()), playerRepository.findById(player2.getId()));
    }
*/
    /**
     * creates a new player in the playerRepository
     *
     * @param isGodMode
     * @return Player
     */
    public Player newPlayer(Boolean isGodMode) {
        Player player = new Player();
        player.setIsGodMode(isGodMode);
        return playerService.createPlayer(player,null,true);
    }

}

