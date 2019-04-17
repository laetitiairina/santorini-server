package ch.uzh.ifi.seal.soprafs19.entity;

import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.EnumBiMap;
import com.google.common.collect.HashBiMap;

@Entity
public class Game implements Serializable {
	

	private static final long serialVersionUID = 1L;

	private static final Map statesMap = new HashMap<Integer, GameStatus>() {{
		put(0, GameStatus.CARDS10);
		put(1, GameStatus.CARDS2);
		put(2, GameStatus.STARTPLAYER);
		put(3, GameStatus.COLOR1);
		put(4, GameStatus.POSITION1);
		put(5, GameStatus.COLOR2);
		put(6, GameStatus.POSITION2);
		put(7, GameStatus.MOVE);
		put(8, GameStatus.BUILD);
		put(9, GameStatus.END);
	}};

    private static final BiMap<Integer, GameStatus> statesBiMap = EnumBiMap.create(statesMap);

	@Id
	@GeneratedValue
	@Column(name = "game_id")
	private Long id;

	@OneToOne(mappedBy = "game", cascade = CascadeType.ALL)
	//@Column(nullable = false)
	private Board board;

	@OneToMany(mappedBy = "game")
	@Column(nullable = false)
	private List<Player> players;

	@OneToMany(mappedBy = "game")
	private List<Card> cards;
	
	@Column(nullable = false)
	private Boolean isGodMode;

	@Column(nullable = false)
	private GameStatus status;

	// TODO: Fix currentPlayer or include isCurrentPlayer in player entity
	/*
	@OneToOne(mappedBy = "game")
	//@Column(nullable = false)
	private Player currentPlayer;
	*/

	private Worker currentWorker;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public Boolean getIsGodMode() {
		return isGodMode;
	}

	public void setIsGodMode(Boolean isGodMode) {
		this.isGodMode = isGodMode;
	}

	public GameStatus getStatus() {
		return status;
	}

	public void setStatus(GameStatus status) {
		this.status = status;
	}

	/*
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
	*/

	public Worker getCurrentWorker() {
		return currentWorker;
	}

	public void setCurrentWorker(Worker currentWorker) {
		this.currentWorker = currentWorker;
	}

	public Game() {
	}

	public Game(List<Player> matchedPlayers, Integer numberOfFields) {
		this.players = matchedPlayers;
		this.isGodMode = matchedPlayers.get(0).getIsGodMode();
		this.status = GameStatus.CARDS10;
		//this.currentPlayer = matchedPlayers.get(0);

		// Delete board and save fields in game entity directly?
		Board newBoard = new Board(numberOfFields);
		this.board = newBoard;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Game)) {
			return false;
		}
		Game game = (Game) o;
		return this.getId().equals(game.getId());
	}

	public int getGameStatusInt (GameStatus status) {
		return statesBiMap.inverse().get(status);
	}

	public GameStatus getGameStatusEnum (int status) {
	    return statesBiMap.get(status);
    }

}
