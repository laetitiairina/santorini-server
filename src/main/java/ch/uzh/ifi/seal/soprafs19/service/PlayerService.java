package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.helper.MatchMaker;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
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
import java.util.UUID;

@Primary
@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    @Autowired
    private PlayerRepository playerRepository;

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

        if (newPlayer.getUserId() == null) {
            newPlayer.setToken(UUID.randomUUID().toString());
        } else {
            // TODO: Implement player creation with token from user
            //newPlayer.setToken(userRepository.findById(newPlayer.getUserId()).get().getToken());
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
}
