package ch.uzh.ifi.seal.soprafs19.entity;

import ch.uzh.ifi.seal.soprafs19.constant.Color;
import ch.uzh.ifi.seal.soprafs19.constant.SimpleGodCard;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Player implements Serializable {


    private static final long serialVersionUID = 1L;

    public Player() {
        this.workers = new ArrayList<>();
        workers.add(new Worker(this));
        workers.add(new Worker(this));

        this.lastMoveMillis = System.currentTimeMillis();
        this.lastPollMillis = System.currentTimeMillis();
        this.isActive = true;
    }

    @Id
    @GeneratedValue
    @Column(name = "player_id")
    private Long id;

    //@Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private Boolean isGodMode;

    @Column(nullable = false)
    private Long lastMoveMillis;

    @Column(nullable = false)
    private Long lastPollMillis;

    @Column(nullable = false)
    private Boolean isActive;

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

    @Column()
    private String username;

    // default value is false
    private boolean isCurrentPlayer = false;

    private boolean wantsRematch = false;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // Leave this commented out, used for calculating remaining move time frontend
    //@JsonIgnore
    public Long getLastMoveMillis() {
        return lastMoveMillis;
    }

    //@JsonIgnore
    public void setLastMoveMillis(Long lastMoveMillis) {
        this.lastMoveMillis = lastMoveMillis;
    }

    @JsonIgnore
    public Long getLastPollMillis() {
        return lastPollMillis;
    }

    @JsonIgnore
    public void setLastPollMillis(Long lastPollMillis) {
        this.lastPollMillis = lastPollMillis;
    }

    public Boolean getWantsRematch() { return wantsRematch; }

    public void setWantsRematch(boolean wantsRematch) { this.wantsRematch = wantsRematch; }



    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Player)) {
            return false;
        }
        Player player = (Player) o;
        return this.getId().equals(player.getId());
    }

    public void didMove() {
        setLastMoveMillis(System.currentTimeMillis());
    }

    public Long lastMove() {
        return System.currentTimeMillis() - getLastMoveMillis();
    }

    public void didPoll() {
        setLastPollMillis(System.currentTimeMillis());
    }

    public Long lastPoll() {
        return System.currentTimeMillis() - getLastPollMillis();
    }

    public Worker getCurrentWorker() {
        List<Worker> currentWorkers = getWorkers().stream().filter(worker -> worker.getIsCurrentWorker()).collect(Collectors.toList());
        if (currentWorkers.size() == 1){
            return currentWorkers.get(0);
        }
        return null;
    }
}
