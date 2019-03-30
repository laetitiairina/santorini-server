package ch.uzh.ifi.seal.soprafs19.REST;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs19.service.GameService;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the REST interface.
 *
 * /games/{id} GET
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class GetGameTest {

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;

    private User testUser1;
    private User testUser2;

    private Player testPlayer1;
    private Player testPlayer2;

    private Game testGame;

    @Before
    public void setup() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        User user1 = new User();
        user1.setUsername("testUsername1");
        user1.setPassword("testPassword1");

        testUser1 = userService.createUser(user1);

        User user2 = new User();
        user2.setUsername("testUsername2");
        user2.setPassword("testPassword2");

        testUser2 = userService.createUser(user2);

        Player player1 = new Player();
        player1.setUserId(testUser1.getId());
        player1.setMode(0);

        testPlayer1 = playerService.createPlayer(player1);

        Player player2 = new Player();
        player2.setUserId(testUser2.getId());
        player2.setMode(0);

        testPlayer2 = playerService.createPlayer(player2);

        testGame = gameService.createGame();

    }

    @Test
    public void getGameCorrect() throws Exception {

        Assert.assertNotNull(gameRepository.findById(testGame.getId()));

        mvc.perform(get("/games/"+testGame.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(testGame.getId()))
                .andExpect(jsonPath("$.board").exists())
                .andExpect(jsonPath("$.player1").exists())
                .andExpect(jsonPath("$.player2").exists())
                .andExpect(jsonPath("$.card1").isEmpty())
                .andExpect(jsonPath("$.card2").isEmpty())
                .andExpect(jsonPath("$.isGodMode").value(testPlayer1.getMode()))
                .andExpect(jsonPath("$.status").value(GameStatus.CARDS10))
                .andExpect(jsonPath("$.currentPlayerId").exists())
                .andExpect(jsonPath("$.currentWorker").exists());
    }
}
