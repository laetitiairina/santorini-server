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
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the REST interface.
 * <p>
 * /games/{id} PUT
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class UpdateGameTest {

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

    private Player testPlayer1;
    private Player testPlayer2;

    private Game testGame;

    @Before
    public void setup() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        Player player1 = new Player();
        player1.setIsGodMode(true);
        testPlayer1 = playerService.createPlayer(player1,false);

        Player player2 = new Player();
        player2.setIsGodMode(true);
        testPlayer2 = playerService.createPlayer(player2,false);

        List<Player> players = new ArrayList<>();
        players.add(testPlayer1);
        players.add(testPlayer2);

        testGame = new Game(players, 5);
        gameService.createGame(testGame);
        playerService.updatePlayer(testPlayer1);
        playerService.updatePlayer(testPlayer2);
    }

    @Test
    public void updateGameCorrect() throws Exception {

        Assert.assertNotNull(gameRepository.findById(testPlayer1.getGame_id()));

        Game game = gameRepository.findById(testPlayer1.getGame_id()).get();

        Assert.assertTrue(game.getStatus() == GameStatus.CARDS1);

        mvc.perform(put("/games/" + testPlayer1.getGame_id())
                .contentType("application/json;charset=UTF-8")
                .header("Token", testPlayer1.getToken())
                .content("{\"id\":" + game.getId() +", \"cards\":[\"ARTEMIS\",\"APOLLO\"]}"))
                .andDo(print())
                .andExpect(status().isNoContent());


        game = gameRepository.findById(testPlayer1.getGame_id()).get();

        Assert.assertTrue(game.getStatus() == GameStatus.CARDS2);
        Assert.assertTrue(game.getCards().size() == 2);
    }

    @Test
    public void updateGameNotFound() throws Exception {

        Assert.assertNotNull(gameRepository.findById(testPlayer1.getGame_id()));

        Game game = gameRepository.findById(testPlayer1.getGame_id()).get();

        Assert.assertTrue(game.getStatus() == GameStatus.CARDS1);

        // wrong Id
        mvc.perform(put("/games/123142")
                .contentType("application/json;charset=UTF-8")
                .header("Token", testPlayer1.getToken())
                .content("{\"id\": \"123142\" , \"cards\":[\"ARTEMIS\",\"APOLLO\"]}"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateGameBadRequest() throws Exception {

        Assert.assertNotNull(gameRepository.findById(testPlayer1.getGame_id()));

        Game game = gameRepository.findById(testPlayer1.getGame_id()).get();

        Assert.assertTrue(game.getStatus() == GameStatus.CARDS1);

        // wrong id in body
        mvc.perform(put("/games/" + testPlayer1.getGame_id())
                .contentType("application/json;charset=UTF-8")
                .header("Token", testPlayer1.getToken())
                .content("{\"id\": \"123142\" , \"cards\":[\"ARTEMIS\",\"APOLLO\"]}"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // invalid input
        mvc.perform(put("/games/" + testPlayer1.getGame_id())
                .contentType("application/json;charset=UTF-8")
                .header("Token", testPlayer1.getToken())
                .content("{\"id\":" + game.getId() +", \"cards\":[\"ARTEMIS\",\"ARTEMIS\"]}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateGameForbidden() throws Exception {

        Assert.assertNotNull(gameRepository.findById(testPlayer1.getGame_id()));

        Game game = gameRepository.findById(testPlayer1.getGame_id()).get();

        Assert.assertTrue(game.getStatus() == GameStatus.CARDS1);

        mvc.perform(put("/games/" + testPlayer1.getGame_id())
                .contentType("application/json;charset=UTF-8")
                .header("Token", "test")
                .content("{\"id\":" + game.getId() +", \"cards\":[\"ARTEMIS\",\"APOLLO\"]}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
