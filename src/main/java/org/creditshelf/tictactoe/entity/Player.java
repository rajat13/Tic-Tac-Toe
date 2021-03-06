package org.creditshelf.tictactoe.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Cacheable
public class Player {
	
	@Id
	@Column(name = "EMAIL", nullable = false)
	private String email;
	
	@Column(name = "NAME", nullable = false)
	private String name;

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}
	
	public Player(String email, String name) {
		super();
		this.email = email;
		this.name = name;
	}

	public Player() {
		super();
	}

	@Override
	public String toString() {
		return "Player [email=" + email + ", name=" + name + "]";
	}

}
