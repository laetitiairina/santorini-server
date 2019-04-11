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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the REST interface.
 *
 * /players POST
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class CreatePlayerTest {

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

    private User testUser;

    @Before
    public void setup() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        User user = new User();
        user.setUsername("testUsername");
        user.setPassword("testPassword");

        testUser = userService.createUser(testUser);
    }

    @Test
    public void createPlayerCorrect() throws Exception {

        Assert.assertNull(playerRepository.findByUserId(testUser.getId()));

        mvc.perform(post("/players")
                .contentType("application/json;charset=UTF-8")
                .content("{\"userId\":" + testUser.getId() + ",\"isGodMode\":false}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.url").exists());

        Assert.assertNotNull(playerRepository.findByUserId(testUser.getId()));

        Player player = playerRepository.findByUserId(testUser.getId());

        //Assert.assertNull(player.getGame_id());
        Assert.assertFalse(player.getIsGodMode());
        Assert.assertNotNull(player.getToken());
    }
}
