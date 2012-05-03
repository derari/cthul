package org.cthul.parser.annotation;

import java.lang.annotation.*;

/**
 *
 * @author Arian Treffer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Arg {
    
    public int value();
    
}
