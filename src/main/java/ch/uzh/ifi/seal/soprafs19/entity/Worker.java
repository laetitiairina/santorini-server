package ch.uzh.ifi.seal.soprafs19.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Worker implements Serializable {

	private static final long serialVersionUID = 1L;


	// TODO: ID is null!
	@Id
	@GeneratedValue
	@Column(name = "worker_id")
	private Long id;

	@ManyToOne()
	@JoinColumn(name = "player_id")
	private Player player;

	// TODO: field is null
	@OneToOne
	@JoinColumn(name="field_id")
	private Field field;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	public Player getPlayer() {
		return player;
	}

	@JsonIgnore
	public void setPlayer(Player player) {
		this.player = player;
	}

	@JsonIgnore
	public Field getField() {
		return field;
	}

	@JsonIgnore
	public void setField(Field field) {
		this.field = field;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Worker)) {
			return false;
		}
		Worker worker = (Worker) o;
		return this.getId().equals(worker.getId());
	}
}
