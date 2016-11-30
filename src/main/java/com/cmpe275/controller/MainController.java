package com.cmpe275.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.cmpe275.dao.*;
import com.cmpe275.entity.*;


@Controller
public class MainController {

	@Autowired
	private IPatronDoa patronDao;
	
	@Autowired
	private ILibrarianDoa librarianDao;
	

	
	
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
		
		   Patron patron = new Patron(name,emailid,password,Integer.parseInt(sjsuid));		  
		   int id = patronDao.addPatron(patron);
		   System.out.println("userid inserted is:"+ id);
		   return new ModelAndView("redirect:/patron/" + id);

	}
	
   
	@RequestMapping(value = "/patron/{id}", method = RequestMethod.GET)
	public @ResponseBody ModelAndView getSpecificUser(@PathVariable("id") String patronId,
			ModelAndView modelAndView) {
		Patron patron = patronDao.getPatron(Integer.parseInt(patronId));

				modelAndView.setViewName("home");
				modelAndView.addObject("userdetail", patron);		

		return modelAndView;
	}

}
