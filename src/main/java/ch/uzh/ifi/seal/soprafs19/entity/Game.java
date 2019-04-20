package ch.uzh.ifi.seal.soprafs19.entity;

import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Game implements Serializable {
	

	private static final long serialVersionUID = 1L;

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

	@Column
	private boolean hasMovedUp;

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

	public boolean isHasMovedUp() {
		return hasMovedUp;
	}

	public void setHasMovedUp(boolean hasMovedUp) {
		this.hasMovedUp = hasMovedUp;
	}

	public Game() {
	}

	public Game(List<Player> matchedPlayers, Integer numberOfRows) {
		this.players = matchedPlayers;
		this.isGodMode = matchedPlayers.get(0).getIsGodMode();
		this.status = GameStatus.CARDS1;

		// set Start Player according to Simple or God Mode
        // default value of Player.isCurrentPlayer is false
        if (this.isGodMode) {
            matchedPlayers.get(0).setCurrentPlayer(true);
        } else {
            // TODO: @Florian add logic for simple game mode (birthday, etc..)
        }

		// TODO: FIX: Board is null
		// Delete board and save fields in game entity directly?
		this.board = new Board(numberOfRows);
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
        return status.ordinal();
	}

	public GameStatus getGameStatusEnum (int status) {
        return GameStatus.values()[status];
    }

}
