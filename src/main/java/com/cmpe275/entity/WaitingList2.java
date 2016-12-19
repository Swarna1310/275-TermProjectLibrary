package com.cmpe275.entity;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
/**
 * setter and getter methods for the waitinglist2 class
 */
@Entity
@Table(name = "book_waitlist")
public class WaitingList2{
	
	
	@Id
	private int waitid;
	
 
	public String getBookid() {
		return bookid;
	}

	public void setBookid(String bookid) {
		this.bookid = bookid;
	}





	public int getUserid() {
		return userid;
	}





	public void setUserid(int userid) {
		this.userid = userid;
	}



	private String bookid;
	
	private int userid;

	

	  public int getWaitid() {
			return waitid;
		}





		public void setWaitid(int waitid) {
			this.waitid = waitid;
		}






	@Column(name="time_of_request")
	private String requestTime;

    
    public WaitingList2(){}
    
    public WaitingList2(String bookid, int patronid, String date){
    	this.bookid = bookid;
    	this.userid = patronid;
    	this.requestTime = date;
    }
    


 

public String getRequestTime() {
	return requestTime;
}



public void setRequestTime(String requestTime) {
	this.requestTime = requestTime;
}
/*
public Book getBook() {
  return getId().getBook();
 }

 public void setBook(Book book) {
  getId().setBook(book);
 }

 public Patron getPatron() {
  return getId().getPatron();
 }

 public void setPatron(Patron patron) {
  getId().setPatron(patron);
 }
  */
 
}	

	
