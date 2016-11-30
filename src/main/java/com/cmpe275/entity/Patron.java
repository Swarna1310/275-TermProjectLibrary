package com.cmpe275.entity;
import java.util.List;



import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;



@Table(name = "patron")
@Entity
public class Patron{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String name;
	private String emailid;
	private String password;
	private int sjsuid;
	

	
	
	@OneToMany
    @JoinTable(name="patron_books", 
          joinColumns=@JoinColumn(name="userid"),
          inverseJoinColumns=@JoinColumn(name="bookid"))
    private List<Book> books;


	public Patron(){}
	
	public Patron(String name, String emailid, String password, int sjsuid) {
		this.name = name;
		this.emailid = emailid;
		this.password = password;
		this.sjsuid = sjsuid;
	}
	
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
	
	public int getSjsuid() {
		return sjsuid;
	}
	public void setSjsuid(int sjsuid) {
		this.sjsuid = sjsuid;
	}


	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}
	
	
	
	
}
