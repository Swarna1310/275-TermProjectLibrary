package com.cmpe275;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class AOPLogging {
	
	/**
	 * Log before entering any function executed by Main controller
	 */
	
	@Before("execution(* com.cmpe275.controller.MainController.*(..))")
	public void logBeforeCall(JoinPoint jp) {
		System.out.println("Controller Log Before executing :" + jp.getSignature().getName() );
	}
	
	
	/**
	 * Log after executing any function in Main Controller
	 */
	
	@AfterReturning("execution(* com.cmpe275.controller.MainController.*(..))") 
	public void logAfterCall(JoinPoint jp) {
		System.out.println("Controller Log After returning successfully executing :" + jp.getSignature().getName() );
	}
	
	
	

}