package ch.uzh.ifi.seal.soprafs19.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Field implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(unique = true)
	private Integer worker;

	@Column(nullable = false)
	private Integer blocks;
	
	@Column(nullable = false)
	private Boolean hasDome;

	@Column(nullable = false)
	private Integer posX;

	@Column(nullable = false)
	private Integer posY;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// TODO: Implement getters and setters

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Field)) {
			return false;
		}
		Field field = (Field) o;
		return this.getId().equals(field.getId());
	}
}
