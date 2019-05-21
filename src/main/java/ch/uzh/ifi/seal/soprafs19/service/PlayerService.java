package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.helper.MatchMaker;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Primary
@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchMaker matchMaker;

    /*
    //@Autowired
    public PlayerService(PlayerRepository playerRepository, MatchMaker matchMaker) {
        this.playerRepository = playerRepository;
        this.matchMaker = matchMaker;
    }
    */

    /*
    public Iterable<Player> getPlayers() {
        return this.playerRepository.findAll();
    }
    */

    /**
     * Get player by id
     * @param id
     * @return
     */
    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    public List<Player> getAllActivePlayers(){
        return playerRepository.findByIsActive(true);
    }

    public Player getPlayerByToken(String token) {
        return playerRepository.findByToken(token);
    }

    /**
     * Create a new player and start matchmaking
     * @param newPlayer
     * @return
     */
    public Player createPlayer(Player newPlayer, String token, Boolean matchmaking) {

        // Player token has to be unique, can't be a copy of user token (see CheckPolling)
        newPlayer.setToken(UUID.randomUUID().toString());

        // Check if a userId was given
        if (newPlayer.getUserId() != null) {
            Optional<User> user = userRepository.findById(newPlayer.getUserId());

            // TODO: LOW PRIORITY: Clean up and return pretty error messages
            // Check if userId of player is valid
            if (user.isEmpty()) {
                return null;
            }
            // Check if token was given
            if (token == null) {
                return null;
            }
            // Check if token matches user token and user is online
            if (!user.get().getToken().equals(token) || !user.get().getStatus().equals(UserStatus.ONLINE)) {
                return null;
            }

            // Set username of player
            newPlayer.setUsername(user.get().getUsername());
        }

        playerRepository.save(newPlayer);

        if (matchmaking) {
            // Push player to matchmaking queue
            matchMaker.pushPlayer(newPlayer);
        }

        log.debug("Created Information for Player: {}", newPlayer);
        return newPlayer;
    }

    /**
     * Update player
     * @param newPlayer
     */
    public void updatePlayer(Player newPlayer) {
        playerRepository.save(newPlayer);
    }

    /**
     * Remove player from matchmaking queue and set player to inactive
     * @param player
     */
    public void abortSearch(Player player) {
        matchMaker.removePlayer(player);
        player.setIsActive(false);
        playerRepository.save(player);
    }

    public void savePlayer (Player player) {
        playerRepository.save(player);
    }
}
