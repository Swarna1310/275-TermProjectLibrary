package com.cmpe275.dao;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmpe275.entity.Book;
import com.cmpe275.entity.Patron;
import com.cmpe275.entity.WaitingList2;





@Repository
public class PatronDao implements IPatronDoa {
	@PersistenceContext
	private EntityManager entitymanager;

	/**
	 * method to add a patron given the required details
	 */
	@Transactional
    public int addPatron(Patron patron){
		entitymanager.persist(patron); 
	    entitymanager.flush();
		return patron.getId();
	}
	
	/**
	 *method to get a patron by id
	 */
	@Transactional
    public Patron getPatron(int id){
		Patron patron = entitymanager.find(Patron.class,id);
		return patron;
	}
	
	/**
	 * method to search a book by its ID
	 */
	@Transactional
	public Book searchBookById (String id){
		Book b  = entitymanager.find(Book.class,id);  
		return b;
	}
	
	
	//Ashwini changes start
	
	/**
	 * method to check a patron based on the emailid and the password entered
	 */
	@Transactional
    public int checkPatron(String emailid, String password){
		
		try{
			Query query =
			      entitymanager.createQuery("FROM Patron P where P.emailid =:emailid and P.password=:password and P.activated=1", Patron.class);
		 	  query.setParameter("emailid", emailid);
		 	  query.setParameter("password", password);
		 	  System.out.println(emailid+ password);
			  List<Patron> patron  =  query.getResultList();
			  
			  if(patron.size() > 0 ){    
				  return patron.get(0).getId() ;				  
			  }
			  else{   
				  return 0 ;
			  }
			  
		}		
		catch(NoResultException nre){
			return 0 ;
			
		}		
		
	}
	
	/**
	 * method to check if the user is activated and return the values accordingly
	 */
	@Transactional
	public int checkActivated(int activated){
		try{
			Query query =
			      entitymanager.createQuery("FROM Patron P where P.activated =:activated", Patron.class);
		 	  query.setParameter("activated", activated);		 	  
		 	  
			  List<Patron> patron  =  query.getResultList();
			  if(patron.size() > 0 ){    
				  return patron.get(0).getId() ;				  
			  }
			  else{   
				  return 0 ;
			  }
			  
		}		
		catch(NoResultException nre){
			return 0 ;
			
		}
		
	}
	
	/**
	 * method to update the activated field when the correct activation code is entered
	 */
	@Transactional
	public void updateActivated( Patron patron){
		
		entitymanager.merge(patron);
		
	}
	
	/**
	 * method to check for the unique SJSUid
	 */
	@Transactional
	public int checkForSJSUID(int sjsuid){
		
		try{
				Query query =
				      entitymanager.createQuery("FROM Patron P where P.sjsuid =:sjsuid", Patron.class);
			 	  		query.setParameter("sjsuid", sjsuid);
			 	List<Patron> patron  =  query.getResultList();
			 	if(patron.size() > 0 ){     //SJSU ID is already used 
					  return patron.get(0).getId() ;				  
				  }
				  else{   //
					  return 0 ;
				 }
			
		}
		catch(NoResultException nre){
			return 0 ;
			
		}
	}
	
	//Ashwini changes end
	
	
	//Neha changes start
	
	/**
	 * method to search book
	 */
	@Transactional
	public List<Book> searchBook(String name){	 
		Query q = entitymanager.createQuery("SELECT p from Book p WHERE p.title like :arg1 or p.author like :arg1");
		q.setParameter("arg1", "%"+name+"%");
		List<Book> Books = q.getResultList();
		return Books;
	}
	
	/**
	 * method to search a book by its ISBN
	 */
	@Transactional
	public List<Book> searchBookbyISBN(String name){	 
		Query q = entitymanager.createQuery("SELECT p from Book p WHERE p.ISBN = :arg1");
		q.setParameter("arg1", name);
		List<Book> Books = q.getResultList();
		return Books;
	}
	
	/**
	 * method to add a book to the cart
	 */
	@Transactional
	public void addToCart(String name,int flag,int id){	
		Book b = entitymanager.find(Book.class,name);
		Patron p = entitymanager.find(Patron.class,id);
		b.setFlag(flag);
	    List<Book> booklist = p.getBooks();	
		
		booklist.add(b);
		p.setBooks(booklist);
		Query q = entitymanager.createQuery("SELECT w from WaitingList2 w WHERE w.bookid = :arg1 and w.userid = :arg2");
		q.setParameter("arg1", name);
		q.setParameter("arg2", id);
		try{
		WaitingList2 waiting =(WaitingList2)q.getSingleResult();

		if(waiting != null){
			entitymanager.remove(waiting);
		}
		}
		catch(Exception e){

		}
	
	}
	
	/**
	 * method to remove a book from the cart
	 */
	@Transactional
	public void RemoveFromCart(String name,int flag,int id){	
		Book b = entitymanager.find(Book.class,name);
		Patron p = entitymanager.find(Patron.class,id);
		b.setFlag(flag);
		List<Book> booklist = p.getBooks();		
		booklist.remove(b);
		p.setBooks(booklist);
	}
	
	/**
	 * method to checkout a book
	 */
	@Transactional
	public void checkoutBook(String s){	
		Book b = entitymanager.find(Book.class,s);
		b.setFlag(2);
		String timeStamp = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(new Date());
		b.setBorrowdate(timeStamp);
		entitymanager.merge(b);
	}
	
	//Neha end
	
	
	
	
	//Swarna Changes Start
	
	/**
	 * method to return a book
	 */
	@Transactional
	public List<Book> returnBook(int userid,String[] books){
		Patron patron = entitymanager.find(Patron.class,userid);
		List<Book> patronbooks = patron.getBooks();
		List<Book> returnbooks = new ArrayList<Book>();
		if(books.length > 10 ){
			return null;
		}
		else{
		for(int i=0;i<books.length;i++)
		{
			Book b = searchBookById(books[i]);
		
		  if(patronbooks.contains(b)){
				patronbooks.remove(b);
				returnbooks.add(b);
			}	
			patron.setBooks(patronbooks);
			entitymanager.merge(patron);
			b.setFlag(0);
			b.setIsrenew(0);
			b.setBorrowdate("");
			entitymanager.merge(b);
		}
		return returnbooks;
		}
	}
	
	/**
	 * method to renew a book
	 */
	@Transactional
	public int renewBook(String ISBN, int userid){
		Book book = entitymanager.find(Book.class,ISBN);
		if(getWaitinglist(ISBN).size() > 0){
			return 0;
		}
		else{
		if(book.getIsrenew() == 0)
			book.setIsrenew(1);
		else if(book.getIsrenew() == 1)
			book.setIsrenew(2);
		String timeStamp = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(new Date());
		book.setRenewdate(timeStamp);
		return 1;
		}
	}
	
	/**
	 * method to get the list of all the patrons
	 */
	@Transactional
	public List<Patron> getAllPatrons(){
		List<Patron> patrons = new ArrayList<Patron>();
		 Query query = entitymanager.createQuery("SELECT p FROM Patron p where role='patron'");    
		    try{
		    patrons = query.getResultList();
		    return patrons;
		    }
		    catch(Exception e){
		    	return patrons;
		    }

	}
	
	/**
	 * method to add the waitlist
	 */
	@Transactional
	public void addWaitlist(String isbn,int userid){
		Patron patron = entitymanager.find(Patron.class,userid);
		Book book = searchBookById(isbn);
		String timeStamp = ""; // new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(new Date());

		
			WaitingList2 w = new WaitingList2(isbn,userid,timeStamp);
	
	}
	
	/**
	 * method to add the timewaitlist
	 */
	@Transactional
	public WaitingList2 addTimeWaitlist(int waitid){
		WaitingList2 w = entitymanager.find(WaitingList2.class,waitid);
		String timeStamp =  new SimpleDateFormat("MM-dd-yyyy HH:mm").format(new Date());
		w.setRequestTime(timeStamp);
		entitymanager.merge(w);
		return w;
	};
	
	/**
	 * method to remove from the waiting list
	 */
	@Transactional
	public void RemoveFromWaitlist(int waitid){
		WaitingList2 w = entitymanager.find(WaitingList2.class,waitid);
		entitymanager.remove(w);
	};
	
	/**
	 * method to get the waiting list
	 */
	@Transactional
	public List<WaitingList2> getWaitinglist(String isbn){
		List<WaitingList2> waiting = new ArrayList<WaitingList2>();
		Query q = entitymanager.createQuery("SELECT w from WaitingList2 w WHERE w.bookid = :arg1");
		q.setParameter("arg1", isbn);
		try{
		waiting = q.getResultList();
         return waiting;
		}
		catch(Exception e){
			 return waiting;
		}
	}
	
	/**
	 *method to get the entitymanager
	 */ 
	public EntityManager getEntityManager() {
		return entitymanager;
	}

	
	/**
	 * method to set the entity manager
	 */
	public void setEntityManager(EntityManager entitymanager) {
		this.entitymanager = entitymanager;
	}

}
