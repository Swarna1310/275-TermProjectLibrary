package com.cmpe275.dao;

import java.util.List;

import com.cmpe275.entity.*;




public interface ILibrarianDoa {
	
	public int addLibrarian(Librarian lib);

	public int addBook(Book book);
	
	public Book searchBook(String name);

	public int updateBook(Book book,String ISBN);

	public int deleteBook(String ISBN);	

}
