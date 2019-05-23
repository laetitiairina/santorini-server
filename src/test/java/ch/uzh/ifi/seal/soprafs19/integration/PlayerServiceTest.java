package ch.uzh.ifi.seal.soprafs19.integration;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.HelperClass.HelperClass;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.service.GameService;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PlayerServiceTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    private HelperClass helperClass;

    private final String token = "123456";

    @Before
    public void before() {
        this.initHelper();
    }

    public void initHelper() {
        helperClass = new HelperClass(this.gameService, this.playerService);
    }

    @Test
    public void createPlayerAndSetToken() {
        Player newPlayer = new Player();
        newPlayer.setIsGodMode(false);
        Player player = playerService.createPlayer(newPlayer, token, false);
        Assert.assertTrue(player != null);

    }
}
