package ch.uzh.ifi.seal.soprafs19.HelperClass;

import ch.uzh.ifi.seal.soprafs19.constant.Color;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;
import ch.uzh.ifi.seal.soprafs19.service.GameService;
import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class HelperClass {

    private GameService gameService;

    private PlayerService playerService;

    public HelperClass(GameService gameService, PlayerService playerService) {
        this.gameService = gameService;
        this.playerService = playerService;
    }

    public Game setup(boolean isGodGame) {
        // creating players and adding to queue for matchmaking
        Player player1 = newPlayer(isGodGame);
        Player player2 = newPlayer(isGodGame);

        // get the games
        return player1.getGame();
    }

    public Game statusCards2(boolean isGodGame) {
        Game godGame = setup(isGodGame);

        // setting cards
        List<SimpleGodCard> cards = new ArrayList<>();
        cards.add(SimpleGodCard.APOLLO);
        cards.add(SimpleGodCard.ARTEMIS);
        godGame.setCards(cards);
        godGame.setStatus(GameStatus.CARDS2);

        // next Turn
        nextTurn(godGame);

        return godGame;
    }

    public Game statusStartPlayer(boolean isGodGame) {
        Game godGame = statusCards2(isGodGame);

        // set cards on players
        for (Player player : godGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                player.setCard(godGame.getCards().get(0));
            } else {
                player.setCard(godGame.getCards().get(1));
            }
            playerService.savePlayer(player);
        }

        godGame.setStatus(GameStatus.STARTPLAYER);
        gameService.saveGame(godGame);

        nextTurn(godGame);
        return godGame;
    }

    public Game statusPosition1(boolean isGodGame) {
        Game game = (isGodGame ?  statusStartPlayer(isGodGame) : setup(isGodGame));

        for (Player player : game.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                player.setColor(Color.BLUE);
                playerService.savePlayer(player);
            }
        }

        game.setStatus(GameStatus.POSITION1);
        gameService.saveGame(game);
        return game;
    }

    public Game statusColor2(boolean isGodGame) {
        Game game = statusPosition1(isGodGame);

        for (Player player : game.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                game.getBoard().getFields().get(4).setWorker(player.getWorkers().get(0));
                player.getWorkers().get(0).setField(game.getBoard().getFields().get(4));

                game.getBoard().getFields().get(18).setWorker(player.getWorkers().get(1));
                player.getWorkers().get(1).setField(game.getBoard().getFields().get(18));
                playerService.savePlayer(player);
            }
        }
        game.setStatus(GameStatus.COLOR2);
        nextTurn(game);
        return game;
    }

    public Game statusPosition2(boolean isGodMode) {

        Game game = statusColor2(isGodMode);

        for (Player player : game.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                player.setColor(Color.WHITE);
                playerService.savePlayer(player);
            }
        }

        game.setStatus(GameStatus.POSITION2);
        gameService.saveGame(game);
        return game;
    }

    public Game statusMove(boolean isGodMode) {
        Game game = statusPosition2(isGodMode);

        for (Player player : game.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                game.getBoard().getFields().get(7).setWorker(player.getWorkers().get(0));
                player.getWorkers().get(0).setField(game.getBoard().getFields().get(7));

                game.getBoard().getFields().get(23).setWorker(player.getWorkers().get(1));
                player.getWorkers().get(1).setField(game.getBoard().getFields().get(23));
                playerService.savePlayer(player);
            }
        }
        game.setStatus(GameStatus.MOVE);
        nextTurn(game);
        return game;
    }

    // not based on previous step anymore
    public Game move(Game game, Field from, Field to) {
        Worker worker = from.getWorker();
        worker.setIsCurrentWorker(true);
        from.setWorker(null);
        to.setWorker(worker);
        game.setStatus(GameStatus.BUILD);
        gameService.saveGame(game);
        return game;
    }

    /**
     * creates a new player in the playerRepository
     *
     * @param isGodMode
     * @return Player
     */
    public Player newPlayer(Boolean isGodMode) {
        Player player = new Player();
        player.setIsGodMode(isGodMode);
        return playerService.createPlayer(player,null,true);
    }

    /**
     * switches who's the current Player
     * @param game
     */
    public void nextTurn(Game game) {
        for (Player player : game.getPlayers()) {
            // reverse value
            player.setIsCurrentPlayer(!player.getIsCurrentPlayer());
        }
        // save
        gameService.saveGame(game);
    }
}
