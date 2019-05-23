package ch.uzh.ifi.seal.soprafs19.entity;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false, unique = true)
	private String token;

	@Column
	@CreatedDate
	private Date creationDate;

	@Column
	private String birthdayDate;

	@Column(nullable = false)
	private UserStatus status;

	private Integer wins = 0;

	private Integer losses = 0;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	//@JsonIgnore
	public String getPassword() {
		return password;
	}

	//@JsonIgnore
	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getBirthdayDate() {
		return birthdayDate;
	}

	public void setBirthdayDate(String birthdayDate) {
		this.birthdayDate = birthdayDate;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public Integer getWins() {
		return wins;
	}

	public void setWins(Integer wins) {
		this.wins = wins;
	}

	public Integer getLosses() {
		return losses;
	}

	public void setLosses(Integer losses) {
		this.losses = losses;
	}



	public void didWin(Boolean won) {
		if (won) {
			setWins(getWins()+1);
		} else {
			setLosses(getLosses()+1);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof User)) {
			return false;
		}
		User user = (User) o;
		return this.getId().equals(user.getId());
	}
}
