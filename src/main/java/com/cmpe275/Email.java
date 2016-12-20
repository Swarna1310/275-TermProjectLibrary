package com.cmpe275;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;


public class Email implements Runnable {

	private String email, body, subject;

	
	private MailSender mailOtp;

	public Email(String email, String body, MailSender mailOtp, String subject) {
		this.email = email;
		this.body = body;
		this.subject = subject;
		this.mailOtp=mailOtp;
	}


	@Override
	public void run() {

		SimpleMailMessage verifyMail = new SimpleMailMessage();
		verifyMail.setFrom("project275.group10@gmail.com");
		verifyMail.setTo(email);
		verifyMail.setSubject(subject);
		verifyMail.setText(body);
		mailOtp.send(verifyMail);
		System.out.println("mail sent!");

	}

}