package ch.uzh.ifi.seal.soprafs19.entity;

import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Card implements Serializable {


	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false, unique = true)
	private SimpleGodCard cardName;

	@ManyToOne
	@JoinColumn(name = "game_id")
	private Game game;

	// TODO: Implement Card

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SimpleGodCard getCardName() {
		return cardName;
	}

	public void setCardName(SimpleGodCard cardName) {
		this.cardName = cardName;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Card)) {
			return false;
		}
		Card card = (Card) o;
		return this.getId().equals(card.getId());
	}
}
