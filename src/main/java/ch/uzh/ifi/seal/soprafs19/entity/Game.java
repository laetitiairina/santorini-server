package ch.uzh.ifi.seal.soprafs19.entity;

import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.beans.SimpleBeanInfo;
import java.io.Serializable;
import java.util.List;

@Entity
public class Game implements Serializable {

	private static final long serialVersionUID = 1L;

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
	private boolean hasMovedUp;

	@Column
	private boolean wantsRematch = false;

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

	public Boolean getHasMovedUp() {
		return hasMovedUp;
	}

	public void setHasMovedUp(boolean hasMovedUp) {
		this.hasMovedUp = hasMovedUp;
	}

	public Boolean getWantsRematch() { return wantsRematch; }

	public void setWantsRematch(boolean wantsRematch) { this.wantsRematch = wantsRematch; }

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
		}

		// Delete board and save fields in game entity directly?
		this.board = new Board(this,numberOfRows);
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
