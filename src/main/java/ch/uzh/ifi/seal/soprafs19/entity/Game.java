package ch.uzh.ifi.seal.soprafs19.entity;

import ch.uzh.ifi.seal.soprafs19.constant.GameStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Game implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable = false) 
	private Board board;
	
	@Column(nullable = false)
	private Player player1;

	@Column(nullable = false)
	private Player player2;

	@Column(nullable = false)
	private Integer card1;

	@Column(nullable = false)
	private Integer card2;
	
	@Column(nullable = false)
	private Boolean isGodMode;

	@Column(nullable = false)
	private GameStatus status;

	@Column(nullable = false)
	private Long currentPlayerId;

	@Column(nullable = false)
	private Integer currentWorker;

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

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	public Integer getCard1() {
		return card1;
	}

	public void setCard1(Integer card1) {
		this.card1 = card1;
	}

	public Integer getCard2() {
		return card2;
	}

	public void setCard2(Integer card2) {
		this.card2 = card2;
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

	public Long getCurrentPlayerId() {
		return currentPlayerId;
	}

	public void setCurrentPlayerId(Long currentPlayerId) {
		this.currentPlayerId = currentPlayerId;
	}

	public Integer getCurrentWorker() {
		return currentWorker;
	}

	public void setCurrentWorker(Integer currentWorker) {
		this.currentWorker = currentWorker;
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
}
