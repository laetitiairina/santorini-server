package ch.uzh.ifi.seal.soprafs19.entity;

import ch.uzh.ifi.seal.soprafs19.constant.Color;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Player implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "player_id")
    private Long id;

    // TODO: Implement User
    //@Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private Boolean isGodMode;

    @Column()
    private SimpleGodCard card;

    @Column()
    private Color color;

	@Column(nullable = false)
	private String token;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "player", cascade = CascadeType.PERSIST)
    @Column(nullable = false)
    private List<Worker> workers;

    // default value is false
    private boolean isCurrentPlayer = false;

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

    public SimpleGodCard getCard() {
        return card;
    }

    public void setCard(SimpleGodCard card) {
        this.card = card;
    }

  	public Color getColor() {
    return color;
  }

  	public void setColor(Color color) {
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

	public Player() {
        this.workers = new ArrayList<>();
        workers.add(new Worker());
        workers.add(new Worker());
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
