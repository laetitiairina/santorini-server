package ch.uzh.ifi.seal.soprafs19.helper;

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

@Component
@Transactional
public class CheckPolling implements Runnable {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameService gameService;

    // 1 seconds
    private int QUEUE_POLL_TIME_MAX = 1000;

    // 30 seconds
    private int GAME_POLL_TIME_MAX = 30000;

    // 120 seconds
    private int GAME_TURN_TIME_MAX = 120000;

    @Scheduled(fixedRate = 1000)
    @Async("taskExecutor")
    public void run() {
        System.out.println("Check polling");
        for (Player player:playerService.getAllActivePlayers()) {
            // still in queue
            if (player.getGame() == null) {
                if (player.lastPoll() > QUEUE_POLL_TIME_MAX) {
                    // Remove player from queue if player hasn't polled in x seconds
                    System.out.println("Player removed from queue");
                    playerService.abortSearch(player);
                }
            // playing game
            } else if (player.getGame().getStatus() != GameStatus.END) {
                if (player.lastPoll() > GAME_POLL_TIME_MAX) {
                    // Abort game if player hasn't polled in x seconds
                    System.out.println("Game aborted");
                    //gameService.abortGame(player.getGame());
                    gameService.abortGameWithWinner(player.getGame(),player,"left the game!");
                }

                if (player.getIsCurrentPlayer() && player.lastMove() > GAME_TURN_TIME_MAX) {
                    // Abort game if player takes too long
                    System.out.println("Game aborted");
                    //gameService.abortGame(player.getGame());
                    gameService.abortGameWithWinner(player.getGame(),player,"took too long!");
                }
            }
        }
    }
}
