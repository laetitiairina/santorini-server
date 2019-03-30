package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;


    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /*
    public Iterable<Player> getPlayers() {
        return this.playerRepository.findAll();
    }
    */

    public Player createPlayer(Player newPlayer) {

        // TODO: set properties of newPlayer

        playerRepository.save(newPlayer);
        log.debug("Created Information for Player: {}", newPlayer);
        return newPlayer;
    }
}
