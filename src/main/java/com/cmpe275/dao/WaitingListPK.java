package com.cmpe275.entity;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
/**
 * setter and getter methods for the waitingclassPK
 */

@Embeddable
public class WaitingListPK implements Serializable {
	 private static final long serialVersionUID = 1L;

	 @ManyToOne
	  private Book book;

	  @ManyToOne
	  private Patron patron;

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public Patron getPatron() {
		return patron;
	}

	public void setPatron(Patron patron) {
		this.patron = patron;
	}
	  


}
