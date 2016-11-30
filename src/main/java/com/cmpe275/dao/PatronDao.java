package com.cmpe275.dao;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmpe275.entity.Book;
import com.cmpe275.entity.Patron;




@Repository
public class PatronDao implements IPatronDoa {
	@PersistenceContext
	private EntityManager entitymanager;


	@Transactional
    public int addPatron(Patron patron){
		entitymanager.persist(patron); 
	    entitymanager.flush();
		return patron.getId();
	}
	
	@Transactional
    public Patron getPatron(int id){
		Patron patron = entitymanager.find(Patron.class,id);
		return patron;
	}
	
	@Transactional
	public Book searchBook (String name){
		Book b = new Book();
		return b;
	}
	
	@Transactional
	public int borrowBooks(List<Book> books){
		return 1;
	}
	
	@Transactional
	public int returnBook(Book book,int userid){
		return 1;
	}
	
	@Transactional
	public int renewBook(Book book, int userid){
		return 1;
	}
	
	@Transactional
	public int addWaitlist(Book book,int userid){
		return 1;
	}
	
	
	public EntityManager getEntityManager() {
		return entitymanager;
	}

	public void setEntityManager(EntityManager entitymanager) {
		this.entitymanager = entitymanager;
	}

}
