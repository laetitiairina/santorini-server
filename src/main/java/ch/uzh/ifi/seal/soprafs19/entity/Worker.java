package ch.uzh.ifi.seal.soprafs19.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Worker implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "worker_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "player_id")
	private Player player;

	@OneToOne(mappedBy = "worker")
	private Field field;

	@Column
	private boolean isCurrentWorker;

	/*@Column
	private int timesBuiltCurrentTurn;

	@Column
	@OneToOne
	private Field lastBuildLocation;*/

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

	public Boolean getIsCurrentWorker() {return isCurrentWorker;}

	public void setIsCurrentWorker(Boolean isCurrentWorker) {this.isCurrentWorker = isCurrentWorker;}

	/*public int getTimesBuiltCurrentTurn() {
		return this.timesBuiltCurrentTurn;
	}

	public void setTimesBuiltCurrentTurn(int timesBuiltCurrentTurn) {
		this.timesBuiltCurrentTurn = timesBuiltCurrentTurn;
	}

	public int incrementTimesBuiltCurrentTurn() {
		this.timesBuiltCurrentTurn++;
		return this.timesBuiltCurrentTurn;
	}

	public Field getLastBuildLocation() {
		return lastBuildLocation;
	}

	public void setLastBuildLocation(Field lastBuildLocation) {
		this.lastBuildLocation = lastBuildLocation;
	}*/

	public Worker() {}

	public Worker(Player player) {
		this.player = player;
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
