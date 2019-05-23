package ch.uzh.ifi.seal.soprafs19.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Field implements Serializable {

	private static final long serialVersionUID = 1L;

	public Field () {}

	public Field(Board board, Integer posX, Integer posY) {
		this.board = board;
		this.blocks = 0;
		this.hasDome = false;
		this.posX = posX;
		this.posY = posY;
	}

	@Id
	@GeneratedValue
	@Column(name = "field_id")
	private Long id;

	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinTable(name = "field_worker", joinColumns = {@JoinColumn(name = "field_id", referencedColumnName = "field_id")},inverseJoinColumns = {@JoinColumn(name = "worker_id", referencedColumnName = "worker_id")})
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

		// Set reference to field in worker if not null
		if (worker != null) {
			worker.setField(this);
		}
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

	public Integer getPosX() {
		return posX;
	}

	public void setPosX(Integer posX) {
		this.posX = posX;
	}
	
	public Integer getPosY() {
		return posY;
	}

	public void setPosY(Integer posY) {
		this.posY = posY;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Field)) {
			return false;
		}
		Field field = (Field) o;
		return this.getId().equals(field.getId());
	}
}
