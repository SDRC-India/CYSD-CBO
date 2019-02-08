/**
 * 
 */
package org.sdrc.cysdcbo.web;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Harsh Pratyush(harsh@sdrc.co.in)
 *
 */

@ControllerAdvice(basePackages="org.sdrc.cysdcbo.web")
public class GlobalAdviceController {

	
	@ExceptionHandler(value=Exception.class)
	public String handleException(Exception e) throws Exception
	{
		if(e instanceof AccessDeniedException)
		{
			throw e;
		}
		else
		{
		e.printStackTrace();
		return "exception";
		}
	}
}
