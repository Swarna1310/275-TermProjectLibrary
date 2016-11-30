package com.cmpe275.dao;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import com.cmpe275.entity.Book;
import com.cmpe275.entity.Librarian;



@Repository
public class LibrarianDao implements ILibrarianDoa{
	@PersistenceContext
	private EntityManager entitymanager;
	
	@Transactional
	public int addLibrarian(Librarian lib){
		return 1;
	}

	@Transactional
	public int addBook(Book book){
		return 1;
	}
	
	@Transactional
	public Book searchBook(String name){
		Book b = new Book();
		return b;
	}

	@Transactional
	public int updateBook(Book book,String ISBN){
		return 1;
	}

	@Transactional
	public int deleteBook(String ISBN){
		return 1;
	}


	public EntityManager getEntityManager() {
		return entitymanager;
	}

	public void setEntityManager(EntityManager entitymanager) {
		this.entitymanager = entitymanager;
	}
	
}
