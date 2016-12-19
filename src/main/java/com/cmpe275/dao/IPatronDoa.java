package com.cmpe275.dao;

import java.util.List;

import com.cmpe275.entity.*;


public interface IPatronDoa {

	public int addPatron(Patron patron);
	
	public Book searchBookById (String id);
	
	public int checkPatron(String emailid, String password);
	
	public int checkActivated(int activated);
	
	public void updateActivated( Patron patron);
	
	public int checkForSJSUID(int sjsuid);
	
	public void addToCart(String name,int flag,int id);
	
	public void RemoveFromCart(String name,int flag,int id);
	
	public void checkoutBook(String s);
	
	public List<Book> searchBook (String name);
	
	public List<Book> searchBookbyISBN (String name);
	

	public List<Book> returnBook(int userid,String[] books);
	
	public int renewBook(String ISBN, int userid);
	
	public void addWaitlist(String isbn,int userid);
	
    public WaitingList2 addTimeWaitlist(int waitid);
	
	public void RemoveFromWaitlist(int waitid);

	public List<WaitingList2> getWaitinglist(String isbn);
	
	public Patron getPatron(int id);
	
	
	public List<Patron> getAllPatrons();
	
	
	

}
