package ch.uzh.ifi.seal.soprafs19.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Field implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "field_id")
	private Long id;

	@OneToOne(mappedBy = "field", cascade = CascadeType.ALL)
	private Worker worker;

	@Column(nullable = false)
	private Integer blocks;

	@Column(nullable = false)
	private Boolean hasDome;

	@Column(nullable = false)
	private Integer posX;
	
	@Column(nullable = false)
	private Integer posY;

	@ManyToOne
	@JoinColumn(name = "board_id")
	private Board board;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}

	public Integer getBlocks() {
		return blocks;
	}

	public void setBlocks(Integer blocks) {
		this.blocks = blocks;
	}

	public Boolean getHasDome() {
		return hasDome;
	}

	public void setHasDome(Boolean hasDome) {
		this.hasDome = hasDome;
	}

	public Integer getPos() {
		return posX;
	}

	public void setPos(Integer posX) {
		this.posX = posX;
	}
	
	public Integer getPos() {
		return posY;
	}

	public void setPos(Integer posY) {
		this.posY = posY;
	}

	public Field(Integer pos) {
		this.blocks = 0;
		this.hasDome = false;
		this.pos = pos;
	}

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
