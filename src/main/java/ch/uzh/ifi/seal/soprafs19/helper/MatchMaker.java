package ch.uzh.ifi.seal.soprafs19.helper;

import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.entity.Board;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.service.GameService;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class MatchMaker {

    private static final Integer playerCount = 2;

    private static final Integer fieldCount = 25;

    private List<Player> simpleQueue;

    private List<Player> godQueue;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameService gameService;

    public MatchMaker() {
        this.simpleQueue = new ArrayList<Player>();
        this.godQueue = new ArrayList<Player>();
    }

    /**
     * Adds player to a queue
     * @param player
     */
    public void pushPlayer(Player player) {

        if (player.getIsGodMode()) {

            // Add player to god queue
            godQueue.add(player);

            // Check if player can be matched
            scanQueue(godQueue, playerCount, fieldCount);
        } else {

            // Add player to simple queue
            simpleQueue.add(player);

            // Check if player can be matched
            scanQueue(simpleQueue, playerCount, fieldCount);
        }
    }


    // Make sure this function can't be executed multiple times at the same time! (@Transactional ?)

    /**
     * Scans provided queue and matches players if possible
     * @param queue
     * @param numberOfPlayers
     */
    private void scanQueue(List<Player> queue, Integer numberOfPlayers, Integer numberOfFields) {

        List<Player> matchedPlayers = new ArrayList<Player>();

        // Check if players can be matched
        if (queue.size() >= numberOfPlayers) {

            // Pop players and match them
            for(int i = 0; i < numberOfPlayers; i++) {
                matchedPlayers.add(queue.remove(0));
            }

            // Create game with matched players
            Game game = new Game(matchedPlayers, numberOfFields);
            gameService.createGame(game);

            // Set game of matched players
            for (Player player : matchedPlayers) {
                player.setGame(game);
                playerService.updatePlayer(player);
            }
        }
    }
}
