package ch.uzh.ifi.seal.soprafs19.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Player implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable = false, unique = true)
	private Long userId;

	private Long gameId;
	
	private Integer card;

	private Integer color;

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

	public Long getGameId() {
		return gameId;
	}

	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}

	public Integer getCard() {
		return card;
	}

	public void setCard(Integer card) {
		this.card = card;
	}

	public Integer getColor() {
		return color;
	}

	public void setColor(Integer color) {
		this.color = color;
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
