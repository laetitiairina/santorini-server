package ch.uzh.ifi.seal.soprafs19.entity;

import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Game implements Serializable {

	private static final long serialVersionUID = 1L;

	public Game() {}

	public Game(List<Player> matchedPlayers, Integer numberOfRows) {
		this.players = matchedPlayers;
		this.isGodMode = matchedPlayers.get(0).getIsGodMode();

		// set Start Player according to Simple or God Mode
		// default value of Player.isCurrentPlayer is false
		if (this.isGodMode) {
			this.status = GameStatus.CARDS1;
			matchedPlayers.get(0).setIsCurrentPlayer(true);
		} else {
			this.status = GameStatus.COLOR1;
            // TODO: Add logic for simple game mode (birthday, etc..)
			matchedPlayers.get(0).setIsCurrentPlayer(true);
		}

		// Set game of matched players
		for (Player player : matchedPlayers) {
			player.setGame(this);
			player.didMove();
		}

		// Delete board and save fields in game entity directly?
		this.board = new Board(this,numberOfRows);
	}

	@Id
	@GeneratedValue
	@Column(name = "game_id")
	private Long id;

	@OneToOne(mappedBy = "game", cascade = CascadeType.ALL)
	@JoinColumn(name="board_id")
	private Board board;

	@OneToMany(mappedBy = "game", cascade = CascadeType.MERGE)
	@Column(nullable = false)
	private List<Player> players;

	@Column()
	@ElementCollection(targetClass=SimpleGodCard.class, fetch = FetchType.EAGER)
	private List<SimpleGodCard> cards;
	
	@Column(nullable = false)
	private Boolean isGodMode;

	@Column(nullable = false)
	private GameStatus status;

	@Column
	private int blockDifference = 0;

	@Column
	private String message;

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

	public List<SimpleGodCard> getCards() {
		return cards;
	}

	public void setCards(List<SimpleGodCard> cards) {
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

	public Integer getBlockDifference() {
		return blockDifference;
	}

	public void setBlockDifference(int blockDifference) {
		this.blockDifference = blockDifference;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Game)) {
			return false;
		}
		Game game = (Game) o;
		return this.getId().equals(game.getId());
	}

	public int getGameStatusInt (GameStatus status) {
        return status.ordinal();
	}

	public GameStatus getGameStatusEnum (int status) {
        return GameStatus.values()[status];
    }

    public Player getCurrentPlayer() {
		List<Player> currentPlayers = players.stream().filter(player -> player.getIsCurrentPlayer()).collect(Collectors.toList());
		if (currentPlayers.size() == 1) {
			return currentPlayers.get(0);
		}
		return null;
	}

}
