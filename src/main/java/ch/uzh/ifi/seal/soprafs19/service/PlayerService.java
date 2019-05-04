package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.helper.MatchMaker;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

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

    //@Autowired
    /*public PlayerService(PlayerRepository playerRepository, MatchMaker matchMaker) {
        this.playerRepository = playerRepository;
        this.matchMaker = matchMaker;
    }*/

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

    /**
     * Create a new player and start matchmaking
     * @param newPlayer
     * @return
     */
    public Player createPlayer(Player newPlayer) {

        // Check if a userId was given
        if (newPlayer.getUserId() == null) {
            newPlayer.setToken(UUID.randomUUID().toString());
        } else {
            Optional<User> user = userRepository.findById(newPlayer.getUserId());

            // Check if userId of player is valid and token is correct
            // TODO: NOT IMPORTANT: Clean up and return pretty error messages
            if (user.isEmpty()) {
                return null;
            }
            if (!user.get().getToken().equals(newPlayer.getToken())) {
                return null;
            }
        }

        playerRepository.save(newPlayer);

        // Push player to matchmaking queue
        matchMaker.pushPlayer(newPlayer);

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

    public void incrementPolls(Player player) {
        player.setPolls(player.getPolls() + 1);
        playerRepository.save(player);
    }
}
