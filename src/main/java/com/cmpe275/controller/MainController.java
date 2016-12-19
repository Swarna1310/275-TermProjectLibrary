package com.cmpe275.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.gson.JsonElement; 
import com.google.gson.JsonObject; 
import com.google.gson.JsonParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.taglibs.standard.lang.jstl.parser.ParseException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.cmpe275.PasswordEncrypt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cmpe275.Email;
import com.cmpe275.dao.*;
import com.cmpe275.entity.*;


/**
 * @author swarnav
 *
 */

@Controller
public class MainController {

	@Autowired
	private IPatronDoa patronDao;
	
	@Autowired
	private ILibrarianDoa librarianDao;
	
	@Autowired
	private MailSender mailOtp;

	int sessionID =0 ;
	
	static String regex = "^[A-Za-z0-9+_.-]+@sjsu.edu";
	
	/**
	 * Gets signup  html
	 * @param modelAndView
	 * @return modelAndView
	 */	
	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public ModelAndView getUser(ModelAndView modelAndView) {     
		   modelAndView.setViewName("signup");
		return modelAndView;
    
	}
	

	/**
	 * @param request
	 * @param modelandView
	 * @return
	 * signin page when the user logs in intially
	 */
	@RequestMapping(value="/signin",method = RequestMethod.GET)
	public ModelAndView getuser(HttpServletRequest request,ModelAndView modelandView){
		
		HttpSession session = request.getSession(false);

		if (session != null && session.getAttribute("username") != null) {
			System.out.println("the session is" + session.getId());
			int userid = (Integer)session.getAttribute("userID");
			Patron patron = patronDao.getPatron(userid);
		    if(patron.getRole().equals("patron"))
		    return new ModelAndView("redirect:/books/" + userid);
		    else{
		    	return new ModelAndView("redirect:/booksearch");
		    }
		}
		else{
		modelandView.setViewName("signin");	
		modelandView.addObject("msg","");
		return modelandView ;
		}
		
	}
	
	
	/**
	 * @param request
	 * @param response
	 * @param emailid
	 * @param password
	 * @param modelAndView
	 * @return
	 * signin page code when the user enters the password and username
	 */
	@RequestMapping(value="/signin", method = RequestMethod.POST)
	public ModelAndView getUser(HttpServletRequest request,HttpServletResponse response,@RequestParam(value = "emailid", required = false) String emailid,
			@RequestParam(value = "password", required = false) String password, ModelAndView modelAndView){
		
		
		 String newPassword =""; 
	       
	       try {
	    	    newPassword = PasswordEncrypt.encrypt(password);
	    	    System.out.println(newPassword);
	       }
	       catch(Exception ex){
	    	   System.out.println(ex.getMessage());
	       }
		
	       int id = patronDao.checkPatron(emailid, newPassword);
		
	       if(id != 0){  
			Patron patron = patronDao.getPatron(id);			
			
			if(patron.getActivated() == 1 ){
				
				ModelAndView modelandView = new ModelAndView() ;	
				HttpSession session = request.getSession();

				session.setAttribute("userID", patron.getId());
				session.setAttribute("username", patron.getName());
				session.setAttribute("role", patron.getRole());

				
				
				System.out.println("Role   :"+ patron.getRole());
				System.out.println("Activated :"+ patron.getActivated());
				
				modelandView.addObject("userid",session.getAttribute("userID"));
				modelandView.addObject("username",session.getAttribute("username"));
				if(patron.getRole().equalsIgnoreCase("patron")){
					System.out.println("This goes to patron page");
					
					modelandView.setViewName("redirect:/books/" + id);
					return modelandView;
					
				}
				else{
					System.out.println("This goes to librarian");
					modelandView.setViewName("booksearch");
					return modelandView;
					
				}
				
			}
			else{    
				
				return new ModelAndView("redirect:/activation/");
			}
			
						
		}
		else{     
			return new ModelAndView("signin","msg","Wrong credentials");
			
		}		
		
		
	}
	
	//Ash Start - for activation management
	/**
	 * @param modelandView
	 * @return
	 * page when the user firsts signs up and is required to enter the activation code
	 */
	@RequestMapping(value="/activation",method = RequestMethod.GET)
	public ModelAndView getActivation(ModelAndView modelandView){
		modelandView.setViewName("activation");
		
		modelandView.addObject("msg", "Please Enter the Activation Code");		
		return modelandView ;
	}
	
	
	
	/**
	 * @param activationcode
	 * @param modelAndView
	 * @return
	 * activation page code when the user enters the activation code, 
	 * checking of the entered code with the database is done here
	 */
	@RequestMapping(value="/activation", method = RequestMethod.POST)
	public  ModelAndView getActivation(@RequestParam(value = "activationcode", required = false) int activationcode,
			ModelAndView modelAndView){
		
		int code = activationcode;
		int id = patronDao.checkActivated(code);
		System.out.println("Prere "+ id);
		if(id != 0 ){    
			Patron patron = patronDao.getPatron(id);
			
			
			if(patron.getActivated() == activationcode){ //Check if the user has entered the correct activation code
				
				
				patron.setActivated(1);
				patronDao.updateActivated(patron);
				
				String subject = "Activation Successful";
				   
				String body = "You have been successfully activated ";
				   
				Runnable r = new Email(patron.getEmailid(), body, mailOtp, subject) ;
				new Thread(r).start();
				
				return new ModelAndView("redirect:/signin");
			}
			else{  
				ModelAndView model = new ModelAndView("redirect:/activation");
				
				model.addObject("msg","Activation code is wrong");
				return model ;
			}
			
			
		}
	
		ModelAndView model = new ModelAndView("redirect:/activation");
		
		model.addObject("msg", "Activation code does not exist");
		return model ;
		
	}
	
	
	@RequestMapping(value= "/duplicateSJSU/{id}", method = RequestMethod.GET)
	public @ResponseBody ModelAndView checkDuplicateSJSU(@PathVariable("id") int id, ModelAndView modelAndView){
		
		ModelAndView model = new ModelAndView("duplicateSJSU");
		model.addObject("id","SJSUID " +id +" is already used for registration. Please sign using your credentials");
		return model ;
		
	}
	
	
	/**
	 * creates the user with details using POST
	 * @param name
	 * @param emailid
	 * @param password
	 * @param sjsuid
	 * @param modelAndView
	 * @return modelAndView 
	 */	
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
		public ModelAndView addUser(@RequestParam(value = "name", required = false) String name,
				@RequestParam(value = "emailid", required = false) String emailid,
				@RequestParam(value = "password", required = false) String password,
				@RequestParam(value = "sjsuid", required = false) String sjsuid, ModelAndView modelAndView) {
		
		  	   
		   int SJSUID =  patronDao.checkForSJSUID(Integer.parseInt(sjsuid));    
		   
		   if(SJSUID != 0){
			   ModelAndView model = new ModelAndView("redirect:/duplicateSJSU/"+sjsuid);
			   return model ;			   
			   
		   }
		   
		  
	       int activation = 1 ;
	      
	       Random rnd = new Random() ;
	       activation  = 100000 + rnd.nextInt(100000);
	       
	             
	       String role = "patron" ;
	       Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		   Matcher matcher = pattern.matcher(emailid);
	       
	       if(matcher.matches()){
	    	   role = "librarian";
	       }
	       //Password Encryption
	       String newPassword1 ="";
	       
	       try {
	    	    newPassword1 = PasswordEncrypt.encrypt(password);
	    	    System.out.println("the passsssword isss"+newPassword1);
	       }
	       catch(Exception ex){
	    	   System.out.println(ex.getMessage());
	       }
	       
		   Patron patron = new Patron(name,emailid,newPassword1,Integer.parseInt(sjsuid),activation,role);		  
		   int id = patronDao.addPatron(patron);
		   
		   System.out.println("userid inserted is:"+ id);
		   
		  
		   String subject = "Activation Code";
		   
		   String body = "Please use this activation code " + activation ;
		   
		   Runnable r = new Email(patron.getEmailid(), body, mailOtp, subject) ;
		   new Thread(r).start();
		   return new ModelAndView("redirect:/activation") ;
		   
		  
		   

	}
	
	//Ashwini changes end
	
	
	
   //Sneha Changes start
	
	/**
	 * @param request
	 * @param response
	 * @param author
	 * @param title
	 * @param callnumber
	 * @param publisher
	 * @param year
	 * @param modelAndView
	 * @return
	 * page when the patron does a book search
	 */
	@RequestMapping(value = "/booksearch", method = RequestMethod.GET)
	public ModelAndView getBookSearch(HttpServletRequest request,HttpServletResponse response,@RequestParam(value = "author", required = false) String author,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "callnumber", required = false) String callnumber,
			@RequestParam(value = "publisher", required = false) String publisher,
			@RequestParam(value = "year", required = false) String year,ModelAndView modelAndView) {   

	    HttpSession session = request.getSession();
	    if(session != null && session.getAttribute("userID") != null && session.getAttribute("role").equals("librarian")){
		String ISBN = null;
		Book book = new Book(ISBN,title,author,callnumber,publisher,year,null,0); 
		modelAndView.setViewName("booksearch");
		List<Book> books  = librarianDao.searchBookbyCriteria(book);

		modelAndView.addObject("books", books);
		modelAndView.addObject("author",author);
		modelAndView.addObject("title",title);
		modelAndView.addObject("callno",callnumber);
		modelAndView.addObject("publ",publisher);
		modelAndView.addObject("year",year);
		
	    }
	    else{
	    	modelAndView.setViewName("error");
			modelAndView.addObject("status","403");
			modelAndView.addObject("msg","Sorry, Login as Librarian to view this page!");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	    	
	    }
	 return modelAndView;
	}

	
	
	/**
	 * @param modelAndView
	 * @return
	 * page code when the book main page is displayed
	 */
	@RequestMapping(value = "/book", method = RequestMethod.GET)
	public ModelAndView getBook(ModelAndView modelAndView,HttpServletRequest request,HttpServletResponse response) {  
		HttpSession session = request.getSession();
		if(session != null && session.getAttribute("userID") != null && session.getAttribute("role").equals("librarian")){
		   modelAndView.setViewName("book");
		return modelAndView;
		}
		else{
			modelAndView.setViewName("error");
			modelAndView.addObject("status","403");
			modelAndView.addObject("msg","Sorry, Login as Librarian to view this page!");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return modelAndView;
		}
    
	}
	
	
	
	/**
	 * @param isbn
	 * @param author
	 * @param title
	 * @param callnumber
	 * @param publisher
	 * @param year
	 * @param location
	 * @param modelAndView
	 * @return
	 * when a book details are entered and submitted
	 */
	@RequestMapping(value = "/book", method = RequestMethod.POST)
	public ModelAndView addBook(@RequestParam(value = "ISBN", required = false) String isbn,
			@RequestParam(value = "author", required = false) String author,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "callnumber", required = false) String callnumber,
			@RequestParam(value = "publisher", required = false) String publisher,
			@RequestParam(value = "year", required = false) String year,
			@RequestParam(value = "location", required = false) String location,
            ModelAndView modelAndView,HttpServletRequest request,HttpServletResponse response) {
		
		HttpSession session = request.getSession();
		if(session != null && session.getAttribute("userID") != null && session.getAttribute("role").equals("librarian")){
			
        int addedBy = (Integer)session.getAttribute("userID");

        String ISBN = "";
        if(isbn.equals("") || isbn == null){
        long number = (long)(Math.random() * 100000000 * 1000000); 
  
        	ISBN  =	Long.toString(number);
        }
        else{
        	ISBN = isbn;
        }
		Book book = new Book(ISBN,title,author,callnumber,publisher,year,location,addedBy);
		String bookid = librarianDao.addBook(book);
		System.out.println("bookid inserted is:" + bookid);
		
		return new ModelAndView("redirect:/book/" + ISBN);
		}
		else{
			modelAndView.setViewName("error");
			modelAndView.addObject("status","403");
			modelAndView.addObject("msg","Sorry, Login as Librarian to view this page!");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return modelAndView;
		}


	}
	
	
	
	/**
	 * @param ISBN
	 * @param modelAndView
	 * @return
	 * page for book search by the ISBN
	 */
	@RequestMapping(value = "/book/{ISBN}", method = RequestMethod.GET)
	public @ResponseBody ModelAndView getSpecificBook(HttpServletRequest request,HttpServletResponse response,@PathVariable("ISBN") String ISBN,
			ModelAndView modelAndView) {
		Book book = librarianDao.searchBook(ISBN);
		if(book == null){
			modelAndView.setViewName("error");
			modelAndView.addObject("status","404");
			modelAndView.addObject("msg","Sorry, the requested book with ID: "+ISBN+" does not exist");
		    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return modelAndView;
		}
		else{
			
				modelAndView.setViewName("bookUpdate");
				modelAndView.addObject("bookdetail", book);		
	    }
		return modelAndView;
	}
	
	
	

	/**
	 * @param ISBN
	 * @param author
	 * @param title
	 * @param callnumber
	 * @param publisher
	 * @param year
	 * @param location
	 * @param modelAndView
	 * @return
	 * get the book details when the ISBN number is entered
	 */
	@RequestMapping(value = "/book/{ISBN}", method = RequestMethod.POST)		
	public ModelAndView updateBook(@PathVariable("ISBN") String ISBN,
			@RequestParam(value = "author", required = false) String author,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "callnumber", required = false) String callnumber,
			@RequestParam(value = "publisher", required = false) String publisher,
			@RequestParam(value = "year", required = false) String year,
			@RequestParam(value = "location", required = false) String location,
			ModelAndView modelAndView) {
		     int addedBy = 1;
		     Book book = new Book(ISBN,title,author,callnumber,publisher,year,location,addedBy); //flag passed as 0
		     int updateResult = librarianDao.updateBook(book, ISBN);
		     
		     return new ModelAndView("redirect:/booksearch");

	}
	
	
	
	/**
	 * @param ISBN
	 * @param modelAndView
	 * @return
	 * code for the page when we want to delete the book based on the ISBN number
	 */
	@RequestMapping(value = { "/booksearch/{ISBN}"}, method = RequestMethod.DELETE)
	public @ResponseBody String deleteBook(HttpServletRequest request,HttpServletResponse response,@PathVariable("ISBN") String ISBN,
			ModelAndView modelAndView) {
        System.out.println("deleting user:"+ISBN);
        try{
		int book = librarianDao.deleteBook(ISBN);
		if(book == 0){
            modelAndView.setViewName("error");
            modelAndView.addObject("status","404");
			modelAndView.addObject("msg","Sorry, the requested user with ID: "+ISBN+" does not exist");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			String error = "<h1>ERROR 404 </h1><br><br> Sorry, the requested book with ID: "+ISBN+" does not exist";
			return error;
		}
			return "success";
        }
        catch(Exception e){
        e.printStackTrace();
           return "error";
        }


	}
	
  //Sneha Changes end
	
	
 //Neha Changes start
	
	/**
	 * @param patronId
	 * @param bookName
	 * @param modelAndView
	 * @param request
	 * @param response
	 * @return
	 * get the book by id page
	 */
	@RequestMapping(value = "/books/{id}", method = RequestMethod.GET)
	public @ResponseBody ModelAndView getBoooks(@PathVariable("id") int patronId,@RequestParam (value="searchbox",required=false) String bookName,	
			ModelAndView modelAndView,HttpServletRequest request, HttpServletResponse response) {
		int count =0;
		 if (request.getParameterMap().containsKey("searchbox")==false) {
			 bookName = "";
		 }
		 HttpSession session = request.getSession();
		 List<Book> AllBookList = patronDao.searchBook(bookName);
		 
		 Patron p = patronDao.getPatron(patronId);
		 if(session != null && session.getAttribute("userID") != null){
			 if((Integer)session.getAttribute("userID") == patronId && session.getAttribute("role").equals("patron")){
		 List<Book> PatronBooks = p.getBooks();
		 List<Book> searchBookList = new ArrayList<Book>();
		 List<Book> AddedToWaitList = new ArrayList<Book>();
		 List<Book> ReservedList = new ArrayList<Book>();
		 for(Book b : AllBookList){
			 
			 int found = 0;
		List<WaitingList2> waitlist = patronDao.getWaitinglist(b.getISBN());
			 if (waitlist.size()>0){
				
					for(WaitingList2 w1 : waitlist){
						if(w1.getUserid() == patronId){
							AddedToWaitList.add(b);
							found=1;
						}
						else if(w1.getUserid() != patronId && !w1.getRequestTime().equals("") && b.getFlag() == 0){
							found=1;  
						}
						
					}
				 }
			 List<Patron> pat = b.getPatrons();
				for(Patron pt : pat){
					if(pt.getId() != patronId && b.getFlag() ==1)   
					found=1;
				}
			
				for(Book bp : PatronBooks){
					if(b.getISBN().equals(bp.getISBN()) && bp.getFlag() !=1){   //check out 
						found =1; 
						
					}
				}
				if (found==0){
					
					searchBookList.add(b);	
				}
				
				
				 List<WaitingList2> w = patronDao.getWaitinglist(b.getISBN());           
					int priority=1000;
					Patron pp = new Patron();
					
					for (WaitingList2 w1 : w){
						
						if (priority > w1.getWaitid()) 					
						{
							priority =  w1.getWaitid();
							pp= patronDao.getPatron(w1.getUserid()); 
							

						}								

					}
				     if (pp.getId()==patronId && b.getFlag()==0){
				    	 WaitingList2 wt = patronDao.addTimeWaitlist(priority);
				    	 AddedToWaitList.remove(b);
				    	 ReservedList.add(b);
				    	
				    	 Date now = new Date();
				    	 DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm");
				    	 try{
				    	if( request.getAttribute("currentdate") != null && !session.getAttribute("currentdate").equals("")){
				    		 now = df.parse(String.valueOf(session.getAttribute("currentdate")));
		                 }
				    	Date waitreq = df.parse(wt.getRequestTime());
					        
					        long diff =  now.getTime() - waitreq.getTime();
					   System.out.println("diff btn"+now.getTime()+"-"+waitreq.getTime()+String.valueOf(diff));
					        long diffDays = 0;
					        if(diff > 0){
					        	diffDays = diff / (1000 * 60 * 60 * 24);
					        	long diffhrs = (diff - diffDays * (1000 * 60 * 60 * 24))/(1000 * 60 * 60);
					        	
					        	if( diffhrs > 0 )
					        		diffDays += 1;
				    	 
					        }
					        if(diffDays > 3){
					        	patronDao.RemoveFromWaitlist(priority);
						    	 ReservedList.remove(b);
					        }
				    	 }
					        catch(Exception e){
					        	
					        }
				    	 
				     }
					        
				
			}
		 
		 		
		

		 
		 
		 
		 List <Book> finalSearchbList = new ArrayList<Book>();
		for(Book b : searchBookList){
			 int addTolist =0;
			List<Patron> pat = b.getPatrons();
			for(Patron pt : pat){
					if(  pt.getId() != patronId){
						addTolist =1;
							}
					else
					addTolist =0;
					if(addTolist==0)
						finalSearchbList.add(b);
					if(b.getFlag()==1)
						count++;
			
				}
			
			}
				modelAndView.setViewName("BookSelection");
				modelAndView.addObject("list", searchBookList);	
				modelAndView.addObject("AddedToWaitList", AddedToWaitList);
				modelAndView.addObject("ReservedList", ReservedList);	
				modelAndView.addObject("searchbox", bookName);	
				modelAndView.addObject("count", count);	
				modelAndView.addObject("username",String.valueOf(session.getAttribute("username")));
				if(session.getAttribute("uidate") != null)
					modelAndView.addObject("curdate", String.valueOf(session.getAttribute("uidate")));
			 }
			 else{
				     modelAndView.setViewName("error");
				     modelAndView.addObject("status","403");
					modelAndView.addObject("msg", "Please login as correct patron to checkout books.");	
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			 }
			 }
			 else{
				     modelAndView.setViewName("error");
				     modelAndView.addObject("status","401");
					modelAndView.addObject("msg", "Unauthorized.Please login to checkout books.");	
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			 }	 
			 
		return modelAndView;
	}

	/**
	 * sendmail
	 */
public void SendMail(Book b){
	 
	
	 List<WaitingList2> w = patronDao.getWaitinglist(b.getISBN());  
	 int priority=1000;
	 
		for (WaitingList2 w1 : w){
			if (priority < w1.getWaitid())
			{
				priority =  w1.getWaitid();
				Patron pp = patronDao.getPatron(w1.getUserid()); 
			}
			
		}
	 
	}
	

	
	
	/**
	 * @param patronId
	 * @param count
	 * @param modelAndView
	 * @return
	 * code for the book check out based on the id given
	 */
	@RequestMapping(value = "/checkout/{id}/{count}", method = RequestMethod.POST)
	public @ResponseBody ModelAndView getCheckoutPage(@PathVariable("id") int patronId,@PathVariable("count") int count,
			ModelAndView modelAndView,HttpServletRequest request, HttpServletResponse response) {
				String s="";
				Patron p = patronDao.getPatron(patronId);
				if (count>5){
					modelAndView.setViewName("error");
					modelAndView.addObject("status","400");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					modelAndView.addObject("msg", "Cant checkout more than five books in one transaction.");	
				}
				else if(p.getBooks().size()>10){
					modelAndView.setViewName("error");
					modelAndView.addObject("status","400");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				modelAndView.addObject("msg", "You cannot have more than 10 books. Please return/remove book from cart. ");	
				}
				else{
					List<Book> patronBook= p.getBooks();
					List<Book> checkoutBooks = new ArrayList<Book>();
					String subject = "Borrowed Books Details";
					String body = "Hello "+ p .getName()+"! You have successfully borrowed the following books\n\n";
					for(Book b  : patronBook){
						if ( b.getFlag() ==1){
							patronDao.checkoutBook(b.getISBN());
							checkoutBooks.add(b);
							
							SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm");
							

							Calendar c = Calendar.getInstance();
							Date now = c.getTime();
					        c.add(Calendar.DATE, 30);
					        Date ret = c.getTime();
					        String date = df.format(now);
					        String retdate = df.format(ret);
				            
							body += "\n\nTitle :" + b.getTitle() + "\nAuthor : " + b.getAuthor()+ "\nBorrow Date :" 
									+date+"\nReturn Date :"+retdate;
						}
					}
					
					Runnable r = new Email(p.getEmailid(), body, mailOtp, subject) ;
					new Thread(r).start();
					
					modelAndView.addObject("list", checkoutBooks);	
					modelAndView.addObject("id",patronId);
					modelAndView.addObject("msg", "The following books have been checked out : " );	
					modelAndView.setViewName("Checkout");
					}
		return modelAndView;
	}
	

	/**
	 * @param patronId
	 * @param bookISBN
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value = "/books/bookcart/{id}/{isbn}", method = RequestMethod.GET)
	public String addToCart(@PathVariable("id") int patronId ,@PathVariable("isbn") String bookISBN,
			ModelAndView modelAndView) {
					patronDao.addToCart(bookISBN, 1,patronId);
				return "redirect:/books/" + patronId;
	}
	
	
	/**
	 * @param patronId
	 * @param bookISBN
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value = "/books/RemoveBookcart/{id}/{isbn}", method = RequestMethod.GET)
	public String RemoveFromCart(@PathVariable("id") int patronId ,@PathVariable("isbn") String bookISBN,
			ModelAndView modelAndView) {
				patronDao.RemoveFromCart(bookISBN, 0,patronId);
				return "redirect:/books/" + patronId;
	}

	
	
// Neha changes end	
	
	
	//Swarna Changes start
	
	/**
	 * @param patronId
	 * @param bookISBN
	 * @param modelAndView
	 * @return
	 * code to add books to the waiting list
	 */
	@RequestMapping(value = "/books/addToWaiting/{id}/{isbn}", method = RequestMethod.GET)
	public String addToWaitinglist(@PathVariable("id") int patronId ,@PathVariable("isbn") String bookISBN,
			ModelAndView modelAndView) {
				patronDao.addWaitlist(bookISBN,patronId);
				return "redirect:/books/" + patronId;
	}
	
	
	/**
	 * @param patronId
	 * @param modelAndView
	 * @return
	 * getting the patron page by id
	 */
	@RequestMapping(value = "/patron/{id}", method = RequestMethod.GET)
	public @ResponseBody ModelAndView getSpecificUser(@PathVariable("id") String patronId,
			ModelAndView modelAndView) {
		Patron patron = patronDao.getPatron(Integer.parseInt(patronId));
		
		System.out.println("patron books:"+patron.getBooks().size());
		
		if(patron.getBooks().size() > 0){
			for(int i=0;i<patron.getBooks().size();i++){
				System.out.println("patron books:"+patron.getBooks().get(i).getTitle()+":"+ patron.getBooks().get(i).getBorrowdate());
			}
		}
		
		return modelAndView;
	}

	
	/**
	 * @param patronId
	 * @param modelAndView
	 * @return
	 * the books borrowed page code by id
	 */
	@RequestMapping(value = "/return/{id}", method = RequestMethod.GET)
	public @ResponseBody ModelAndView getBooksBorrowed(@PathVariable("id") String patronId,
			ModelAndView modelAndView,HttpServletRequest request, HttpServletResponse response){
		
		Patron patron = patronDao.getPatron(Integer.parseInt(patronId));
		
			HttpSession session = request.getSession();
		
		System.out.println("patron books:"+patron.getBooks().size());
		List<Book> pbooks = new ArrayList<Book>(patron.getBooks());

		
		if(pbooks.size() > 0){
			for(Book b : pbooks){
				System.out.println("patron books:"+b.getTitle()+":"+ b.getBorrowdate());
				
				String bdate = b.getBorrowdate();
				if(b.getRenewdate() != null && !b.getRenewdate().equals("")){
					bdate = b.getRenewdate();
				}
				DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm");
				try{
				 Date d = df.parse(bdate);
				 Calendar c = Calendar.getInstance(); 
				 c.setTime(d); 
				 c.add(Calendar.DATE, 30);
				 Date retur = c.getTime();
				 String retdate= df.format(retur);
				 System.out.println("returndate:"+retdate);

                 b.setReturndate(retdate);
                 Date now = new Date(); 
                 if(session != null && session.getAttribute("currentdate") != null && !session.getAttribute("currentdate").equals("")){
                	 System.out.println("coming inside");
                	 now = df.parse(String.valueOf(session.getAttribute("currentdate")));
                 }
			        
			        long diff =  now.getTime() - retur.getTime();
			   System.out.println("diff btn"+now.getTime()+"-"+retur.getTime()+String.valueOf(diff));
			        long diffDays = 0;
			        if(diff > 0){
			        	diffDays = diff / (1000 * 60 * 60 * 24);
			        	long diffhrs = (diff - diffDays * (1000 * 60 * 60 * 24))/(1000 * 60 * 60);
			        	long diffmin = (diff - diffhrs * (1000 * 60 * 60))/(1000 * 60);
			        	if( diffhrs > 0 )
			        		diffDays += 1;
			        	if( diffmin > 0 )  {
			        		System.out.println("minutes diff"+String.valueOf(diffmin));
			        	}
			        	System.out.println("diffDays"+String.valueOf(diffDays)+" diff hours: "+String.valueOf(diffhrs));
			        	 b.setOverdue((int)diffDays); 
			        }
			       
				}
				catch(Exception e){
					 System.out.println(e);
				}
			}
		}
		       
		        patron.setBooks(pbooks);
				modelAndView.setViewName("return");
				modelAndView.addObject("patron", patron);
				if(session.getAttribute("uidate") != null)
				modelAndView.addObject("curdate", String.valueOf(session.getAttribute("uidate")));
				else
					modelAndView.addObject("curdate","");
		return modelAndView;
	}
	
	
	
	/**
	 * @param patronId
	 * @param checkboxValue
	 * @param modelAndView
	 * @return
	 * returning the books based on the given id
	 */
	@RequestMapping(value = "/return/{id}", method = RequestMethod.POST)
	public @ResponseBody ModelAndView returnBooks(@PathVariable("id") String patronId,
			@RequestParam(value = "booklist",required = false)String[] checkboxValue,
			ModelAndView modelAndView,HttpServletRequest request, HttpServletResponse response) {
		if(checkboxValue == null){
			modelAndView.setViewName("error");
			modelAndView.addObject("status","400");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			modelAndView.addObject("msg", "No books have been selected.Select books to return");	
		}
		else{
		List<Book> returnlist = patronDao.returnBook(Integer.parseInt(patronId), checkboxValue);
		Patron patron = patronDao.getPatron(Integer.parseInt(patronId));
		
		if(returnlist != null ){
			String subject = "Returned Books Details";
			   
			String body = "Hello "+ patron.getName()+"! You have successfully returned the following books\n\n";
			for(Book b  : returnlist){
					body += "Title :" + b.getTitle() + " Author: " + b.getAuthor()+ "\n";
				}

			   
			Runnable r = new Email(patron.getEmailid(), body, mailOtp, subject) ;
			new Thread(r).start();
			modelAndView.setViewName("redirect:/return/"+patronId);
			
		}
		else{
			modelAndView.setViewName("error");
			modelAndView.addObject("status","400");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			modelAndView.addObject("msg", "You cannot Return more than ten books at a time");	
		}
		
	}
         return modelAndView;

	}
	
	
	
	/**
	 * @param ISBN
	 * @param userid
	 * @param modelAndView
	 * @return
	 * renew the books based on the ISBN number and user id
	 */
	@RequestMapping(value = { "/renew/{ISBN}/{userid}"}, method = RequestMethod.GET)
	public @ResponseBody String renewBook(@PathVariable("ISBN") String ISBN,
			@PathVariable("userid") String userid,ModelAndView modelAndView) {
        System.out.println("renew book:"+ISBN);
        try{
		int status = patronDao.renewBook(ISBN,Integer.parseInt(userid));
		if(status == 1){
			return "success";
		}
			return "error";

        }
        catch(Exception e){
        e.printStackTrace();
           return "error";
        }


	}
	
	/**
	 * save date
	 */
	@RequestMapping(value = { "/saveDate/{date}"}, method = RequestMethod.GET)
	public @ResponseBody String saveDate(HttpServletRequest request,@PathVariable("date") String date,
			ModelAndView modelAndView) {
       HttpSession session = request.getSession();
       System.out.println("date param:"+date);
		if(session != null){
       Date d = null;
       String curtime="";
       DateFormat df = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm");
		try{
		 d = df.parse(date);
		 curtime = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(d);
		 session.setAttribute("uidate",date);
		 session.setAttribute("currentdate",curtime);
		}
		catch(Exception e){
			
		}
			System.out.println("date passed:"+curtime);
			
		}
		return "success";

	}

	/**
	 * @param request
	 * @param modelAndView
	 * @return
	 * logout page when the user logs out
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logOut(HttpServletRequest request,ModelAndView modelAndView) {
		 		HttpSession session=request.getSession();  
		 		System.out.println("user logging out:"+session.getAttribute("username"));
		 		session.invalidate();  

				modelAndView.setViewName("signin");	
				modelAndView.addObject("msg","");
				return modelAndView ;
	}
	
	
	
	/**
	 * @param ISBN
	 * @param modelAndView
	 * @return
	 * @throws IOException
	 * code for fetching the book by its ISBN
	 */
	@RequestMapping(value = "/fetch/{ISBN}", method = RequestMethod.GET)
	public @ResponseBody String getBookByISBN(@PathVariable("ISBN") String ISBN,
			ModelAndView modelAndView) throws IOException{
	
			ModelMap map = new ModelMap();
			ObjectMapper mapper = new ObjectMapper();
			String bookUrl = "https://www.googleapis.com/books/v1/volumes?q=isbn:"+ ISBN ;
			URL url = new URL(bookUrl);
			
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			request.connect();
	
			JsonParser parser = new com.google.gson.JsonParser();
			JsonElement element = parser.parse(new InputStreamReader((InputStream)request.getContent()));
			JsonObject object = element.getAsJsonObject();
				System.out.println("fetched details"+object.toString());
				String jsonString =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString("hh");
				Map<String, String> model = new HashMap<String, String>();
				
				model.put("book",object.toString());
	return object.toString();
	}

      
}
