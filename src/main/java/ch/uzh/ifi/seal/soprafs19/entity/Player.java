package ch.uzh.ifi.seal.soprafs19.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Player implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="player_id")
	private Long id;

	// TODO: Implement User
	//@Column(nullable = false, unique = true)
	private Long userId;

	@Column(nullable = false)
	private Boolean isGodMode;
	
	@ManyToOne
	@JoinColumn(name = "card_id")
	private Card card;
	
	@Column()
	private String color;

	@Column(nullable = false)
	private String token;

	@ManyToOne
	@JoinColumn(name = "game_id")
	private Game game;

	@OneToMany
	@Column
	private List<Worker> workers;

	private boolean isCurrentPlayer;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Boolean getIsGodMode() {
		return isGodMode;
	}

	public void setIsGodMode(Boolean isGodMode) {
		this.isGodMode = isGodMode;
	}
	
	public Card getCard() {
		return card;
	}

  	public void setCard(Card card) {
    this.card = card;
  }

  	public String getColor() {
    return color;
  }

  	public void setColor(String color) {
    this.color = color;
  }

	@JsonIgnore
	public String getToken() {
		return token;
	}

	@JsonIgnore
	public void setToken(String token) {
		this.token = token;
	}

	@JsonIgnore
	public Game getGame() {
		return game;
	}

	@JsonIgnore
	public void setGame(Game game) {
		this.game = game;
	}

	public Long getGame_id() {
		if (game != null) {
			return game.getId();
		}
		return null;
	}

	public List<Worker> getWorkers() {
		return workers;
	}

	public void setWorkers(List<Worker> workers) {
		this.workers = workers;
	}

	public boolean getIsCurrentPlayer() {
		return isCurrentPlayer;
	}

	public void setIsCurrentPlayer(boolean isCurrentPlayer) {
		this.isCurrentPlayer = isCurrentPlayer;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Player)) {
			return false;
		}
		Player player = (Player) o;
		return this.getId().equals(player.getId());
	}
}
