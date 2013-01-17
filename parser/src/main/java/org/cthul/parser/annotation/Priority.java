package org.cthul.parser.annotation;

import java.lang.annotation.*;

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

    public static final int Low = 0x1000;
	
    public static final int Default = 0x2000;
    
    public static final int High = 0x3000;

    public int value();

}
