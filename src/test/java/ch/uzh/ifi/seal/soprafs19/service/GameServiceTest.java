package ch.uzh.ifi.seal.soprafs19.service;


import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.Color;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import ch.uzh.ifi.seal.soprafs19.entity.*;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;


/**
 * Test class for the GameResource REST resource.
 *
 * @see GameService
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    private Game simpleGame;
    private Game godGame;

    public void setup() {
        // creating players and adding to queue for matchmaking
        // simple
        Player player1 = newPlayer(false);
        Player player2 = newPlayer(false);
        // god
        Player player3 = newPlayer(true);
        Player player4 = newPlayer(true);

        // get the games
        simpleGame = player1.getGame();
        godGame = player3.getGame();
    }

    @Test
    public void setInitialGameStatus() {

        setup();

        Assert.assertEquals(GameStatus.COLOR1, simpleGame.getStatus());
        Assert.assertEquals(GameStatus.CARDS1, godGame.getStatus());
    }

    @Test
    public void updateCards1Successfully() {

        setup();

        // select the two cards
        Game updatedGame = SerializationUtils.clone(godGame);
        List<SimpleGodCard> cards = new ArrayList<>();

        SimpleGodCard card1 = SimpleGodCard.APOLLO;
        cards.add(card1);

        SimpleGodCard card2 = SimpleGodCard.ARTEMIS;
        cards.add(card2);

        updatedGame.setCards(cards);

        // update the two cards
        boolean isSuccessful = gameService.updateGame(godGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);

        Assert.assertEquals(GameStatus.CARDS2, godGame.getStatus());
        Assert.assertEquals(cards, godGame.getCards());

    }

    @Test
    public void updateCards1SameCards() {

        setup();

        // select the two cards
        Game updatedGame = SerializationUtils.clone(godGame);
        List<SimpleGodCard> cards = new ArrayList<>();

        cards.add(SimpleGodCard.APOLLO);
        cards.add(SimpleGodCard.APOLLO);

        updatedGame.setCards(cards);

        // update the two cards
        boolean isSuccessful = gameService.updateGame(godGame, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
        Assert.assertEquals(GameStatus.CARDS1, godGame.getStatus());
        Assert.assertNull(godGame.getCards());

    }

    @Test
    public void updateCards1OneCard() {

        setup();

        // select the two cards
        Game updatedGame = SerializationUtils.clone(godGame);
        List<SimpleGodCard> cards = new ArrayList<>();

        cards.add(SimpleGodCard.APOLLO);

        updatedGame.setCards(cards);

        // update the two cards
        boolean isSuccessful = gameService.updateGame(godGame, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
        Assert.assertEquals(GameStatus.CARDS1, godGame.getStatus());
        Assert.assertNull(godGame.getCards());

    }

    @Test
    public void updateCards2Successfully() {

        updateCards1Successfully();

        // select the two cards
        Game updatedGame = SerializationUtils.clone(godGame);
        List<Player> players = new ArrayList<>();

        for (Player player : updatedGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                player.setCard(updatedGame.getCards().get(0));
                players.add(player);
            }
        }
        updatedGame.setPlayers(players);

        // update the two cards
        boolean isSuccessful = gameService.updateGame(godGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
        Assert.assertEquals(GameStatus.STARTPLAYER, godGame.getStatus());
        Assert.assertEquals(2, godGame.getCards().size());
        Assert.assertEquals(2, godGame.getPlayers().size());

    }

    @Test
    public void updateCards2WrongCard() {

        updateCards1Successfully();

        // select the two cards
        Game updatedGame = SerializationUtils.clone(godGame);
        List<Player> players = new ArrayList<>();

        for (Player player : updatedGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                player.setCard(SimpleGodCard.HEPHAESTUS);
                players.add(player);
            }
        }
        updatedGame.setPlayers(players);

        // update the two cards
        boolean isSuccessful = gameService.updateGame(godGame, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
        Assert.assertEquals(GameStatus.CARDS2, godGame.getStatus());
        Assert.assertNull(godGame.getPlayers().get(0).getCard());
        Assert.assertNull(godGame.getPlayers().get(1).getCard());

    }

    @Test
    public void updateCards2WrongPlayer() {

        updateCards1Successfully();

        // select the two cards
        Game updatedGame = SerializationUtils.clone(godGame);
        List<Player> players = new ArrayList<>();

        for (Player player : updatedGame.getPlayers()) {
            if (!player.getIsCurrentPlayer()) {
                player.setCard(updatedGame.getCards().get(0));
                players.add(player);
            }
        }
        updatedGame.setPlayers(players);

        // update the two cards
        boolean isSuccessful = gameService.updateGame(godGame, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
        Assert.assertEquals(GameStatus.CARDS2, godGame.getStatus());
        Assert.assertNull(godGame.getPlayers().get(0).getCard());
        Assert.assertNull(godGame.getPlayers().get(1).getCard());

    }

    @Test
    public void updateStartPlayerSuccessfully() {

        updateCards2Successfully();

        // select the player
        Game updatedGame = SerializationUtils.clone(godGame);

        List<Player> players = new ArrayList<>();
        players.add(updatedGame.getPlayers().get(0));
        players.get(0).setIsCurrentPlayer(true);

        updatedGame.setPlayers(players);

        // update the two cards
        boolean isSuccessful = gameService.updateGame(godGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
        Assert.assertEquals(GameStatus.COLOR1, godGame.getStatus());
        Assert.assertEquals(2, godGame.getPlayers().size());

    }

    @Test
    public void updateStartPlayerNotCurrentPlayer() {

        updateCards2Successfully();

        // select the player
        Game updatedGame = SerializationUtils.clone(godGame);

        List<Player> players = new ArrayList<>();
        players.add(updatedGame.getPlayers().get(0));
        players.get(0).setIsCurrentPlayer(false);

        updatedGame.setPlayers(players);

        // update the two cards
        boolean isSuccessful = gameService.updateGame(godGame, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
        Assert.assertEquals(GameStatus.STARTPLAYER, godGame.getStatus());
        Assert.assertEquals(2, godGame.getPlayers().size());

    }

    @Test
    public void updateStartPlayerWrongPlayer() {

        updateCards2Successfully();

        // select the player
        Game updatedGame = SerializationUtils.clone(godGame);

        List<Player> players = new ArrayList<>();
        players.add(newPlayer(true));
        players.get(0).setIsCurrentPlayer(true);

        updatedGame.setPlayers(players);

        // update the two cards
        boolean isSuccessful = gameService.updateGame(godGame, updatedGame);

        // Asserts
        // TODO: wait for entity game etc. not to be null!
        //Assert.assertFalse(isSuccessful);
        //Assert.assertEquals(GameStatus.STARTPLAYER, godGame.getStatus());
        //Assert.assertEquals(2, godGame.getPlayers().size());

    }

    @Test
    public void updateColor1Successfully() {

        setup();

        // create game with chosen color
        Game updatedGame = SerializationUtils.clone(simpleGame);
        List<Player> players = updatedGame.getPlayers();
        Player playerToBeRemoved = null;

        // set current player's color to blue and remove other player
        for (Player player : players) {
            if (player.getIsCurrentPlayer()) {
                player.setColor(Color.BLUE);
            } else {
                playerToBeRemoved = player;
            }
        }
        Assert.assertTrue(players.remove(playerToBeRemoved));
        updatedGame.setPlayers(players);
        Assert.assertEquals(1, updatedGame.getPlayers().size());

        // update color of current player
        boolean isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);

        Assert.assertEquals(GameStatus.POSITION1, simpleGame.getStatus());

        for (Player player : simpleGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                Assert.assertEquals(Color.BLUE, player.getColor());
            } else {
                Assert.assertNull(player.getColor());
            }
        }

    }

    @Test
    public void updateColor1WrongPlayer () {

        setup();

        // create game with chosen color
        Game updatedGame = SerializationUtils.clone(simpleGame);
        List<Player> players = updatedGame.getPlayers();
        Player playerToBeRemoved = null;

        // here: current player is removed and other player's color is set.
        for (Player player : players) {
            if (player.getIsCurrentPlayer()) {
                playerToBeRemoved = player;
            } else {
                player.setColor(Color.BLUE);
            }
        }
        Assert.assertTrue(players.remove(playerToBeRemoved));
        updatedGame.setPlayers(players);
        Assert.assertEquals(1, updatedGame.getPlayers().size());

        // update color of current player
        boolean isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        Assert.assertFalse(isSuccessful);
        Assert.assertEquals(GameStatus.COLOR1, simpleGame.getStatus());
    }

    @Test
    public void updateColor1NoColor () {

        setup();

        // create game with chosen color
        Game updatedGame = SerializationUtils.clone(simpleGame);
        List<Player> players = updatedGame.getPlayers();
        Player playerToBeRemoved = null;

        // here: current player is removed and other player's color is set.
        for (Player player : players) {
            if (!player.getIsCurrentPlayer()) {
                playerToBeRemoved = player;
            }
        }
        Assert.assertTrue(players.remove(playerToBeRemoved));
        updatedGame.setPlayers(players);
        Assert.assertEquals(1, updatedGame.getPlayers().size());

        // update color of current player
        boolean isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        Assert.assertFalse(isSuccessful);
        Assert.assertEquals(GameStatus.COLOR1, simpleGame.getStatus());
    }

    @Test
    public void updatePosition1Successfully() {

        // get to Position1
        updateColor1Successfully();

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // create Workers
        Worker worker1 = null;
        Worker worker2 = null;

        for (Player player : updatedGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                worker1 = player.getWorkers().get(0);
                worker2 = player.getWorkers().get(1);
            }
        }

        // place Workers on two random fields
        List<Field> fields = new ArrayList<>();
        board.getFields().get(4).setWorker(worker1);
        fields.add(board.getFields().get(4));
        board.getFields().get(18).setWorker(worker2);
        fields.add(board.getFields().get(18));
        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
        Assert.assertEquals(GameStatus.COLOR2, simpleGame.getStatus());

        List<Field> assertFields = simpleGame.getBoard().getFields();
        Assert.assertEquals(worker1, assertFields.get(4).getWorker());
        Assert.assertEquals(worker2, assertFields.get(18).getWorker());
        int count = 0;
        for (Field field : assertFields) {
            Worker worker = field.getWorker();
            if (worker != worker1 && worker != worker2) {
                Assert.assertNull(worker);
                count ++;
            }
        }
        Assert.assertEquals(23, count);

    }

    @Test
    public void updatePosition1WrongPlayer() {

        // get to Position1
        updateColor1Successfully();

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // create Workers
        Worker worker1 = null;
        Worker worker2 = null;

        for (Player player : updatedGame.getPlayers()) {
            if (!player.getIsCurrentPlayer()) {
                worker1 = player.getWorkers().get(0);
                worker2 = player.getWorkers().get(1);
            }
        }

        // place Workers on two random fields
        List<Field> fields = new ArrayList<>();
        board.getFields().get(4).setWorker(worker1);
        fields.add(board.getFields().get(4));
        board.getFields().get(18).setWorker(worker2);
        fields.add(board.getFields().get(18));
        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
        Assert.assertEquals(GameStatus.POSITION1, simpleGame.getStatus());

    }

    @Test
    public void updatePosition1OneField() {

        // get to Position1
        updateColor1Successfully();

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // create Workers
        Worker worker1 = null;

        for (Player player : updatedGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                worker1 = player.getWorkers().get(0);
            }
        }

        // place Workers on two random fields
        List<Field> fields = new ArrayList<>();
        // only one field instead of two
        board.getFields().get(4).setWorker(worker1);
        fields.add(board.getFields().get(4));
        board.setFields(fields);

        Assert.assertEquals(1, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
        Assert.assertEquals(GameStatus.POSITION1, simpleGame.getStatus());

    }

    @Test
    public void updatePosition1NoField() {

        // get to Position1
        updateColor1Successfully();

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // place Workers on two random fields
        List<Field> fields = new ArrayList<>();
        board.setFields(fields);

        Assert.assertEquals(0, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
        Assert.assertEquals(GameStatus.POSITION1, simpleGame.getStatus());

    }

    @Test
    public void updatePosition1SameField() {

        // get to Position1
        updateColor1Successfully();

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // create Workers
        Worker worker1 = null;
        Worker worker2 = null;

        for (Player player : updatedGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                worker1 = player.getWorkers().get(0);
                worker2 = player.getWorkers().get(1);
            }
        }

        // place Workers on two random fields
        List<Field> fields = new ArrayList<>();
        board.getFields().get(4).setWorker(worker1);
        fields.add(board.getFields().get(4));
        board.getFields().get(4).setWorker(worker2);
        fields.add(board.getFields().get(4));
        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
        Assert.assertEquals(GameStatus.POSITION1, simpleGame.getStatus());

    }

    @Test
    public void updateColor2Successfully() {

        // get to Color2
        updatePosition1Successfully();

        // create game with chosen color
        Game updatedGame = SerializationUtils.clone(simpleGame);
        List<Player> players = updatedGame.getPlayers();
        Player playerToBeRemoved = null;

        // set current player's color to red and remove other player
        for (Player player : players) {
            if (player.getIsCurrentPlayer()) {
                player.setColor(Color.WHITE);
            } else {
                playerToBeRemoved = player;
            }
        }
        Assert.assertTrue(players.remove(playerToBeRemoved));
        updatedGame.setPlayers(players);
        Assert.assertEquals(1, updatedGame.getPlayers().size());

        // update color of current player
        boolean isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);

        Assert.assertEquals(GameStatus.POSITION2, simpleGame.getStatus());

        for (Player player : simpleGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                Assert.assertEquals(Color.WHITE, player.getColor());
            } else {
                Assert.assertEquals(Color.BLUE, player.getColor());
            }
        }

    }

    @Test
    public void updateColor2SameColor() {

        // get to Color2
        updatePosition1Successfully();

        // create game with chosen color
        Game updatedGame = SerializationUtils.clone(simpleGame);
        List<Player> players = updatedGame.getPlayers();
        Player playerToBeRemoved = null;

        // set current player's color to red and remove other player
        for (Player player : players) {
            if (player.getIsCurrentPlayer()) {
                player.setColor(Color.BLUE);
            } else {
                playerToBeRemoved = player;
            }
        }
        Assert.assertTrue(players.remove(playerToBeRemoved));
        updatedGame.setPlayers(players);
        Assert.assertEquals(1, updatedGame.getPlayers().size());

        // update color of current player
        boolean isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
        Assert.assertEquals(GameStatus.COLOR2, simpleGame.getStatus());

    }

    @Test
    public void updatePosition2Successfully() {

        // get to Position2
        updateColor2Successfully();

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // create Workers
        Worker worker1 = null;
        Worker worker2 = null;

        for (Player player : updatedGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                worker1 = player.getWorkers().get(0);
                worker2 = player.getWorkers().get(1);
            }
        }

        // place Workers on two random fields
        List<Field> fields = new ArrayList<>();
        board.getFields().get(7).setWorker(worker1);
        fields.add(board.getFields().get(7));
        board.getFields().get(23).setWorker(worker2);
        fields.add(board.getFields().get(23));
        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
        Assert.assertEquals(GameStatus.MOVE, simpleGame.getStatus());

        List<Field> assertFields = simpleGame.getBoard().getFields();
        Assert.assertEquals(worker1, assertFields.get(7).getWorker());
        Assert.assertEquals(worker2, assertFields.get(23).getWorker());
        int count1 = 0;
        int count2 = 0;
        for (Field field : assertFields) {
            Worker worker = field.getWorker();
            if (worker == null) {
                count1 ++;
            } else if (worker != worker1 && worker != worker2) {
                count2 ++;
            }
        }
        Assert.assertEquals(21, count1);
        Assert.assertEquals(2, count2);

    }

    @Test
    public void updatePosition2OccupiedField() {

        // get to Position2
        updateColor2Successfully();

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // create Workers
        Worker worker1 = null;
        Worker worker2 = null;

        for (Player player : updatedGame.getPlayers()) {
            if (player.getIsCurrentPlayer()) {
                worker1 = player.getWorkers().get(0);
                worker2 = player.getWorkers().get(1);
            }
        }

        // place Workers on two random fields
        List<Field> fields = new ArrayList<>();
        board.getFields().get(7).setWorker(worker1);
        fields.add(board.getFields().get(7));
        // same field as in Position1
        board.getFields().get(18).setWorker(worker2);
        fields.add(board.getFields().get(18));
        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertFalse(isSuccessful);
        Assert.assertEquals(GameStatus.POSITION2, simpleGame.getStatus());

    }

    @Test
    public void updateMoveSuccessfully() {

        // get to Move
        updatePosition2Successfully();

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // move a worker by one field
        List<Field> fields = new ArrayList<>();
        Worker worker = board.getFields().get(4).getWorker();

        fields.add(board.getFields().get(5));
        fields.get(0).setWorker(worker);

        fields.add(board.getFields().get(4));
        fields.get(1).setWorker(null);

        board.setFields(fields);

        Assert.assertEquals(2, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
        Assert.assertEquals(GameStatus.BUILD, simpleGame.getStatus());

        List<Field> assertFields = simpleGame.getBoard().getFields();
        Assert.assertEquals(worker, assertFields.get(5).getWorker());
        Assert.assertNull(assertFields.get(4).getWorker());

        int count = 0;

        for (Field field : assertFields) {
            Worker updatedWorker = field.getWorker();
            if (updatedWorker != worker) {
                count ++;
            }
        }

        Assert.assertEquals(24, count);

    }

    @Test
    public void updateBuildSuccessfully() {

        // get to Build
        updateMoveSuccessfully();

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // build a block
        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(13));
        fields.get(0).setBlocks(1);

        board.setFields(fields);

        Assert.assertEquals(1, board.getFields().size());

        // update position of Workers
        boolean isSuccessful = gameService.updateGame(simpleGame, updatedGame);

        // Asserts
        Assert.assertTrue(isSuccessful);
        Assert.assertEquals(GameStatus.MOVE, simpleGame.getStatus());

        List<Field> assertFields = simpleGame.getBoard().getFields();
        long blocks = assertFields.get(13).getBlocks();
        Assert.assertEquals((long) 1, blocks);

        int count = 0;

        for (Field field : assertFields) {
            if (field.getBlocks() == 0) {
                count ++;
            }
        }

        Assert.assertEquals(24, count);

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
        return playerService.createPlayer(player);
    }

}
