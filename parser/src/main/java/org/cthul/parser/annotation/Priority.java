package org.cthul.parser.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the priority of a rule. The value has to be a non-negative integer.
 * <p>
 * If used for tokens, it only affects the order in which the patterns
 * are tested. If used for productions, it may remove possible parse trees.
 * @author Arian Treffer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Priority {

    public static final int Low = org.cthul.parser.Priority.LOW;
	
    public static final int Default = org.cthul.parser.Priority.DEFAULT;
    
    public static final int High = org.cthul.parser.Priority.HIGH;

	public int value();

}
