package ch.uzh.ifi.seal.soprafs19.helper;

import ch.uzh.ifi.seal.soprafs19.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs19.service.GameService;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
public class CheckPolling implements Runnable {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameService gameService;

    // 10 seconds
    private int POLL_TIME_MAX = 10000;

    @Scheduled(fixedRate = 1000)
    @Async("taskExecutor")
    public void run() {
        System.out.println("Check polling");
        for (Player player:playerService.getAllActivePlayers()) {
            if (player.lastPoll() > POLL_TIME_MAX) {
                // still in queue
                if (player.getGame() == null) {
                    // Remove player from queue if player hasn't polled in x seconds
                    System.out.println("Player removed from queue");
                    playerService.abortSearch(player);
                // playing game
                } else if (player.getGame().getStatus() != GameStatus.END) {
                    // Abort game if player hasn't polled in x seconds
                    System.out.println("Game aborted");
                    gameService.abortGame(player.getGame());
                }
            }
        }
    }
}
