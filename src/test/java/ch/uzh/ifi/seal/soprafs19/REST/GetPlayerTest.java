package ch.uzh.ifi.seal.soprafs19.REST;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the REST interface.
 *
 * /players/{id} GET
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class GetPlayerTest {

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;

    private Player testPlayer;

    @Before
    public void setup() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        Player player = new Player();
        player.setIsGodMode(false);

        testPlayer = playerService.createPlayer(player,null,false);
    }

    @Test
    public void getPlayerCorrect() throws Exception {
        mvc.perform(get("/players/"+testPlayer.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(testPlayer.getId()))
                .andExpect(jsonPath("$.isGodMode").value(testPlayer.getIsGodMode()))
                .andExpect(jsonPath("$.card").isEmpty())
                .andExpect(jsonPath("$.color").isEmpty())
                ;
    }

    @Test
    public void getPlayerNotFound() throws Exception {
        mvc.perform(get("/players/1232421"))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    public void getPlayerField() throws Exception {
        mvc.perform(get("/players/"+testPlayer.getId()+"?fields=id"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(testPlayer.getId()))
                .andExpect(jsonPath("$.color").doesNotExist())
        ;

    }
}
