package ch.uzh.ifi.seal.soprafs19.helper;

import ch.uzh.ifi.seal.soprafs19.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
public class CheckPolling implements Runnable {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerRepository playerRepository;


    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MatchMaker matchMaker;

    private String threadName;
    private int POLL_TIME = 10000;
    private Player player;

    public void setName (String name) {
        threadName = name;
    }

    public void setPlayer (Player player) {
        this.player = player;
    }

    public void run() {
        System.out.println("Running " +  threadName );
        try {
            int polls = player.getPolls();

            Thread.sleep(POLL_TIME);
            player = playerService.getPlayerById(player.getId()).get();
            // free player
            player.setIsLocked(false);
            playerRepository.save(player);

            // check if polls were changed
            int updatedPolls = player.getPolls();

            if (updatedPolls == polls) {
                // still in queue
                if (player.getGame() == null) {
                    System.out.println("A");
                    matchMaker.removePlayer(player);
                }
                // playing game
                else {
                    System.out.println("B");
                    Game game = gameRepository.findById(player.getGame().getId()).get();
                    // let game end inform front-end of crash
                    game.getPlayers().get(0).setIsCurrentPlayer(false);
                    game.getPlayers().get(1).setIsCurrentPlayer(false);
                    game.setStatus(GameStatus.END);
                    gameRepository.save(game);
                }
            } else {
                run();
            }
        } catch (InterruptedException e) {
            System.out.println("Thread " +  threadName + " interrupted.");
        }
        System.out.println("Thread " +  threadName + " exiting.");
    }
}
