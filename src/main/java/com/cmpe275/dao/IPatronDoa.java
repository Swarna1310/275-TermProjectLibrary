package com.cmpe275.dao;

import java.util.List;

import com.cmpe275.entity.*;


public interface IPatronDoa {

	public int addPatron(Patron patron);
	

	public Book searchBook (String name);
	
	
	public int borrowBooks(List<Book> books);
	

	public int returnBook(Book book,int userid);
	
	public int renewBook(Book book, int userid);
	
	public int addWaitlist(Book book,int userid);
	
	public Patron getPatron(int id);
	

}
