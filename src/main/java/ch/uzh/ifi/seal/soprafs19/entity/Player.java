package ch.uzh.ifi.seal.soprafs19.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Player implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	// TODO: Implement User
	//@Column(nullable = false, unique = true)
	private Long userId;

	@Column(nullable = false)
	private Boolean isGodMode;

	@Column(nullable = false, unique = true)
	private String token;

	@ManyToOne
	@JoinColumn(name = "game_id")
	private Game game;

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

	public String getToken() {
		return token;
	}

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
