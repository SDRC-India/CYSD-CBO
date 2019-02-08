package org.sdrc.cysdcbo.core;
/**
 * @author Harsh(harsh@sdrc.in)
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {
	
	public String feature() default "default" ;
	public String permission() default "default" ;

}
