package ch.uzh.ifi.seal.soprafs19.REST;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Test class for the REST interface.
 *
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class UserTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;

    //private User testUser;
    //private User testUser2;
    //private User testUser3;

    @Before
    public void setup() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        /*
        User user = new User();
        user.setPassword("pass");
        user.setUsername("testUser");

        testUser = userService.createUser(user);

        User user2 = new User();
        user2.setPassword("pass2");
        user2.setUsername("testUser2");

        testUser2 = userService.createUser(user2);

        User user3 = new User();
        user3.setPassword("pass3");
        user3.setUsername("testUser3");

        testUser3 = userService.createUser(user3);
        */
    }

    @Test
    public void registerUserCorrect() throws Exception {

        Assert.assertNull(userRepository.findByUsername("test1"));

        mvc.perform(post("/users")
                .contentType("application/json;charset=UTF-8")
                .content("{\"username\": \"test1\",\"password\": \"pass1\"}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        Assert.assertNotNull(userRepository.findByUsername("test1"));

        User user = userRepository.findByUsername("test1");

        Assert.assertNotNull(user.getToken());
        Assert.assertNotNull(user.getCreationDate());
        Assert.assertEquals(user.getPassword(),"pass1");
        Assert.assertEquals(user.getStatus(),UserStatus.OFFLINE);
    }

    @Test
    public void registerUserIncorrect() throws Exception {

        User user = new User();
        user.setPassword("pass");
        user.setUsername("testUser1");

        User testUser = userService.createUser(user);

        Assert.assertNotNull(userRepository.findByUsername("testUser1"));

        mvc.perform(post("/users")
                .contentType("application/json;charset=UTF-8")
                .content("{\"username\": \"testUser1\",\"password\": \"pass\"}"))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));

        Assert.assertNotNull(userRepository.findByUsername("testUser1"));
    }

    @Test
    public void loginUserCorrect() throws Exception {

        User user = new User();
        user.setPassword("pass");
        user.setUsername("testUser6");

        User testUser = userService.createUser(user);

        Assert.assertNotNull(userRepository.findByUsername("testUser6"));
        User userLoggedOut = userRepository.findByUsername("testUser6");
        Assert.assertEquals(userLoggedOut.getStatus(),UserStatus.OFFLINE);

        mvc.perform(post("/users/login")
                .contentType("application/json;charset=UTF-8")
                .content("{\"username\": \"testUser6\",\"password\": \"pass\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.token").value(testUser.getToken()));

        Assert.assertNotNull(userRepository.findByUsername("testUser6"));
        User userLoggedIn = userRepository.findByUsername("testUser6");
        Assert.assertEquals(userLoggedIn.getStatus(),UserStatus.ONLINE);
    }

    @Test
    public void loginUserIncorrect() throws Exception {

        User user = new User();
        user.setPassword("pass");
        user.setUsername("testUser7");

        User testUser = userService.createUser(user);

        Assert.assertNotNull(userRepository.findByUsername("testUser7"));
        User userLoggedOut = userRepository.findByUsername("testUser7");
        Assert.assertEquals(userLoggedOut.getStatus(),UserStatus.OFFLINE);

        mvc.perform(post("/users/login")
                .contentType("application/json;charset=UTF-8")
                .content("{\"username\": \"testUser7\",\"password\": \"wrongPass\"}"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));

        Assert.assertNotNull(userRepository.findByUsername("testUser7"));
        User userLoggedIn = userRepository.findByUsername("testUser7");
        Assert.assertEquals(userLoggedIn.getStatus(),UserStatus.OFFLINE);
    }

    @Test
    public void logoutUserCorrect() throws Exception {

        User user = new User();
        user.setPassword("pass");
        user.setUsername("testUser8");

        User testUser = userService.createUser(user);

        // Set status to ONLINE in order to pass authentication
        testUser.setStatus(UserStatus.ONLINE);
        userRepository.save(testUser);

        Assert.assertNotNull(userRepository.findByUsername("testUser8"));
        User userLoggedIn = userRepository.findByUsername("testUser8");
        Assert.assertEquals(userLoggedIn.getStatus(),UserStatus.ONLINE);

        mvc.perform(post("/users/logout")
                .header("Token",testUser.getToken())
                .contentType("application/json;charset=UTF-8")
                .content("{}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));

        Assert.assertNotNull(userRepository.findByUsername("testUser8"));
        User userLoggedOut = userRepository.findByUsername("testUser8");
        Assert.assertEquals(userLoggedOut.getStatus(),UserStatus.OFFLINE);
    }

    @Test
    public void logoutUserIncorrect() throws Exception {

        User user = new User();
        user.setPassword("pass");
        user.setUsername("testUser9");

        User testUser = userService.createUser(user);

        // Set status to ONLINE in order to pass authentication
        testUser.setStatus(UserStatus.ONLINE);
        userRepository.save(testUser);

        Assert.assertNotNull(userRepository.findByUsername("testUser9"));
        User userLoggedIn = userRepository.findByUsername("testUser9");
        Assert.assertEquals(userLoggedIn.getStatus(),UserStatus.ONLINE);

        mvc.perform(post("/users/logout")
                .header("Token","wrongToken")
                .contentType("application/json;charset=UTF-8")
                .content("{}"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));

        Assert.assertNotNull(userRepository.findByUsername("testUser9"));
        User userLoggedOut = userRepository.findByUsername("testUser9");
        Assert.assertEquals(userLoggedOut.getStatus(),UserStatus.ONLINE);
    }

    @Test
    public void accessProfileCorrect() throws Exception {

        User user = new User();
        user.setPassword("pass");
        user.setUsername("testUser2");

        User testUser = userService.createUser(user);

        // Set status to ONLINE in order to pass authentication
        testUser.setStatus(UserStatus.ONLINE);
        userRepository.save(testUser);

        Assert.assertNotNull(userRepository.findByUsername("testUser2"));

        mvc.perform(get("/users/"+testUser.getId())
                .header("Token",testUser.getToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.username").value("testUser2"))
                .andExpect(jsonPath("$.creationDate").exists())
                .andExpect(jsonPath("$.status").value("ONLINE"))
                .andExpect(jsonPath("$.birthdayDate").isEmpty());
    }

    @Test
    public void accessProfileIncorrect() throws Exception {

        User user = new User();
        user.setPassword("pass");
        user.setUsername("testUser3");

        User testUser = userService.createUser(user);

        // Set status to ONLINE in order to pass authentication
        testUser.setStatus(UserStatus.ONLINE);
        userRepository.save(testUser);

        Assert.assertNotNull(userRepository.findByUsername("testUser3"));

        Long badId = testUser.getId() + 1;

        mvc.perform(get("/users/"+badId)
                .header("Token",testUser.getToken()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @Test
    public void updateProfileCorrect() throws Exception {

        User user = new User();
        user.setPassword("pass");
        user.setUsername("testUser4");

        User testUser = userService.createUser(user);

        // Set status to ONLINE in order to pass authentication
        testUser.setStatus(UserStatus.ONLINE);
        userRepository.save(testUser);

        Assert.assertNotNull(userRepository.findByUsername("testUser4"));

        mvc.perform(put("/users/"+testUser.getId())
                .header("Token",testUser.getToken())
                .contentType("application/json;charset=UTF-8")
                .content("{\"username\": \"newName\",\"birthdayDate\": \"1970-01-01\"}"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Assert.assertNull(userRepository.findByUsername("testUser4"));
        Assert.assertNotNull(userRepository.findByUsername("newName"));

        User userNew = userRepository.findByUsername("newName");

        Assert.assertEquals(userNew.getBirthdayDate(),"1970-01-01");
    }

    @Test
    public void updateProfileIncorrect() throws Exception {

        User user = new User();
        user.setPassword("pass");
        user.setUsername("testUser5");

        User testUser = userService.createUser(user);

        // Set status to ONLINE in order to pass authentication
        testUser.setStatus(UserStatus.ONLINE);
        userRepository.save(testUser);

        Assert.assertNotNull(userRepository.findByUsername("testUser5"));

        Long badId = testUser.getId() + 1;

        mvc.perform(put("/users/"+badId)
                .header("Token",testUser.getToken())
                .contentType("application/json;charset=UTF-8")
                .content("{\"username\": \"newNameFail\",\"birthdayDate\": \"1970-01-01\"}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));

        Assert.assertNotNull(userRepository.findByUsername("testUser5"));
        Assert.assertNull(userRepository.findByUsername("newNameFail"));
    }
}
