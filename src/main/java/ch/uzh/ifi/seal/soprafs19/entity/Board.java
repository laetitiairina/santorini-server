package ch.uzh.ifi.seal.soprafs19.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Board implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "board_id")
	private Long id;

	@OneToMany(mappedBy = "board")
	@Column(nullable = false)
	private List<Field> fields;

	@OneToOne
	@JoinColumn(name = "game_id")
	private Game game;


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

	public Board(Integer numberOfRows) {

		this.fields = new ArrayList<Field>();

		for (int i = 0; i < numberOfRows; i++) {
			for (int j = 0; j < numberOfRows; j++)
			this.fields.add(new Field(i, i));
		}

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
