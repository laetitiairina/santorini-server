package ch.uzh.ifi.seal.soprafs19.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Board implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@OneToMany
	@Column(nullable = false)
	private List<Field> fields;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}


	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Board)) {
			return false;
		}
		Board board = (Board) o;
		return this.getId().equals(board.getId());
	}
}
