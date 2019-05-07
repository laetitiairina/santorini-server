package ch.uzh.ifi.seal.soprafs19.helper;

import ch.uzh.ifi.seal.soprafs19.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Service
@Transactional
public class CheckPolling implements Runnable {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MatchMaker matchMaker;

    private Thread t;
    private String threadName;
    private int POLL_TIME = 2000;
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
            // check if polls were changed
            player = playerService.getPlayerById(player.getId()).get();
            int updatedPolls = player.getPolls();
            if (updatedPolls == polls) {
                // still in queue
                if (player.getGame() == null) {
                    matchMaker.removePlayer(player);
                }
                // playing game
                else {
                    Game game = player.getGame();
                    // let game end inform front-end of crash
                    game.getPlayers().get(0).setIsCurrentPlayer(false);
                    game.getPlayers().get(1).setIsCurrentPlayer(false);
                    game.setStatus(GameStatus.END);
                    gameRepository.save(game);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Thread " +  threadName + " interrupted.");
        }
        System.out.println("Thread " +  threadName + " exiting.");
    }

    public void start () {
        System.out.println("Starting " +  threadName );
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
    }
}
