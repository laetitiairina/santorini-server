package ch.uzh.ifi.seal.soprafs19.service;


import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.HelperClass.HelperClass;
import ch.uzh.ifi.seal.soprafs19.constant.Color;
import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import ch.uzh.ifi.seal.soprafs19.entity.*;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.Before;
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

    private HelperClass helperClass;

    @Before
    public void before() {
        this.initHelper();
    }

    public void initHelper() {
        helperClass = new HelperClass(this.gameService, this.playerService);
    }

    @Test
    public void setInitialGameStatus() {

        Game simpleGame = helperClass.setup(false);
        Game godGame = helperClass.setup(true);

        Assert.assertEquals(GameStatus.COLOR1, simpleGame.getStatus());
        Assert.assertEquals(GameStatus.CARDS1, godGame.getStatus());
    }

    @Test
    public void updateCards1Successfully() {

        Game godGame = helperClass.setup(true);

        // select the two cards
        Game updatedGame = SerializationUtils.clone(godGame);
        List<SimpleGodCard> cards = new ArrayList<>();

        SimpleGodCard card1 = SimpleGodCard.APOLLO;
        cards.add(card1);

        SimpleGodCard card2 = SimpleGodCard.ARTEMIS;
        cards.add(card2);

        updatedGame.setCards(cards);

        // update the two cards
        Game result = gameService.setCards1(godGame, updatedGame);

        // Asserts
        Assert.assertNotNull(result);
        Assert.assertEquals(cards, result.getCards());
    }

    @Test
    public void updateCards1SameCards() {

        Game godGame = helperClass.setup(true);

        // select the two cards
        Game updatedGame = SerializationUtils.clone(godGame);
        List<SimpleGodCard> cards = new ArrayList<>();

        cards.add(SimpleGodCard.APOLLO);
        cards.add(SimpleGodCard.APOLLO);

        updatedGame.setCards(cards);

        // update the two cards
        Game result = gameService.setCards1(godGame, updatedGame);

        // Asserts
        Assert.assertNull(result);
    }

    @Test
    public void updateCards1OneCard() {

        Game godGame = helperClass.setup(true);

        // select the two cards
        Game updatedGame = SerializationUtils.clone(godGame);
        List<SimpleGodCard> cards = new ArrayList<>();

        cards.add(SimpleGodCard.APOLLO);

        updatedGame.setCards(cards);

        // update the two cards
        Game result = gameService.setCards1(godGame, updatedGame);

        // Asserts
        Assert.assertNull(result);
    }

    @Test
    public void updateCards2Successfully() {

        Game godGame = helperClass.statusCards2(true);

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
        Game result = gameService.setCards2(godGame, updatedGame);

        // Asserts
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.getCards().size());
        Assert.assertEquals(2, result.getPlayers().size());
    }

    @Test
    public void updateCards2WrongCard() {

        Game godGame = helperClass.statusCards2(true);

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
        Game result = gameService.setCards2(godGame, updatedGame);

        // Asserts
        Assert.assertNull(result);
    }

    @Test
    public void updateCards2WrongPlayer() {

        Game godGame = helperClass.statusCards2(true);

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
        Game result = gameService.setCards2(godGame, updatedGame);

        // Asserts
        Assert.assertNull(result);
    }

    @Test
    public void updateStartPlayerSuccessfully() {

        Game godGame = helperClass.statusStartPlayer(true);

        // select the player
        Game updatedGame = SerializationUtils.clone(godGame);

        List<Player> players = new ArrayList<>();
        players.add(updatedGame.getPlayers().get(0));
        players.get(0).setIsCurrentPlayer(true);

        updatedGame.setPlayers(players);

        // update the start player
        Game result = gameService.setStartPlayer(godGame, updatedGame);

        // Asserts
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.getPlayers().size());
    }

    @Test
    public void updateStartPlayerNotCurrentPlayer() {

        Game godGame = helperClass.statusStartPlayer(true);

        // select the player
        Game updatedGame = SerializationUtils.clone(godGame);

        List<Player> players = new ArrayList<>();
        players.add(updatedGame.getPlayers().get(0));
        players.get(0).setIsCurrentPlayer(false);

        updatedGame.setPlayers(players);

        // update start player
        Game result = gameService.setStartPlayer(godGame, updatedGame);

        // Asserts
        Assert.assertNull(result);
    }

    @Test
    public void updateStartPlayerWrongPlayer() {

        Game godGame = helperClass.statusStartPlayer(true);

        // select the player
        Game updatedGame = SerializationUtils.clone(godGame);

        List<Player> players = new ArrayList<>();
        players.add(helperClass.newPlayer(true));
        players.get(0).setIsCurrentPlayer(true);

        updatedGame.setPlayers(players);

        // update start player
        Game result = gameService.setStartPlayer(godGame, updatedGame);

        // Asserts
        Assert.assertNull(result);
    }

    @Test
    public void updateColor1Successfully() {

        Game simpleGame = helperClass.setup(false);

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
        players.remove(playerToBeRemoved);
        updatedGame.setPlayers(players);

        // update color of current player
        Game result = gameService.setColor(simpleGame, updatedGame);

        // Asserts
        Assert.assertNotNull(result);

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

        Game simpleGame = helperClass.setup(false);

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
        players.remove(playerToBeRemoved);
        updatedGame.setPlayers(players);

        // update color of current player
        Game result = gameService.setColor(simpleGame, updatedGame);

        // Assert
        Assert.assertNull(result);
    }

    @Test
    public void updateColor1NoColor () {

        Game simpleGame = helperClass.setup(false);

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
        players.remove(playerToBeRemoved);
        updatedGame.setPlayers(players);

        // update color of current player
        Game result = gameService.setColor(simpleGame, updatedGame);

        // Assert
        Assert.assertNull(result);
    }

    @Test
    public void updatePosition1Successfully() {

        // get to Position1
        Game simpleGame = helperClass.statusPosition1(false);

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

        // update position of Workers
        Game result = gameService.setPosition(simpleGame, updatedGame);

        // Asserts
        Assert.assertNotNull(result);

        List<Field> assertFields = simpleGame.getBoard().getFields();
        Assert.assertEquals(worker1, assertFields.get(4).getWorker());
        Assert.assertEquals(worker2, assertFields.get(18).getWorker());
        int count = 0;
        for (Field field : assertFields) {
            Worker worker = field.getWorker();
            if (worker == null) {
                count++;
            }
        }
        Assert.assertEquals(23, count);

    }

    @Test
    public void updatePosition1WrongPlayer() {

        // get to Position1
        Game simpleGame = helperClass.statusPosition1(false);

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

        // update position of Workers
        Game result = gameService.setPosition(simpleGame, updatedGame);

        // Asserts
        Assert.assertNull(result);
    }

    @Test
    public void updatePosition1OneField() {

        // get to Position1
        Game simpleGame = helperClass.statusPosition1(false);

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

        // update position of Workers
        Game result = gameService.setPosition(simpleGame, updatedGame);

        // Asserts
        Assert.assertNull(result);
    }

    @Test
    public void updatePosition1NoField() {

        // get to Position1
        Game simpleGame = helperClass.statusPosition1(false);

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // place Workers on two random fields
        List<Field> fields = new ArrayList<>();
        board.setFields(fields);

        // update position of Workers
        Game result = gameService.setPosition(simpleGame, updatedGame);

        // Asserts
        Assert.assertNull(result);
    }

    @Test
    public void updatePosition1SameField() {

        // get to Position1
        Game simpleGame = helperClass.statusPosition1(false);

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

        // update position of Workers
        Game result = gameService.setPosition(simpleGame, updatedGame);

        // Asserts
        Assert.assertNull(result);
    }

    @Test
    public void updateColor2Successfully() {

        // get to Color2
        Game simpleGame = helperClass.statusColor2(false);

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
        players.remove(playerToBeRemoved);
        updatedGame.setPlayers(players);

        // update color of current player
        Game result = gameService.setColor(simpleGame, updatedGame);

        // Asserts
        Assert.assertNotNull(result);

        for (Player player : result.getPlayers()) {
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
        Game simpleGame = helperClass.statusColor2(false);

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
        players.remove(playerToBeRemoved);
        updatedGame.setPlayers(players);

        // update color of current player
        Game result = gameService.setColor(simpleGame, updatedGame);

        // Asserts
        Assert.assertNull(result);
    }

    @Test
    public void updatePosition2Successfully() {

        // get to Position2
        Game simpleGame = helperClass.statusPosition2(false);

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

        // update position of Workers
        Game result = gameService.setPosition(simpleGame, updatedGame);

        // Asserts
        Assert.assertNotNull(result);

        List<Field> assertFields = simpleGame.getBoard().getFields();
        Assert.assertEquals(worker1, assertFields.get(7).getWorker());
        Assert.assertEquals(worker2, assertFields.get(23).getWorker());
        int count1 = 0;
        int count2 = 0;
        for (Field field : assertFields) {
            Worker worker = field.getWorker();
            if (worker == null) {
                count1 ++;
            } else if (!worker.getId().equals(worker1.getId()) && !worker.getId().equals(worker2.getId())) {
                count2 ++;
            }
        }
        Assert.assertEquals(21, count1);
        Assert.assertEquals(2, count2);

    }

    @Test
    public void updatePosition2OccupiedField() {

        // get to Position2
        Game simpleGame = helperClass.statusPosition2(false);

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

        // update position of Workers
        Game result = gameService.setPosition(simpleGame, updatedGame);

        // Asserts
        Assert.assertNull(result);
    }

    @Test
    public void updateMoveSuccessfully() {

        // get to Move
        Game simpleGame = helperClass.statusMove(false);

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // move a worker by one field
        List<Field> fields = new ArrayList<>();
        Worker worker = board.getFields().get(4).getWorker();

        fields.add(board.getFields().get(3));
        fields.get(0).setWorker(worker);

        fields.add(board.getFields().get(4));
        fields.get(1).setWorker(null);

        board.setFields(fields);

        // update position of Workers
        Game result = gameService.move(simpleGame, updatedGame);

        // Asserts
        Assert.assertNotNull(result);

        List<Field> assertFields = result.getBoard().getFields();
        Assert.assertEquals(worker, assertFields.get(3).getWorker());
        Assert.assertNull(assertFields.get(4).getWorker());

        int count = 0;

        for (Field field : assertFields) {
            Worker updatedWorker = field.getWorker();
            if (updatedWorker == null || !updatedWorker.getId().equals(worker.getId())) {
                count ++;
            }
        }

        Assert.assertEquals(24, count);

    }

    @Test
    public void updateBuildSuccessfully() {

        // get to Move
        Game simpleGame = helperClass.statusMove(false);
        // move a worker
        simpleGame = helperClass.move(simpleGame, simpleGame.getBoard().getFields().get(4), simpleGame.getBoard().getFields().get(3));

        // create game with chosen position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        Board board = updatedGame.getBoard();

        // build a block
        List<Field> fields = new ArrayList<>();

        fields.add(board.getFields().get(8));
        fields.get(0).setBlocks(1);

        board.setFields(fields);

        // update blocks
        Game result = gameService.move(simpleGame, updatedGame);

        // Asserts
        Assert.assertNull(result);
    }

    @Test
    public void authenticateTokenSuccessfully(){

        Game simpleGame = helperClass.setup(false);

        //create game with chosen move position
        Game updatedGame = SerializationUtils.clone(simpleGame);
        List<Player> players = updatedGame.getPlayers();
        Player currentPlayer = null;

        for(Player player : players){
            if(player.getIsCurrentPlayer()){
                currentPlayer = player;
            }
        }

        String token1 = currentPlayer.getToken();
        boolean tokenOnCurrentPlayer = gameService.checkPlayerAuthentication(simpleGame, token1);

        //Asserts
        Assert.assertNotNull(token1);
        Assert.assertTrue(tokenOnCurrentPlayer);

    }

    @Test
    public void abortGame() {
        // setup
        Game simpleGame = helperClass.setup(false);

        // abort game
        gameService.abortGame(simpleGame);

        // Asserts
        Assert.assertEquals(GameStatus.END, simpleGame.getStatus());
        for (Player player : simpleGame.getPlayers()) {
            Assert.assertFalse(player.getIsCurrentPlayer());
            Assert.assertFalse(player.getIsActive());
        }
    }

    @Test
    public void rematch() {
        // get to move
        Game godGame = helperClass.statusMove(true);

        // change game
        godGame.getBoard().getFields().get(4).setBlocks(2);
        godGame.getBoard().getFields().get(9).setBlocks(3);
        Worker worker = godGame.getBoard().getFields().get(4).getWorker();
        worker.setIsCurrentWorker(true);
        godGame.getBoard().getFields().get(4).setWorker(null);
        godGame.getBoard().getFields().get(9).setWorker(worker);
        godGame.setStatus(GameStatus.END);
        godGame.setWantsRematch(true);
        gameService.saveGame(godGame);

        // rematch
        gameService.rematch(godGame);

        // Asserts
        Assert. assertEquals(GameStatus.CARDS1, godGame.getStatus());
        Assert.assertNull(godGame.getCards());
        Assert.assertFalse(godGame.getWantsRematch());
        Assert.assertFalse(godGame.getHasMovedUp());

        for (Field field : godGame.getBoard().getFields()) {
            Assert.assertNull(field.getWorker());
            Assert.assertFalse(field.getHasDome());
            Assert.assertTrue(0 == field.getBlocks());
        }

        for (Player player : godGame.getPlayers()) {
            Assert.assertNull(player.getColor());
            Assert.assertNull(player.getWorkers().get(0).getField());
            Assert.assertNull(player.getWorkers().get(1).getField());
            Assert.assertNull(player.getCard());
        }
    }

}

