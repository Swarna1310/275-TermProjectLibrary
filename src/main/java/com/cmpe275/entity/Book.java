package com.cmpe275.entity;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;



@Table(name = "book")
@Entity
public class Book{
	
	@Id
	@GeneratedValue
	private String ISBN;
	
	private String title;
	private String author;
	private String callnumber;
	private String publisher;
	private String year;
	private String location;
	private int copies;
	
	
	private int addedby;
     
	@OneToMany
    @JoinTable(name="book_waitlist", 
          joinColumns=@JoinColumn(name="bookid"),
          inverseJoinColumns=@JoinColumn(name="userid"))
	private List<Patron> waitlist;
	



	public Book(){}
	
	public Book(String ISBN, String title, String author, String callnumber, String publisher, String year, String location, int copies,int addedby) {
		this.ISBN = ISBN;
		this.title = title;
		this.author = author;
		this.callnumber = callnumber;
		this.publisher = publisher;
		this.year = year;
		this.location = location;
		this.copies = copies;
		this.addedby = addedby;
	}
	
	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String ISBN) {
		this.ISBN = ISBN;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCallnumber() {
		return callnumber;
	}
	public void setCallnumber(String callnumber) {
		this.callnumber = callnumber;
	}
	
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	
	public int getCopies() {
		return copies;
	}
	public void setCopies(int copies) {
		this.copies = copies;
	}
	
	public int getAddedby() {
		return addedby;
	}
	public void setAddedby(int addedby) {
		this.addedby = addedby;
	}


	public List<Patron> getWaitlist() {
		return waitlist;
	}

	public void setWaitlist(List<Patron> waitlist) {
		this.waitlist = waitlist;
	}
	
	
	
	
}
