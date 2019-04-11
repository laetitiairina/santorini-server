package ch.uzh.ifi.seal.soprafs19.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Worker implements Serializable {


	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "board_id")
	private Board board;

	// TODO: Implement Worker

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
