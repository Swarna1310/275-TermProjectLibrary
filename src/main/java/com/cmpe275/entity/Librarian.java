package com.cmpe275.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;





@Table(name = "librarian")
@Entity
public class Librarian{
	@Id
	@GeneratedValue
	private int id;
	
	private String name; 
	private String emailid;
	private String password;
	
	 
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmailid() {
		return emailid;
	}
	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public Librarian(String name, String emailid, String password) {
		this.name = name;
		this.emailid = emailid;
		this.password = password;
	}
	
	public Librarian(){
		
	}
}
