package ch.uzh.ifi.seal.soprafs19.entity;

import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Card implements Serializable {

    // TODO: Implement Card

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "card_id")
    private Long id;

    @Column(nullable = false) //, unique = true
    private SimpleGodCard cardName;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<Player> players;

    @ManyToOne()
    @JoinColumn(name = "game_id")
    private Game game;

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

    public List<Player> getPlayer() {
        return players;
    }

    public void setPlayer(List<Player> players) {
        this.players = players;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Card() {
    }

    public Card(SimpleGodCard cardName) {
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
