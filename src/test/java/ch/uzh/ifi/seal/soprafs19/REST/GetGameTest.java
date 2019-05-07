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

import java.util.ArrayList;
import java.util.List;

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

        Player player1 = new Player();
        player1.setIsGodMode(false);

        testPlayer1 = playerService.createPlayer(player1);

        Player player2 = new Player();
        player2.setIsGodMode(false);

        testPlayer2 = playerService.createPlayer(player2);


        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        testGame = new Game(players, 25);
        gameService.createGame(testGame);
    }

    @Test
    public void getGameCorrect() throws Exception {

       Assert.assertNotNull(gameRepository.findById(testPlayer1.getGame_id()));

        mvc.perform(get("/games/"+testPlayer1.getGame_id()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(testPlayer1.getGame_id()))
                .andExpect(jsonPath("$.board").exists())
                .andExpect(jsonPath("$.players").exists())
                .andExpect(jsonPath("$.cards").isEmpty())
                .andExpect(jsonPath("$.isGodMode").value(testPlayer1.getIsGodMode()))
                .andExpect(jsonPath("$.status").value(GameStatus.COLOR1.toString()))
                .andExpect(jsonPath("$.hasMovedUp").value(false))
                ;
    }

    @Test
    public void getGamewrongId() throws Exception {

        Assert.assertNotNull(gameRepository.findById(testPlayer1.getGame_id()));

        mvc.perform(get("/games/122343243"))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    public void getGameField() throws Exception {

        Assert.assertNotNull(gameRepository.findById(testPlayer1.getGame_id()));

        mvc.perform(get("/games/"+testPlayer1.getGame_id()+"?fields=id"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(testPlayer1.getGame_id()))
                .andExpect(jsonPath("$.board").doesNotExist())
        ;
        ;
    }
}
