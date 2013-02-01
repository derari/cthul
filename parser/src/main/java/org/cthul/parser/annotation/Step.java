package org.cthul.parser.annotation;

import java.lang.annotation.*;

/**
 * Defines a production of a grammar.
 * @author Arian Treffer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Step {

    String value() default "";

    int i() default -1;
        
    boolean all() default false;

    boolean allMatches() default false;
    
}
