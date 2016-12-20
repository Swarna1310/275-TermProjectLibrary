package com.cmpe275;


import org.springframework.scheduling.annotation.Scheduled;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import com.cmpe275.dao.*;
import com.cmpe275.entity.Book;
import com.cmpe275.entity.Patron;


@Component
public class Scheduler {


	@Autowired
	IPatronDoa patrondao;
	
	@Autowired
	private MailSender mailOtp;

          	        
/**
	 * It is the scheduler which runs daily at 2pm, checking  for books that are 
	 * due within 5 days and send email notificaton to patron
	 * @author swarnav 
	 */

	@Scheduled(cron = "0 0 14 * * *")
	public void scheduleReturn() {
        
		List<Patron> patrons = patrondao.getAllPatrons();
		
		if (patrons != null && !patrons.isEmpty()) {
			for (Patron p : patrons) {

                List<Book> books = p.getBooks();
				if(books != null && !books.isEmpty()){
					System.out.println("Books for user "+p.getName()+ " "+books.size());
					for(Book b : books){
						String bdate = b.getBorrowdate();
						System.out.println("BBorrowDate "+ bdate);
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
						 System.out.println("scheduler-book details: "+ b.getTitle()+" returndate:"+retdate);

					        Date now = new Date(); 
					        long diff =   retur.getTime() - now.getTime();
					        long diffDays = 0;
					        if(diff > 0){
					        	diffDays =  diff / (1000 * 60 * 60 * 24);
					        	long diffhrs = (diff - diffDays * (1000 * 60 * 60 * 24))/(1000 * 60 * 60);
					        	//long diffmin = (diff - diffDays * (1000 * 60 * 60 * 24))/(1000 * 60);
					        	if( diffhrs > 0 )
					        		diffDays += 1;
					        		
					        	if(diffDays < 6 ){
					        	System.out.println("Send mail: return date within"+String.valueOf(diffDays)+" Days");
					        	String subject = "Your Books is Nearing DueDate";
			   
			String body = "Hello "+ p.getName()+"! Your book is nearing due in "+String.valueOf(diffDays)+" days. Please Return it soon or Renew \n\n";
					
					body += "Book Details:\n";
					body += "Title :" + b.getTitle() + " Author: " + b.getAuthor()+ "\n";


			   
			Runnable r = new Email(p.getEmailid(), body, mailOtp, subject) ;
			new Thread(r).start();
					        	}
					        	 
					        }
						}
						catch(Exception e){
							System.out.println("Error:"+e);
						}
					}
				}

			}
		} else {
			System.out.println("its null");
		}
		
	}

	


}